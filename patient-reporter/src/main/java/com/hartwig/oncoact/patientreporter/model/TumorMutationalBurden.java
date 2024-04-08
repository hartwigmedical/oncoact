package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class TumorMutationalBurden {

    public abstract double value();

    @NotNull
    public abstract TumorMutationalStatus status();

    @NotNull
    public abstract String label();

    public static ImmutableTumorMutationalBurden.Builder builder() {
        return ImmutableTumorMutationalBurden.builder();
    }
}
