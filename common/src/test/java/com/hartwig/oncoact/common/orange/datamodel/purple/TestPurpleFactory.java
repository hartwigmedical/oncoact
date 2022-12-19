package com.hartwig.oncoact.common.orange.datamodel.purple;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestPurpleFactory {

    private TestPurpleFactory() {
    }

    @NotNull
    public static ImmutablePurpleFit.Builder fitBuilder() {
        return ImmutablePurpleFit.builder().hasReliableQuality(false).hasReliablePurity(false).purity(0).ploidy(0);
    }

    @NotNull
    public static ImmutablePurpleCharacteristics.Builder characteristicsBuilder() {
        return ImmutablePurpleCharacteristics.builder()
                .microsatelliteStabilityStatus(Strings.EMPTY)
                .tumorMutationalBurden(0D)
                .tumorMutationalBurdenStatus(Strings.EMPTY)
                .tumorMutationalLoad(0)
                .tumorMutationalLoadStatus(Strings.EMPTY);
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
                .reported(true)
                .type(PurpleVariantType.SNP)
                .gene(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .adjustedCopyNumber(0D)
                .variantCopyNumber(0D)
                .hotspot(PurpleHotspotType.NON_HOTSPOT)
                .subclonalLikelihood(0D)
                .biallelic(false)
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
        return ImmutablePurpleCopyNumber.builder()
                .gene(Strings.EMPTY)
                .interpretation(PurpleCopyNumberInterpretation.FULL_LOSS)
                .minCopies(0)
                .maxCopies(0);
    }
}
