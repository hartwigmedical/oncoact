package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class TumorSample extends Sample {

    @NotNull
    public abstract PrimaryTumor primaryTumor();

    public abstract ReportingId reportingId();

    @NotNull
    public abstract String cohort();

    @Nullable
    public abstract Patient patient();

    @NotNull
    public abstract Hospital hospital();

    @NotNull
    public abstract Biopsy biopsy();

    @NotNull
    public abstract String sop();
}
