package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class WgsReport implements WgsPatientReport {

    @NotNull
    public abstract String reportDate();

    @NotNull
    public abstract String receiver();

    @NotNull
    public abstract Summary summary();

    @NotNull
    public abstract TumorSample tumorSample();

    @NotNull
    public abstract Sample referenceSample();

    @NotNull
    public abstract Genomic genomic();

    @NotNull
    public abstract Therapy therapy();

    @NotNull
    public abstract Version version();

    public static ImmutableWgsReport.Builder builder() {
        return ImmutableWgsReport.builder();
    }
}