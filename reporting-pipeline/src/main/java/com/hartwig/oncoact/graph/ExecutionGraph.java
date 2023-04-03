package com.hartwig.oncoact.graph;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
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

    public void start(StageScheduler stageScheduler) {
        var g = createGraph();

    }

    DefaultDirectedGraph<String, DefaultEdge> createGraph() {
        var g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        for (final Resource input : pipeline.inputs()) {
            g.addVertex(input.name());
        }
        for (final Resource output : pipeline.outputs()) {
            g.addVertex(output.name());
        }
        for (final Stage stage : pipeline.stages()) {
            g.addVertex(stage.name());
            for (final Resource input : stage.inputs()) {
                g.addVertex(input.name());
                g.addEdge(input.name(), stage.name());
            }
            for (final Resource output : stage.outputs()) {
                g.addVertex(output.name());
                g.addEdge(stage.name(), output.name());
            }
        }
        LOGGER.info("Completed execution graph. Looks like: {}", exportAsDot(g));
        return g;
    }

    private String exportAsDot(DefaultDirectedGraph<String, DefaultEdge> g) {
        var exporter = new DOTExporter<String, DefaultEdge>();
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v));
            return map;
        });
        var writer = new StringWriter();
        exporter.exportGraph(g, writer);
        return writer.toString();
    }
}
