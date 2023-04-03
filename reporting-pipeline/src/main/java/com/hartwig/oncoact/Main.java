package com.hartwig.oncoact;

import com.hartwig.oncoact.graph.ExecutionGraph;
import com.hartwig.oncoact.pipeline.Arguments;
import com.hartwig.oncoact.pipeline.stage.Stages;

public class Main {

    public static void main(String[] args) {
        var arguments = Arguments.builder().patientId("id").primaryTumorDoids("126").build();
        var executionGraph = new ExecutionGraph(Stages.getStages(arguments));
        executionGraph.start(null);
    }
}
