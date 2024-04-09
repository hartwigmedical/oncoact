package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.hartwig.hmftools.datamodel.purple.Hotspot;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;
import com.hartwig.oncoact.patientreporter.model.ObservedVariant;
import com.hartwig.oncoact.patientreporter.model.VariantDriverInterpretation;
import com.hartwig.oncoact.protect.EventGenerator;
import com.hartwig.oncoact.util.Formats;
import com.hartwig.oncoact.variant.DriverInterpretation;
import com.hartwig.oncoact.variant.ReportableVariant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.hartwig.oncoact.patientreporter.algo.wgs.ReadDepthCreator.createReadDepth;
import static com.hartwig.oncoact.patientreporter.model.Hotspot.*;

class VariantCreator {

    private static final Logger LOGGER = LogManager.getLogger(VariantCreator.class);

    static List<ObservedVariant> createObservedVariant(
            @NotNull List<ReportableVariant> reportableVariants,
            boolean hasReliablePurity,
            @NotNull Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant
    ) {
        List<ObservedVariant> observedVariants = Lists.newArrayList();
        for (ReportableVariant variant : sort(reportableVariants)) {
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
    public static List<ReportableVariant> sort(@NotNull List<ReportableVariant> variants) {
        var geneToReportableVariants = variants.stream().collect(Collectors.groupingBy(ReportableVariant::gene, Collectors.toList()));
        var geneToSortedReportableVariants = sortVariantsPerGene(geneToReportableVariants);

        List<String> sortedGenes = sortGenes(geneToSortedReportableVariants);

        return sortedGenes.stream().flatMap(g -> geneToSortedReportableVariants.get(g).stream()).collect(Collectors.toList());
    }

    @NotNull
    private static Map<String, List<ReportableVariant>> sortVariantsPerGene(
            @NotNull Map<String, List<ReportableVariant>> geneToReportableVariants) {
        // Sort by two rules:
        //  1. Sort by driver likelihood from high to low. Null is interpreted as the lowest possible driver likelihood.
        //  2. Sort by position in the HGVS coding impact, if present. Empty coding impacts are sorted towards the end of the list.
        Map<String, List<ReportableVariant>> geneToSortedReportableVariants = Maps.newHashMap();
        for (Map.Entry<String, List<ReportableVariant>> entry : geneToReportableVariants.entrySet()) {
            List<ReportableVariant> sortedVariantsForGene =
                    entry.getValue().stream().sorted(VariantCreator::variantCompareTo).collect(Collectors.toList());
            geneToSortedReportableVariants.put(entry.getKey(), sortedVariantsForGene);
        }
        return geneToSortedReportableVariants;
    }

    @NotNull
    private static List<String> sortGenes(@NotNull Map<String, List<ReportableVariant>> geneToReportableVariants) {
        // Sort genes by two rules:
        //  1. Maximum driver likelihood from high to low. Null is interpreted as the lowest possible driver likelihood.
        //  2. If maximum driver likelihoods are the same or very similar, sort gene names alphabetically.
        Map<String, Double> geneToMaxDriverLikelihood = determineMaximumDriverLikelihoodPerGene(geneToReportableVariants);

        return geneToReportableVariants.keySet()
                .stream()
                .sorted((gene1, gene2) -> geneCompareTo(gene1, gene2, geneToMaxDriverLikelihood))
                .collect(Collectors.toList());
    }

    @NotNull
    private static Map<String, Double> determineMaximumDriverLikelihoodPerGene(
            @NotNull Map<String, List<ReportableVariant>> geneToReportableVariants) {
        Map<String, Double> geneToMaxDriverLikelihood = new HashMap<>();
        for (Map.Entry<String, List<ReportableVariant>> entry : geneToReportableVariants.entrySet()) {
            geneToMaxDriverLikelihood.put(entry.getKey(), determineMaximumDriverLikelihood(entry.getValue()));
        }
        return geneToMaxDriverLikelihood;
    }

    @Nullable
    private static Double determineMaximumDriverLikelihood(@NotNull List<ReportableVariant> variants) {
        OptionalDouble optionalMaximumDriverLikelihood =
                variants.stream().map(ReportableVariant::driverLikelihood).filter(Objects::nonNull).mapToDouble(d -> d).max();
        if (optionalMaximumDriverLikelihood.isPresent()) {
            return optionalMaximumDriverLikelihood.getAsDouble();
        } else {
            return null;
        }
    }

    @VisibleForTesting
    static int geneCompareTo(@NotNull String gene1, @NotNull String gene2, @NotNull Map<String, Double> geneToMaximumDriverLikelihood) {
        Double maxDriverLikelihood1 = geneToMaximumDriverLikelihood.get(gene1);
        Double maxDriverLikelihood2 = geneToMaximumDriverLikelihood.get(gene2);
        if (maxDriverLikelihood1 == null && maxDriverLikelihood2 != null) {
            return 1;
        } else if (maxDriverLikelihood1 != null && maxDriverLikelihood2 == null) {
            return -1;
        } else if (driverLikelihoodsComparable(maxDriverLikelihood1, maxDriverLikelihood2)) {
            return (maxDriverLikelihood1 - maxDriverLikelihood2) < 0 ? 1 : -1;
        } else {
            return gene1.compareTo(gene2);
        }
    }

    private static boolean driverLikelihoodsComparable(@Nullable Double driverLikelihood1, @Nullable Double driverLikelihood2) {
        if (driverLikelihood1 == null || driverLikelihood2 == null) {
            return false;
        } else {
            return Math.abs(driverLikelihood1 - driverLikelihood2) > 0.001;
        }
    }

    @VisibleForTesting
    static int variantCompareTo(@NotNull ReportableVariant variant1, @NotNull ReportableVariant variant2) {
        Double driverLikelihood1 = variant1.driverLikelihood();
        Double driverLikelihood2 = variant2.driverLikelihood();
        if (driverLikelihood1 == null && driverLikelihood2 != null) {
            return 1;
        } else if (driverLikelihood1 != null && driverLikelihood2 == null) {
            return -1;
        } else if (driverLikelihoodsComparable(driverLikelihood1, driverLikelihood2)) {
            return (driverLikelihood1 - driverLikelihood2) < 0 ? 1 : -1;
        } else if (variant1.canonicalHgvsCodingImpact().isEmpty() && variant2.canonicalHgvsCodingImpact().isEmpty()) {
            return 0;
        } else if (variant1.canonicalHgvsCodingImpact().isEmpty()) {
            return 1;
        } else if (variant2.canonicalHgvsCodingImpact().isEmpty()) {
            return -1;
        } else {
            int codonVariant1 = extractCodonField(variant1.canonicalHgvsCodingImpact());
            int codonVariant2 = extractCodonField(variant2.canonicalHgvsCodingImpact());
            return Integer.compare(codonVariant1, codonVariant2);
        }
    }

    @VisibleForTesting
    static int extractCodonField(@NotNull String hgvsCoding) {
        StringBuilder codonAppender = new StringBuilder();
        boolean noDigitFound = true;

        int startIndex = findStartIndex(hgvsCoding);
        int index = startIndex;
        while (noDigitFound && index < hgvsCoding.length()) {
            boolean isMinusSign = Character.toString(hgvsCoding.charAt(index)).equals("-");
            if ((isMinusSign && index == startIndex) || Character.isDigit(hgvsCoding.charAt(index))) {
                codonAppender.append(hgvsCoding.charAt(index));
            } else {
                noDigitFound = false;
            }
            index++;
        }
        String codon = codonAppender.toString();
        if (codon.isEmpty()) {
            LOGGER.warn("Could not extract codon from {}", hgvsCoding);
            return -1;
        } else {
            return Integer.parseInt(codon);
        }
    }

    private static int findStartIndex(@NotNull String hgvsCoding) {
        // hgvsCoding starts with either "c." or "c.*", we need to skip that...
        return hgvsCoding.startsWith("c.*") ? 3 : 2;
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