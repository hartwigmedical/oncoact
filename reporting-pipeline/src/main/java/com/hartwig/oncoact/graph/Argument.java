package com.hartwig.oncoact.graph;

import org.immutables.value.Value;

@Value.Immutable
public interface Argument {
    String key();

    String value();

    static ImmutableArgument.Builder builder() {
        return ImmutableArgument.builder();
    }
}
