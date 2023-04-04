package com.hartwig.oncoact.graph;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface StageScheduler {
    CompletableFuture<Boolean> schedule(Stage stage, List<IOResourcePair> resourcePairs);
}
