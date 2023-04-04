package com.hartwig.oncoact.graph;

import org.immutables.value.Value;

@Value.Immutable
public interface IOResourcePair {
    IOResource output();
    IOResource input();

    static ImmutableIOResourcePair.Builder builder() {
        return ImmutableIOResourcePair.builder();
    }
}
