package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.hartwig.hmftools.datamodel.purple.Hotspot;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;
import com.hartwig.oncoact.patientreporter.model.ObservedVariant;
import com.hartwig.oncoact.patientreporter.model.VariantDriverInterpretation;
import com.hartwig.oncoact.protect.EventGenerator;
import com.hartwig.oncoact.util.Formats;
import com.hartwig.oncoact.variant.DriverInterpretation;
import com.hartwig.oncoact.variant.ReportableVariant;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static com.hartwig.oncoact.patientreporter.algo.wgs.ReadDepthCreator.createReadDepth;
import static com.hartwig.oncoact.patientreporter.model.Hotspot.*;

class VariantCreator {

    static List<ObservedVariant> createObservedVariant(
            @NotNull List<ReportableVariant> reportableVariants,
            boolean hasReliablePurity,
            @NotNull Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant
    ) {
        List<ObservedVariant> observedVariants = Lists.newArrayList();
        for (ReportableVariant variant : reportableVariants) {
            observedVariants.add(ObservedVariant.builder()
                    .gene(determineGeneSymbol(variant.gene(), notifyGermlineStatusPerVariant.get(variant), variant.canonicalEffect()))
                    .position(variant.gDNA())
                    .variant(determineVariantAnnotations(variant))
                    .readDepth(createReadDepth(variant.alleleReadCount(), variant.totalReadCount()))
                    .copies(determineCopies(variant.totalCopyNumber(), hasReliablePurity))
                    .tVaf(determineTvaf(variant.tVAF(), hasReliablePurity, variant.totalCopyNumber()))
                    .biallelic(determineBiallelic(variant.biallelic(), hasReliablePurity))
                    .hotspot(determineHotspot(variant.hotspot()))
                    .driver(determineDriver(variant.driverLikelihoodInterpretation()))
                    .hasNotifiableGermlineVariant(hasNotifiableGermlineVariant(notifyGermlineStatusPerVariant))
                    .hasPhasedVariant(hasPhasedVariant(variant))
                    .build());
        }
        return observedVariants;

    }

    @NotNull
    private static String determineGeneSymbol(@NotNull String gene, boolean notifyGermline, @NotNull String canonicalEffect) {
        String footer = Strings.EMPTY;
        if (notifyGermline) {
            footer = footer + " #";
        }

        if (isPhasedInframeEffect(canonicalEffect)) {
            footer = footer + " +";
        }

        return gene + footer;
    }

    private static boolean isPhasedInframeEffect(String canonicalEffect) {
        var upper = canonicalEffect.toUpperCase();
        return upper.contains(PurpleVariantEffect.PHASED_INFRAME_DELETION.toString())
                || upper.contains(PurpleVariantEffect.PHASED_INFRAME_INSERTION.toString());
    }

    @NotNull
    public static List<String> determineVariantAnnotations(ReportableVariant variant) {
        return EventGenerator.determineVariantAnnotationReport(variant.canonicalHgvsCodingImpact(),
                variant.canonicalHgvsProteinImpact(),
                variant.otherImpactClinical());
    }

    @NotNull
    public static String determineCopies(Double copyNumber, boolean hasReliablePurity) {
        if (copyNumber == null) {
            return Strings.EMPTY;
        } else if (!hasReliablePurity || copyNumber.isNaN()) {
            return Formats.NA_STRING;
        }
        return String.valueOf(Math.round(copyNumber));
    }

    @NotNull
    public static String determineTvaf(@NotNull String tVAF, boolean hasReliablePurity, Double totalCopyNumber) {
        if (tVAF.equals(Strings.EMPTY)) {
            return Strings.EMPTY;
        } else if (totalCopyNumber == null) {
            return Formats.NA_STRING;
        } else {
            double flooredCopyNumber = Math.max(0, totalCopyNumber);
            long roundedCopyNumber = Math.round(flooredCopyNumber);
            return hasReliablePurity && roundedCopyNumber >= 1 ? tVAF : Formats.NA_STRING;
        }
    }

    @NotNull
    public static String determineBiallelic(@Nullable Boolean biallelic, boolean hasReliablePurity) {
        if (biallelic == null) {
            return Strings.EMPTY;
        } else {
            if (hasReliablePurity) {
                return biallelic ? "Yes" : "No";
            } else {
                return Formats.NA_STRING;
            }
        }
    }

    @NotNull
    public static com.hartwig.oncoact.patientreporter.model.Hotspot determineHotspot(@Nullable Hotspot hotspot) {
        if (hotspot == null) {
            return UNKNOWN;
        } else {
            switch (hotspot) {
                case HOTSPOT:
                    return HOTSPOT;
                case NEAR_HOTSPOT:
                    return NEAR_HOTSPOT;
                default:
                    return UNKNOWN;
            }
        }
    }

    @NotNull
    public static VariantDriverInterpretation determineDriver(@Nullable DriverInterpretation driverInterpretation) {
        if (driverInterpretation == null) {
            return VariantDriverInterpretation.UNKNOWN;
        } else {
            switch (driverInterpretation) {
                case HIGH:
                    return VariantDriverInterpretation.HIGH;
                case MEDIUM:
                    return VariantDriverInterpretation.MEDIUM;
                case LOW:
                    return VariantDriverInterpretation.LOW;
                default:
                    return VariantDriverInterpretation.UNKNOWN;
            }
        }
    }

    public static boolean hasNotifiableGermlineVariant(@NotNull Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant) {
        for (Boolean notify : notifyGermlineStatusPerVariant.values()) {
            if (notify) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasPhasedVariant(@NotNull ReportableVariant reportableVariant) {
        return isPhasedInframeEffect(reportableVariant.canonicalEffect());
    }
}