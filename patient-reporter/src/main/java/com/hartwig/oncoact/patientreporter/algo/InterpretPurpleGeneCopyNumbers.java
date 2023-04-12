package com.hartwig.oncoact.patientreporter.algo;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class InterpretPurpleGeneCopyNumbers {

    @NotNull
    public abstract String chromosome();

    @NotNull
    public abstract String chromosomeBand();

    @NotNull
    public abstract String geneName();

    @Nullable
    public abstract Double minCopyNumber();

    @Nullable
    public abstract Double minMinorAlleleCopyNumber();
}
