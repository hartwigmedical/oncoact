package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class WgsReport {

    @NotNull
    public abstract LocalDate reportDate();

    @NotNull
    public abstract TumorSample tumorSample();

    @NotNull
    public abstract Therapy therapy();

    @NotNull
    public abstract Genomic genomic();

    @NotNull
    public abstract Summary summary();
}