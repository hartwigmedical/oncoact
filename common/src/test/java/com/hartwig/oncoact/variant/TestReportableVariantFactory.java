package com.hartwig.oncoact.variant;

import com.hartwig.oncoact.orange.purple.PurpleCodingEffect;
import com.hartwig.oncoact.orange.purple.PurpleGenotypeStatus;
import com.hartwig.oncoact.orange.purple.PurpleHotspotType;
import com.hartwig.oncoact.orange.purple.PurpleVariantType;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestReportableVariantFactory {

    private TestReportableVariantFactory() {
    }

    @NotNull
    public static ImmutableReportableVariant.Builder builder() {
        return ImmutableReportableVariant.builder()
                .source(ReportableVariantSource.SOMATIC)
                .gene(Strings.EMPTY)
                .transcript("transcript")
                .isCanonical(true)
                .genotypeStatus(PurpleGenotypeStatus.UNKNOWN)
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .type(PurpleVariantType.SNP)
                .otherReportedEffects(Strings.EMPTY)
                .canonicalTranscript("123")
                .canonicalEffect(Strings.EMPTY)
                .canonicalCodingEffect(PurpleCodingEffect.UNDEFINED)
                .canonicalHgvsCodingImpact(Strings.EMPTY)
                .canonicalHgvsProteinImpact(Strings.EMPTY)
                .totalReadCount(0)
                .alleleReadCount(0)
                .totalCopyNumber(0)
                .alleleCopyNumber(0D)
                .minorAlleleCopyNumber(0D)
                .hotspot(PurpleHotspotType.HOTSPOT)
                .clonalLikelihood(1D)
                .driverLikelihood(0D)
                .biallelic(false);
    }
}
