package com.hartwig.oncoact.patientreporter.germline;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class GermlineReportingEntry {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract GermlineCondition condition();

    @Nullable
    public abstract String conditionFilter();
}
