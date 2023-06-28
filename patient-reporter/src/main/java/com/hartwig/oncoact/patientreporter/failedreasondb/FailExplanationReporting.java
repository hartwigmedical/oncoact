package com.hartwig.oncoact.patientreporter.failedreasondb;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class FailExplanationReporting {

    @NotNull
    public abstract String reportReason();

    @NotNull
    public abstract String reportExplanation();

    @NotNull
    public abstract String reportExplanationDetail();
}
