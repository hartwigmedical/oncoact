package com.hartwig.oncoact;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import com.hartwig.oncoact.execution.bash.BashScheduler;
import com.hartwig.oncoact.graph.ExecutionGraph;
import com.hartwig.oncoact.pipeline.Arguments;
import com.hartwig.oncoact.pipeline.stage.Stages;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var arguments = Arguments.builder()
                .patientId("id")
                .primaryTumorDoids("126")
                .orangePath("/Users/matthijsvanniekerk/tmp/orange-output/COLO829v003T.orange.json")
                .build();
        var executorService = Executors.newCachedThreadPool();
        var bashScheduler = new BashScheduler(executorService);
        var executionGraph = new ExecutionGraph(Stages.getStages(arguments));
        executionGraph.start(executorService, bashScheduler).get();
        executorService.shutdown();
    }
}
