package com.hartwig.oncoact.patientreporter.model;

import com.hartwig.oncoact.patientreporter.failedreasondb.FailedReason;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class WgsReportFailed implements WgsPatientReport {

    @NotNull
    public abstract String reportDate();

    @NotNull
    public abstract String receiver();

    @NotNull
    public abstract TumorSample tumorSample();

    @NotNull
    public abstract Sample referenceSample();

    @NotNull
    public abstract FailedReason failedDatabase();

    @Nullable
    public abstract FailGenomic failGenomic();

    @NotNull
    public abstract Version version();

    @NotNull
    public abstract String user();

    @NotNull
    public abstract Optional<String> comments();

    public static ImmutableWgsReportFailed.Builder builder() {
        return ImmutableWgsReportFailed.builder();
    }
}