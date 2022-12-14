package com.hartwig.oncoact.orange.purple;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestPurpleFactory {

    private TestPurpleFactory() {
    }

    @NotNull
    public static ImmutablePurpleFit.Builder fitBuilder() {
        return ImmutablePurpleFit.builder().hasSufficientQuality(false).containsTumorCells(false).purity(0).ploidy(0);
    }

    @NotNull
    public static ImmutablePurpleCharacteristics.Builder characteristicsBuilder() {
        return ImmutablePurpleCharacteristics.builder()
                .microsatelliteIndelsPerMb(0D)
                .microsatelliteStatus(PurpleMicrosatelliteStatus.UNKNOWN)
                .tumorMutationalBurdenPerMb(0D)
                .tumorMutationalBurdenStatus(PurpleTumorMutationalStatus.UNKNOWN)
                .tumorMutationalLoad(0)
                .tumorMutationalLoadStatus(PurpleTumorMutationalStatus.UNKNOWN);
    }

    @NotNull
    public static ImmutablePurpleDriver.Builder driverBuilder() {
        return ImmutablePurpleDriver.builder()
                .gene(Strings.EMPTY)
                .transcript(Strings.EMPTY)
                .type(PurpleDriverType.MUTATION)
                .driverLikelihood(0D);
    }

    @NotNull
    public static ImmutablePurpleVariant.Builder variantBuilder() {
        return ImmutablePurpleVariant.builder()
                .reported(false)
                .type(PurpleVariantType.SNP)
                .gene(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .adjustedCopyNumber(0D)
                .minorAlleleCopyNumber(0D)
                .variantCopyNumber(0D)
                .hotspot(PurpleHotspotType.NON_HOTSPOT)
                .tumorDepth(depthBuilder().build())
                .subclonalLikelihood(0D)
                .biallelic(false)
                .genotypeStatus(PurpleGenotypeStatus.UNKNOWN)
                .canonicalImpact(transcriptImpactBuilder().build());
    }

    @NotNull
    public static ImmutablePurpleTranscriptImpact.Builder transcriptImpactBuilder() {
        return ImmutablePurpleTranscriptImpact.builder()
                .transcript(Strings.EMPTY)
                .hgvsCodingImpact(Strings.EMPTY)
                .hgvsProteinImpact(Strings.EMPTY)
                .spliceRegion(false)
                .codingEffect(PurpleCodingEffect.UNDEFINED);
    }

    @NotNull
    public static ImmutablePurpleCopyNumber.Builder copyNumberBuilder() {
        return ImmutablePurpleCopyNumber.builder().chromosome(Strings.EMPTY).start(0).end(0).averageTumorCopyNumber(0D);
    }

    @NotNull
    public static ImmutablePurpleGeneCopyNumber.Builder geneCopyNumberBuilder() {
        return ImmutablePurpleGeneCopyNumber.builder()
                .chromosome(Strings.EMPTY)
                .chromosomeBand(Strings.EMPTY)
                .gene(Strings.EMPTY)
                .minCopyNumber(0D)
                .minMinorAlleleCopyNumber(0D);
    }

    @NotNull
    public static ImmutablePurpleGainLoss.Builder gainLossBuilder() {
        return ImmutablePurpleGainLoss.builder()
                .chromosome(Strings.EMPTY)
                .chromosomeBand(Strings.EMPTY)
                .gene(Strings.EMPTY)
                .transcript(Strings.EMPTY)
                .isCanonical(false)
                .interpretation(PurpleGainLossInterpretation.FULL_LOSS)
                .minCopies(0)
                .maxCopies(0);
    }

    @NotNull
    public static ImmutablePurpleAllelicDepth.Builder depthBuilder() {
        return ImmutablePurpleAllelicDepth.builder().totalReadCount(0).alleleReadCount(0);
    }
}
