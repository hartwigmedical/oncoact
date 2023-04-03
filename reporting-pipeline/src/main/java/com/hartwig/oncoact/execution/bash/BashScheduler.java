package com.hartwig.oncoact.execution.bash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.hartwig.oncoact.graph.SchedulerDetails;
import com.hartwig.oncoact.graph.Stage;
import com.hartwig.oncoact.graph.StageScheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BashScheduler implements StageScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BashScheduler.class);

    private final ExecutorService executorService;

    public BashScheduler(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public CompletableFuture<Boolean> schedule(final Stage stage) throws IOException {
        var schedulerDetails = stage.executionDetails()
                .stream()
                .filter(details -> details.type() == SchedulerDetails.SchedulerDetailType.BASH)
                .findFirst();
        if (schedulerDetails.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        var details = (BashDetails) schedulerDetails.get();
        var command = details.command().asBash();
        var process = Runtime.getRuntime().exec(command);
        LOGGER.info("Command: {}", command);
        var future = new CompletableFuture<Boolean>();
        executorService.submit(() -> {
            try {
                var exitCode = process.waitFor();
                future.complete(exitCode == 0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        executorService.submit(() -> tryLog(process.getInputStream(), stage.name() + "-info"));
        executorService.submit(() -> tryLog(process.getErrorStream(), stage.name() + "-error"));
        return future;
    }

    private static void tryLog(InputStream inputStream, String process) {
        try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.lines().forEach(line -> LOGGER.info("{}: {}", process, line));
        } catch (IOException e) {
            LOGGER.warn("Could not read InputStream for {}", process);
        }
    }
}
