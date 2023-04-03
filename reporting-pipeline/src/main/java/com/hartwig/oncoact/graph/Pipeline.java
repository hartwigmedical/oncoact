package com.hartwig.oncoact.graph;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public interface Pipeline {
    List<Stage> stages();

    static ImmutablePipeline.Builder builder() {
        return ImmutablePipeline.builder();
    }
}
