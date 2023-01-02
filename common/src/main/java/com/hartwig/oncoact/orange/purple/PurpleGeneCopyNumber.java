package com.hartwig.oncoact.orange.purple;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class PurpleGeneCopyNumber {

    @NotNull
    public abstract String chromosome();

    @NotNull
    public abstract String chromosomeBand();

    @NotNull
    public abstract String gene();

    public abstract double minCopyNumber();

    public abstract double minMinorAlleleCopyNumber();

}
