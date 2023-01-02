package com.hartwig.oncoact.orange.linx;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestLinxFactory {

    private TestLinxFactory() {
    }

    @NotNull
    public static ImmutableLinxStructuralVariant.Builder structuralVariantBuilder() {
        return ImmutableLinxStructuralVariant.builder().svId(0).clusterId(0);
    }

    @NotNull
    public static ImmutableLinxHomozygousDisruption.Builder homozygousDisruptionBuilder() {
        return ImmutableLinxHomozygousDisruption.builder()
                .chromosome(Strings.EMPTY)
                .chromosomeBand(Strings.EMPTY)
                .gene(Strings.EMPTY)
                .transcript(Strings.EMPTY)
                .isCanonical(false);
    }

    @NotNull
    public static ImmutableLinxBreakend.Builder breakendBuilder() {
        return ImmutableLinxBreakend.builder()
                .reported(true)
                .disruptive(false)
                .svId(0)
                .gene(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .chrBand(Strings.EMPTY)
                .transcriptId(Strings.EMPTY)
                .canonical(false)
                .type(LinxBreakendType.BND)
                .junctionCopyNumber(0D)
                .undisruptedCopyNumber(0D)
                .nextSpliceExonRank(0)
                .exonUp(0)
                .exonDown(0)
                .geneOrientation(Strings.EMPTY)
                .orientation(0)
                .strand(0)
                .regionType(LinxRegionType.INTRONIC)
                .codingType(LinxCodingType.NON_CODING);
    }

    @NotNull
    public static ImmutableLinxFusion.Builder fusionBuilder() {
        return ImmutableLinxFusion.builder()
                .reported(true)
                .type(LinxFusionType.NONE)
                .name(Strings.EMPTY)
                .geneStart(Strings.EMPTY)
                .geneTranscriptStart(Strings.EMPTY)
                .geneContextStart(Strings.EMPTY)
                .fusedExonUp(0)
                .geneEnd(Strings.EMPTY)
                .geneTranscriptEnd(Strings.EMPTY)
                .geneContextEnd(Strings.EMPTY)
                .fusedExonDown(0)
                .driverLikelihood(LinxFusionDriverLikelihood.LOW)
                .phased(LinxPhasedType.OUT_OF_FRAME)
                .junctionCopyNumber(0D);
    }
}
