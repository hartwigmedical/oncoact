package com.hartwig.oncoact.graph;

import org.immutables.value.Value;

@Value.Immutable
public interface IOResource {

    String name();

    String location();

    static ImmutableIOResource.Builder builder() {
        return ImmutableIOResource.builder();
    }
}
