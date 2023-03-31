package com.hartwig.oncoact.graph;

import java.util.Collection;

import org.immutables.value.Value;

@Value.Immutable
public interface FileResource extends Resource {

    Collection<ExecutionDetails> executionDetails();

    static ImmutableFileResource.Builder builder() {
        return ImmutableFileResource.builder();
    }
}
