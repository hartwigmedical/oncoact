package com.hartwig.oncoact.orange.linx;

import java.util.List;
import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class LinxRecord {

    @NotNull
    public abstract Set<LinxStructuralVariant> allSomaticStructuralVariants();

    @NotNull
    public abstract Set<LinxHomozygousDisruption> somaticHomozygousDisruptions();

    @NotNull
    public abstract List<LinxBreakend> additionalSuspectSomaticBreakends();

    @NotNull
    public abstract Set<LinxBreakend> allSomaticBreakends();

    @NotNull
    public abstract Set<LinxBreakend> reportableSomaticBreakends();

    @NotNull
    public abstract Set<LinxFusion> allSomaticFusions();

    @NotNull
    public abstract Set<LinxFusion> reportableSomaticFusions();

    @NotNull
    public abstract Set<LinxFusion> additionalSuspectSomaticFusions();
}
