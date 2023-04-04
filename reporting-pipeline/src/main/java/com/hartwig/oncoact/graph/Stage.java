package com.hartwig.oncoact.graph;

import java.util.Collection;

import org.immutables.value.Value;

@Value.Immutable
public interface Stage {
    String name();

    Collection<IOResource> inputs();

    Collection<IOResource> outputs();

    Collection<SchedulerDetails> executionDetails();

    static ImmutableStage.Builder builder() {
        return ImmutableStage.builder();
    }
}
