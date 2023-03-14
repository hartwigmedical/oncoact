package com.hartwig.oncoact.orange.linx;

import com.hartwig.hmftools.datamodel.gene.TranscriptCodingType;
import com.hartwig.hmftools.datamodel.gene.TranscriptRegionType;
import com.hartwig.hmftools.datamodel.linx.FusionLikelihoodType;
import com.hartwig.hmftools.datamodel.linx.FusionPhasedType;
import com.hartwig.hmftools.datamodel.linx.ImmutableHomozygousDisruption;
import com.hartwig.hmftools.datamodel.linx.ImmutableLinxBreakend;
import com.hartwig.hmftools.datamodel.linx.ImmutableLinxFusion;
import com.hartwig.hmftools.datamodel.linx.ImmutableLinxSvAnnotation;
import com.hartwig.hmftools.datamodel.linx.LinxFusionType;
import com.hartwig.hmftools.datamodel.sv.LinxBreakendType;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestLinxFactory {

    private TestLinxFactory() {
    }

    @NotNull
    public static ImmutableLinxSvAnnotation.Builder structuralVariantBuilder() {
        return ImmutableLinxSvAnnotation.builder().svId(0).clusterId(0);
    }

    @NotNull
    public static ImmutableHomozygousDisruption.Builder homozygousDisruptionBuilder() {
        return ImmutableHomozygousDisruption.builder()
                .chromosome(Strings.EMPTY)
                .chromosomeBand(Strings.EMPTY)
                .gene(Strings.EMPTY)
                .transcript(Strings.EMPTY)
                .isCanonical(false);
    }

    @NotNull
    public static ImmutableLinxBreakend.Builder breakendBuilder() {
        return ImmutableLinxBreakend.builder()
                .reportedDisruption(true)
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
                .regionType(TranscriptRegionType.INTRONIC)
                .codingType(TranscriptCodingType.NON_CODING);
    }

    @NotNull
    public static ImmutableLinxFusion.Builder fusionBuilder() {
        return ImmutableLinxFusion.builder()
                .reported(true)
                .reportedType(LinxFusionType.NONE)
                .name(Strings.EMPTY)
                .geneStart(Strings.EMPTY)
                .geneTranscriptStart(Strings.EMPTY)
                .geneContextStart(Strings.EMPTY)
                .fusedExonUp(0)
                .geneEnd(Strings.EMPTY)
                .geneTranscriptEnd(Strings.EMPTY)
                .geneContextEnd(Strings.EMPTY)
                .fusedExonDown(0)
                .likelihood(FusionLikelihoodType.LOW)
                .phased(FusionPhasedType.OUT_OF_FRAME)
                .junctionCopyNumber(0D);
    }
}
