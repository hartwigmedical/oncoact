package com.hartwig.oncoact.graph;

import org.immutables.value.Value;

@Value.Immutable
public interface ParameterResource extends Resource {
    String value();

    static ImmutableParameterResource.Builder builder() {
        return ImmutableParameterResource.builder();
    }
}
