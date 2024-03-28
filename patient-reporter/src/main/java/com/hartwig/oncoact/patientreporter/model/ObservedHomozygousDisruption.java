package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class ObservedHomozygousDisruption {

    @NotNull
    public abstract String chromosome();

    @NotNull
    public abstract String region();

    @NotNull
    public abstract String gene();

    public static ImmutableObservedHomozygousDisruption.Builder builder() {
        return ImmutableObservedHomozygousDisruption.builder();
    }
}