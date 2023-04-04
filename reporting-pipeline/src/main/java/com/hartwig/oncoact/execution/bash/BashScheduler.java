package com.hartwig.oncoact.execution.bash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.hartwig.oncoact.graph.IOResource;
import com.hartwig.oncoact.graph.IOResourcePair;
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
    public CompletableFuture<Boolean> schedule(final Stage stage, final List<IOResourcePair> resourcePairs) {
        var schedulerDetails = stage.executionDetails()
                .stream()
                .filter(details -> details.type() == SchedulerDetails.SchedulerDetailType.BASH)
                .findFirst();
        if (schedulerDetails.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        var details = (BashDetails) schedulerDetails.get();
        var command = details.command().asBash();
        try {
            for (final IOResourcePair resourcePair : resourcePairs) {
                var source = Path.of(resourcePair.output().location());
                var target = Path.of(resourcePair.input().location());
                copyDirectory(source, target);
            }
            for (final IOResource output : stage.outputs()) {
                LOGGER.info("Creating output resource directory for {}", output.location());
                Files.createDirectories(Path.of(output.location()));
            }
            return runStage(stage, command);
        } catch (IOException e) {
            LOGGER.warn("Stage {} run failed with exception {}", stage.name(), e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    private CompletableFuture<Boolean> runStage(final Stage stage, final String command) throws IOException {
        File commandFile = File.createTempFile(stage.name(), ".sh");
        try (var stream = new FileOutputStream(commandFile)) {
            stream.write(command.getBytes(StandardCharsets.UTF_8));
            stream.flush();
            LOGGER.info("Written temp file {}", commandFile.getAbsolutePath());
        }
        var process = Runtime.getRuntime().exec(new String[]{"bash", commandFile.getAbsolutePath()});
        LOGGER.info("Command: {}", command);
        var future = new CompletableFuture<Boolean>();
        executorService.submit(() -> {
            try {
                var exitCode = process.waitFor();
                future.complete(exitCode == 0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                LOGGER.info("Deleting temp file {}", commandFile.getAbsolutePath());
                commandFile.delete();
            }
        });
        executorService.submit(() -> tryLog(process.getInputStream(), stage.name() + "-info"));
        executorService.submit(() -> tryLog(process.getErrorStream(), stage.name() + "-error"));
        return future;
    }

    private static void copyDirectory(Path source, Path target) throws IOException {
        if (!Files.exists(source) || !Files.isDirectory(source)) {
            throw new IOException("Source directory does not exist or is not a directory.");
        }

        if (source.equals(target)) {
            return;
        }

        LOGGER.info("Copying resource directory from {} to {}", source, target);

        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(source)) {
            for (Path file : stream) {
                Path targetFile = target.resolve(file.getFileName());
                if (Files.isDirectory(file)) {
                    copyDirectory(file, targetFile);
                } else {
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static void tryLog(InputStream inputStream, String process) {
        try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.lines().forEach(line -> LOGGER.info("{}: {}", process, line));
        } catch (IOException e) {
            LOGGER.warn("Could not read InputStream for {}", process);
        }
    }
}
