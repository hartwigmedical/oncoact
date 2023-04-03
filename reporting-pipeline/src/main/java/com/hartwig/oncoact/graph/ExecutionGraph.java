package com.hartwig.oncoact.graph;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionGraph {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionGraph.class);

    private final Pipeline pipeline;

    public ExecutionGraph(final Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void start(ExecutorService executorService, StageScheduler stageScheduler) throws ExecutionException, InterruptedException {
        var g = createGraph();
        new ExecutionGraphRun(g, executorService, stageScheduler).start().get();
    }

    DefaultDirectedGraph<Stage, NamedEdge> createGraph() {
        var g = new DefaultDirectedGraph<Stage, NamedEdge>(NamedEdge.class);

        for (final Stage stage : pipeline.stages()) {
            g.addVertex(stage);
        }
        for (final Stage from : pipeline.stages()) {
            for (final Stage to : pipeline.stages()) {
                to.inputs()
                        .stream()
                        .map(Resource::name)
                        .filter(input -> from.outputs().stream().map(Resource::name).anyMatch(output -> output.equals(input)))
                        .forEach(edge -> g.addEdge(from, to, new NamedEdge(edge)));
            }
        }
        LOGGER.info("Created execution graph. Looks like: {}", exportAsDot(g));
        return g;
    }

    private static String exportAsDot(DefaultDirectedGraph<Stage, NamedEdge> g) {
        var exporter = new DOTExporter<Stage, NamedEdge>();
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.name()));
            return map;
        });
        exporter.setEdgeAttributeProvider((e) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(e.name()));
            return map;
        });
        var writer = new StringWriter();
        exporter.exportGraph(g, writer);
        return writer.toString();
    }

    private static class ExecutionGraphRun {
        private final Set<String> runningStageTags = new HashSet<>();
        private final BlockingQueue<Pair<Stage, Boolean>> stageDoneQueue = new LinkedBlockingQueue<>();

        private final DefaultDirectedGraph<Stage, NamedEdge> runGraph;
        private final ExecutorService executorService;
        private final StageScheduler stageScheduler;

        public ExecutionGraphRun(final DefaultDirectedGraph<Stage, NamedEdge> runGraph, final ExecutorService executorService,
                final StageScheduler stageScheduler) {
            this.runGraph = runGraph;
            this.executorService = executorService;
            this.stageScheduler = stageScheduler;
        }

        Future<?> start() {
            return this.executorService.submit(() -> {
                while (!runGraph.vertexSet().isEmpty()) {
                    runRound();
                    try {
                        var done = stageDoneQueue.take();
                        onStageDone(done.getLeft(), done.getRight());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                LOGGER.info("Finished running execution graph.");
            });
        }

        private void runRound() {
            var readyStages = runGraph.vertexSet().stream()
                    .filter(stage -> runGraph.incomingEdgesOf(stage).size() == 0)
                    .filter(stage -> !runningStageTags.contains(stage.name()))
                    .collect(Collectors.toList());
            for (Stage stage : readyStages) {
                LOGGER.info("Starting graph vertex {}", stage.name());
                var tag = stage.name();
                runningStageTags.add(tag);
                stageScheduler.schedule(stage)
                        .thenAccept(result -> stageDoneQueue.add(Pair.of(stage, result)));
            }
        }

        private void onStageDone(Stage stage, boolean result) {
            LOGGER.info("Finished running stage {}, result was {}", stage.name(), result ? "Success" : "Fail");
            runningStageTags.remove(stage.name());
            runGraph.removeVertex(stage);
        }
    }
}
