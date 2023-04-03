package com.hartwig.oncoact.graph;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface StageScheduler {
    CompletableFuture<Boolean> schedule(Stage stage);
}
