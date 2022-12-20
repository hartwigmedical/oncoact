package com.hartwig.oncoact.orange.linx;

import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class LinxRecord {

    @NotNull
    public abstract Set<LinxStructuralVariant> structuralVariants();

    @NotNull
    public abstract Set<LinxHomozygousDisruption> homozygousDisruptions();

    @NotNull
    public abstract Set<LinxBreakend> allBreakends();

    @NotNull
    public abstract Set<LinxBreakend> reportableBreakends();

    @NotNull
    public abstract Set<LinxFusion> allFusions();

    @NotNull
    public abstract Set<LinxFusion> reportableFusions();
}
