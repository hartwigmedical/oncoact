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
    public abstract List<LinxStructuralVariant> allSomaticStructuralVariants();

    @NotNull
    public abstract List<LinxFusion> allSomaticFusions();

    @NotNull
    public abstract List<LinxFusion> reportableSomaticFusions();

    @NotNull
    public abstract List<LinxFusion> additionalSuspectSomaticFusions();

    @NotNull
    public abstract List<LinxBreakend> allSomaticBreakends();

    @NotNull
    public abstract List<LinxBreakend> reportableSomaticBreakends();

    @NotNull
    public abstract List<LinxBreakend> additionalSuspectSomaticBreakends();

    @NotNull
    public abstract List<LinxHomozygousDisruption> somaticHomozygousDisruptions();

    @Nullable
    public abstract List<LinxStructuralVariant> allGermlineStructuralVariants();

    @Nullable
    public abstract List<LinxBreakend> allGermlineBreakends();

    @Nullable
    public abstract List<LinxBreakend> reportableGermlineBreakends();

    @Nullable
    public abstract List<LinxHomozygousDisruption> germlineHomozygousDisruptions();
}
