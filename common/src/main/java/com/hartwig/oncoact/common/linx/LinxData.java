package com.hartwig.oncoact.common.linx;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface LinxData
{

    @NotNull
    List<LinxSvAnnotation> allStructuralVariants();

    @NotNull
    List<LinxFusion> allFusions();

    @NotNull
    List<LinxFusion> reportableFusions();

    @NotNull
    List<LinxBreakend> allBreakends();

    @NotNull
    List<GeneDisruption> reportableGeneDisruptions();

    @NotNull
    List<HomozygousDisruption> homozygousDisruptions();

    @NotNull
    List<LinxDriver> drivers();

    @NotNull
    List<LinxGermlineSv> allGermlineDisruptions();

    @NotNull
    List<LinxGermlineSv> reportableGermlineDisruptions();
}
