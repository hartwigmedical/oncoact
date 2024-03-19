package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class ObservedVariant {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String position();

    @NotNull
    public abstract String variant();

    @NotNull
    public abstract ReadDepth readDepth();

    @NotNull
    public abstract String copies();

    @NotNull
    public abstract String tVaf();

    @NotNull
    public abstract String biallelic();

    @NotNull
    public abstract Hotspot hotspot();

    @NotNull
    public abstract DriverInterpretation driver();

    public abstract boolean hasNotifiableGermlineVariant();

    public abstract boolean hasPhasedVariant();
}