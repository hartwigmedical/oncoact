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

    public abstract int minCopies();

    public abstract int maxCopies();

    public abstract int chromosomeArmCopies();

}
