package com.hartwig.oncoact.pipeline;

import org.immutables.value.Value;

@Value.Immutable
public interface Arguments {

    String patientId();

    String primaryTumorDoids();

    String orangePath();

    static ImmutableArguments.Builder builder() {
        return ImmutableArguments.builder();
    }
}
