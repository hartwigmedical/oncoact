package com.hartwig.oncoact.graph;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionGraph {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionGraph.class);

    private final Pipeline pipeline;

    private ExecutionGraphRun run;

    public ExecutionGraph(final Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public Future<?> start(ExecutorService executorService, StageScheduler stageScheduler) {
        if (run != null) {
            LOGGER.warn("Run was already started. Cannot start a new run for this execution graph.");
            return CompletableFuture.completedFuture(null);
        }
        run = new ExecutionGraphRun(createGraph(), executorService, stageScheduler);
        return run.start();
    }

    private DefaultDirectedGraph<Stage, NamedEdge> createGraph() {
        var g = new DefaultDirectedGraph<Stage, NamedEdge>(NamedEdge.class);

        for (final Stage stage : pipeline.stages()) {
            g.addVertex(stage);
        }
        for (final Stage from : pipeline.stages()) {
            for (final Stage to : pipeline.stages()) {
                to.inputs().stream()
                        .map(IOResource::name)
                        .filter(input -> from.outputs().stream().map(IOResource::name).anyMatch(output -> output.equals(input)))
                        .forEach(edge -> g.addEdge(from, to, new NamedEdge(edge)));
            }
        }
        return g;
    }

    private enum StageRunningState {
        WAITING("black"),
        RUNNING("orange"),
        SUCCESS("green"),
        FAILED("red"),
        IGNORED("grey");

        final String color;

        StageRunningState(final String color) {
            this.color = color;
        }
    }

    private static class ExecutionGraphRun {
        private final Map<String, StageRunningState> stageTagToRunningState = new HashMap<>();
        private final BlockingQueue<Pair<Stage, Boolean>> stageDoneQueue = new LinkedBlockingQueue<>();
        private final DefaultDirectedGraph<Stage, NamedEdge> fullGraph;
        private final DefaultDirectedGraph<Stage, NamedEdge> runGraph;
        private final ExecutorService executorService;
        private final StageScheduler stageScheduler;

        public ExecutionGraphRun(final DefaultDirectedGraph<Stage, NamedEdge> g, final ExecutorService executorService,
                final StageScheduler stageScheduler) {
            fullGraph = g;
            runGraph = (DefaultDirectedGraph<Stage, NamedEdge>) g.clone();
            for (final Stage stage : g.vertexSet()) {
                stageTagToRunningState.put(stage.name(), StageRunningState.WAITING);
            }
            this.executorService = executorService;
            this.stageScheduler = stageScheduler;
        }

        Future<?> start() {
            return this.executorService.submit(() -> {
                LOGGER.info("Starting execution graph. Looks like: {}", toDotFormat());
                while (!runGraph.vertexSet().isEmpty()) {
                    runRound();
                    try {
                        var done = stageDoneQueue.take();
                        onStageDone(done.getLeft(), done.getRight());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                LOGGER.info("Finished running execution graph. Final result: {}", toDotFormat());
            });
        }

        private void runRound() {
            var readyStages = runGraph.vertexSet().stream()
                    .filter(stage -> runGraph.incomingEdgesOf(stage).size() == 0)
                    .filter(stage -> stageTagToRunningState.get(stage.name()) == StageRunningState.WAITING)
                    .collect(Collectors.toList());
            for (Stage stage : readyStages) {
                LOGGER.info("Starting graph vertex {}", stage.name());
                stageTagToRunningState.put(stage.name(), StageRunningState.RUNNING);
                var outputResources = fullGraph.incomingEdgesOf(stage)
                        .stream()
                        .map(fullGraph::getEdgeSource)
                        .flatMap(s -> s.outputs().stream())
                        .collect(Collectors.toList());
                var outputInputResources = new ArrayList<IOResourcePair>();
                for (final IOResource input : stage.inputs()) {
                    var matchingOutput = outputResources.stream()
                            .filter(resource -> resource.name().equals(input.name()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException(
                                    "Input " + input.name() + " could not be matched to a previous stage output."));
                    outputInputResources.add(IOResourcePair.builder().input(input).output(matchingOutput).build());
                }
                stageScheduler.schedule(stage, outputInputResources).thenAccept(result -> stageDoneQueue.add(Pair.of(stage, result)));
            }
            LOGGER.info("Execution graph updated: {}", toDotFormat());
        }

        private void onStageDone(Stage stage, boolean success) {
            LOGGER.info("Finished running stage {}, result was {}", stage.name(), success ? "Success" : "Fail");
            if (!success) {
                var iterator = new DepthFirstIterator<>(runGraph, stage);
                var ignoredStages = new ArrayList<Stage>();
                while (iterator.hasNext()) {
                    ignoredStages.add(iterator.next());
                }
                runGraph.removeAllVertices(ignoredStages);
                for (Stage ignored : ignoredStages) {
                    stageTagToRunningState.put(ignored.name(), StageRunningState.IGNORED);
                }
                stageTagToRunningState.put(stage.name(), StageRunningState.FAILED);
            } else {
                runGraph.removeVertex(stage);
                stageTagToRunningState.put(stage.name(), StageRunningState.SUCCESS);
            }
        }

        public String toDotFormat() {
            var exporter = new DOTExporter<Stage, NamedEdge>();
            exporter.setVertexAttributeProvider((v) -> {
                Map<String, Attribute> map = new LinkedHashMap<>();
                var name = v.name();
                map.put("label", DefaultAttribute.createAttribute(name));
                map.put("color", DefaultAttribute.createAttribute(stageTagToRunningState.get(name).color));
                return map;
            });
            exporter.setEdgeAttributeProvider((e) -> {
                Map<String, Attribute> map = new LinkedHashMap<>();
                map.put("label", DefaultAttribute.createAttribute(e.name()));
                return map;
            });
            var writer = new StringWriter();
            exporter.exportGraph(fullGraph, writer);
            return writer.toString();
        }
    }
}
