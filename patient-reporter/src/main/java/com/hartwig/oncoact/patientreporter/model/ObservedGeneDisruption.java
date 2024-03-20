package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class ObservedGeneDisruption {

    @NotNull
    public abstract String location();

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String disruptedRange();

    @NotNull
    public abstract String disruptionType();

    @NotNull
    public abstract String clusterId();

    public abstract int disruptedCopies();

    public abstract int undisruptedCopies();
}
