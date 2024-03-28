package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class ObservedGainsLosses {

    @NotNull
    public abstract String chromosome();

    @NotNull
    public abstract String region();

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract ObservedGainsLossesType type();

    public abstract String minCopies();

    public abstract String maxCopies();

    public abstract String chromosomeArmCopies();

    public static ImmutableObservedGainsLosses.Builder builder() {
        return ImmutableObservedGainsLosses.builder();
    }
}