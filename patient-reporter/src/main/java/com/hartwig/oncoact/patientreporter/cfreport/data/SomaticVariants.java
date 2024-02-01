package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.util.Lists;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.purple.Hotspot;
import com.hartwig.hmftools.datamodel.purple.PurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;
import com.hartwig.oncoact.copynumber.CopyNumberInterpretation;
import com.hartwig.oncoact.copynumber.PurpleGainLossData;
import com.hartwig.oncoact.patientreporter.algo.CurationFunctions;
import com.hartwig.oncoact.patientreporter.util.Genes;
import com.hartwig.oncoact.util.Formats;
import com.hartwig.oncoact.variant.DriverInterpretation;
import com.hartwig.oncoact.variant.ReportableVariant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SomaticVariants {

    private static final Logger LOGGER = LogManager.getLogger(SomaticVariants.class);

    private SomaticVariants() {
    }

    @NotNull
    public static List<ReportableVariant> sort(@NotNull List<ReportableVariant> variants) {

        // create map on gene level
        Map<String, List<ReportableVariant>> mapReportableVariant = Maps.newHashMap();
        for (ReportableVariant variant : variants) {
            if (mapReportableVariant.containsKey(variant.gene())) {
                List<ReportableVariant> current = mapReportableVariant.get(variant.gene());
                current.add(variant);
                mapReportableVariant.put(variant.gene(), current);
            } else {
                mapReportableVariant.put(variant.gene(), com.google.common.collect.Lists.newArrayList(variant));
            }
        }

        // sort the gene level map
        Map<String, List<ReportableVariant>> mapReportableVariantSorted = Maps.newHashMap();
        for (Map.Entry<String, List<ReportableVariant>> entry : mapReportableVariant.entrySet()) {
            mapReportableVariantSorted.put(entry.getKey(), entry.getValue().stream().sorted((variant1, variant2) -> {
                if (variant1.canonicalHgvsCodingImpact().isEmpty()) {
                    return 1;
                } else if (variant2.canonicalHgvsCodingImpact().isEmpty()) {
                    return -1;
                } else {
                    int codonVariant1 = extractCodonField(variant1.canonicalHgvsCodingImpact());
                    int codonVariant2 = extractCodonField(variant2.canonicalHgvsCodingImpact());
                    return Integer.compare(codonVariant1, codonVariant2);
                }
            }).collect(Collectors.toList()));
        }

        // sorted the genes on driver likelihood
        List<ReportableVariant> variantList = Lists.newArrayList();
        for (Map.Entry<String, List<ReportableVariant>> entry : mapReportableVariantSorted.entrySet()) {
            variantList.addAll(entry.getValue().stream().sorted((variant1, variant2) -> {
                if (variant1.driverLikelihood() != null && variant2.driverLikelihood() != null) {
                    if (Math.abs(variant1.driverLikelihood() - variant2.driverLikelihood()) > 0.001) {
                        return (variant1.driverLikelihood() - variant2.driverLikelihood()) < 0 ? 1 : -1;
                    }
                } else if (variant1.driverLikelihood() != null && variant2.driverLikelihood() == null) {
                    return -1;
                } else {
                    return 1;
                }
                return 0;
            }).collect(Collectors.toList()));
        }
        return variantList;
    }

    public static boolean hasNotifiableGermlineVariant(@NotNull Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant) {
        for (Boolean notify : notifyGermlineStatusPerVariant.values()) {
            if (notify) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasPhasedVariant(@NotNull List<ReportableVariant> reportableVariants) {
        for (ReportableVariant reportableVariant : reportableVariants) {
            if (isPhasedInframeEffect(reportableVariant.canonicalEffect())) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    public static String determineVariantAnnotationCanonical(@NotNull String hgvsCoding, @NotNull String hgvsProtein) {
        if (!hgvsCoding.isEmpty() && !hgvsProtein.isEmpty()) {
            return hgvsCoding + " (" + hgvsProtein + ")";
        } else if (!hgvsCoding.isEmpty()) {
            return hgvsCoding;
        } else if (!hgvsProtein.isEmpty()) {
            return hgvsProtein;
        }
        return Strings.EMPTY;
    }

    @NotNull
    public static String determineVariantAnnotationClinical(@Nullable PurpleTranscriptImpact purpleTranscriptImpact) {
        if (purpleTranscriptImpact != null) {
            String hgvsCoding = purpleTranscriptImpact.hgvsCodingImpact();
            String hgvsProtein = purpleTranscriptImpact.hgvsProteinImpact();
            return determineVariantAnnotationCanonical(hgvsCoding, hgvsProtein);
        }
        return Strings.EMPTY;
    }

    @NotNull
    public static List<String> determineVariantAnnotations(@NotNull String hgvsCoding, @NotNull String hgvsProtein,
            @Nullable PurpleTranscriptImpact purpleTranscriptImpact) {

        List<String> annotationList = Lists.newArrayList();
        annotationList.add(SomaticVariants.determineVariantAnnotationCanonical(hgvsCoding, hgvsProtein));
        if (purpleTranscriptImpact != null && !hgvsProtein.equals(purpleTranscriptImpact.hgvsProteinImpact()) && !hgvsCoding.equals(
                purpleTranscriptImpact.hgvsCodingImpact())) {
            annotationList.add(SomaticVariants.determineVariantAnnotationClinical(purpleTranscriptImpact));
        }
        return annotationList;
    }

    @NotNull
    public static String geneDisplayString(@NotNull ReportableVariant variant, boolean notifyGermline, @NotNull String canonicalEffect) {
        String footer = Strings.EMPTY;
        if (notifyGermline) {
            footer = footer + " #";
        }

        if (isPhasedInframeEffect(canonicalEffect)) {
            footer = footer + " +";
        }

        return variant.gene() + footer;
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
    public static String tVAFString(@NotNull String tVAF, boolean hasReliablePurity, Double totalCopyNumber) {
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
    public static String hotspotString(@Nullable Hotspot hotspot) {
        if (hotspot == null) {
            return Strings.EMPTY;
        } else {
            switch (hotspot) {
                case HOTSPOT:
                    return "Yes";
                case NEAR_HOTSPOT:
                    return "Near";
                default:
                    return Strings.EMPTY;
            }
        }
    }

    @NotNull
    public static String biallelicString(@Nullable Boolean biallelic, boolean hasReliablePurity) {
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
    public static String clonalString(double clonalLikelihood) {
        if (clonalLikelihood > 0.95) {
            return "> 95%";
        } else if (clonalLikelihood > 0.9) {
            return "90-95%";
        } else if (clonalLikelihood > 0.8) {
            return "80-90%";
        } else if (clonalLikelihood > 0.7) {
            return "70-80%";
        } else if (clonalLikelihood > 0.6) {
            return "60-70%";
        } else if (clonalLikelihood > 0.5) {
            return "50-60%";
        } else if (clonalLikelihood > 0.4) {
            return "40-50%";
        } else if (clonalLikelihood > 0.3) {
            return "30-40%";
        } else if (clonalLikelihood > 0.2) {
            return "20-30%";
        } else if (clonalLikelihood > 0.1) {
            return "10-20%";
        } else if (clonalLikelihood > 0.05) {
            return "5-10%";
        } else {
            return "< 5%";
        }
    }

    @NotNull
    public static Set<String> driverGenesWithVariant(@NotNull List<ReportableVariant> variants) {
        Set<String> genes = Sets.newTreeSet();
        for (ReportableVariant variant : variants) {
            if (DriverInterpretation.interpret(variant.driverLikelihood()) == DriverInterpretation.HIGH) {
                genes.add(CurationFunctions.curateGeneNamePdf(variant.gene()));
            }
        }
        return genes;
    }

    @NotNull
    public static Set<String> determineMSIGenes(@NotNull List<ReportableVariant> reportableVariants,
            @NotNull List<PurpleGainLossData> gainsAndLosses, @NotNull List<HomozygousDisruption> homozygousDisruptions) {
        Set<String> genesDisplay = Sets.newTreeSet();

        for (ReportableVariant variant : reportableVariants) {
            if (Genes.MSI_GENES.contains(variant.gene())) {
                genesDisplay.add(CurationFunctions.curateGeneNamePdf(variant.gene()));
            }
        }

        for (PurpleGainLossData gainLoss : gainsAndLosses) {
            if (Genes.MSI_GENES.contains(gainLoss.gene()) && (
                    gainLoss.interpretation() == com.hartwig.oncoact.copynumber.CopyNumberInterpretation.PARTIAL_LOSS
                            || gainLoss.interpretation() == com.hartwig.oncoact.copynumber.CopyNumberInterpretation.FULL_LOSS)) {
                genesDisplay.add(CurationFunctions.curateGeneNamePdf(gainLoss.gene()));
            }
        }

        for (HomozygousDisruption homozygousDisruption : homozygousDisruptions) {
            if (Genes.MSI_GENES.contains(homozygousDisruption.gene())) {
                genesDisplay.add(CurationFunctions.curateGeneNamePdf(homozygousDisruption.gene()));
            }
        }
        return genesDisplay;
    }

    @NotNull
    public static Set<String> determineHRDGenes(@NotNull List<ReportableVariant> reportableVariants,
            @NotNull List<PurpleGainLossData> gainsAndLosses, @NotNull List<HomozygousDisruption> homozygousDisruptions) {
        Set<String> genesDisplay = Sets.newTreeSet();

        for (ReportableVariant variant : reportableVariants) {
            if (Genes.HRD_GENES.contains(variant.gene())) {
                genesDisplay.add(CurationFunctions.curateGeneNamePdf(variant.gene()));
            }
        }

        for (PurpleGainLossData gainLoss : gainsAndLosses) {
            if (Genes.HRD_GENES.contains(gainLoss.gene()) && (
                    gainLoss.interpretation() == com.hartwig.oncoact.copynumber.CopyNumberInterpretation.PARTIAL_LOSS
                            || gainLoss.interpretation() == CopyNumberInterpretation.FULL_LOSS)) {
                genesDisplay.add(CurationFunctions.curateGeneNamePdf(gainLoss.gene()));
            }
        }

        for (HomozygousDisruption homozygousDisruption : homozygousDisruptions) {
            if (Genes.HRD_GENES.contains(homozygousDisruption.gene())) {
                genesDisplay.add(CurationFunctions.curateGeneNamePdf(homozygousDisruption.gene()));
            }
        }

        return genesDisplay;
    }

    private static boolean isPhasedInframeEffect(String canonicalEffect) {
        var upper = canonicalEffect.toUpperCase();
        return upper.contains(PurpleVariantEffect.PHASED_INFRAME_DELETION.toString())
                || upper.contains(PurpleVariantEffect.PHASED_INFRAME_INSERTION.toString());
    }
}