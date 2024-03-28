package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class ReportingId {

    @NotNull
    public abstract String value();

    @NotNull
    public abstract ReportingIdType type();

    @Nullable
    public abstract String pathologyId();

    @Nullable
    public abstract String label();

    public static ImmutableReportingId.Builder builder() {
        return ImmutableReportingId.builder();
    }
}