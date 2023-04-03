package com.hartwig.oncoact.graph;

import java.util.Collection;

import org.immutables.value.Value;

@Value.Immutable
public interface FileResource extends Resource {

    Collection<SchedulerDetails> executionDetails();

    String path();

    static ImmutableFileResource.Builder builder() {
        return ImmutableFileResource.builder();
    }
}
