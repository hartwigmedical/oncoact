package com.hartwig.oncoact.copynumber;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class PurpleGainLossData {

    @NotNull
    public abstract CopyNumberInterpretation interpretation();

    @NotNull
    public abstract String chromosome();

    @NotNull
    public abstract String chromosomeBand();

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String transcript();

    public abstract boolean isCanonical();

    public abstract double minCopies();

    public abstract double maxCopies();
}
