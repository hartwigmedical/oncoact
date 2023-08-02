package com.hartwig.oncoact.patientreporter.failedreasondb;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class FailedReason {

    @NotNull
    public abstract String reportReason();

    @NotNull
    public abstract String reportExplanation();

    @Nullable
    public abstract String sampleFailReasonComment();
}
