package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.CopyNumberInterpretation;
import com.hartwig.hmftools.datamodel.purple.Hotspot;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;
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
        return variants.stream().sorted((variant1, variant2) -> {
            if (Math.abs(variant1.driverLikelihood() - variant2.driverLikelihood()) > 0.001) {
                return (variant1.driverLikelihood() - variant2.driverLikelihood()) < 0 ? 1 : -1;
            } else {
                if (variant1.gene().equals(variant2.gene())) {
                    // sort on codon position if gene is the same
                    if (variant1.canonicalHgvsCodingImpact().isEmpty()) {
                        return 1;
                    } else if (variant2.canonicalHgvsCodingImpact().isEmpty()) {
                        return -1;
                    } else {
                        int codonVariant1 = extractCodonField(variant1.canonicalHgvsCodingImpact());
                        int codonVariant2 = extractCodonField(variant2.canonicalHgvsCodingImpact());
                        return Integer.compare(codonVariant1, codonVariant2);
                    }
                } else {
                    return variant1.gene().compareTo(variant2.gene());
                }
            }
        }).collect(Collectors.toList());
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
            // TODO Check whether evaluation still works
            if (reportableVariant.canonicalEffect().contains(PurpleVariantEffect.PHASED_INFRAME_DELETION.toString())
                    || reportableVariant.canonicalEffect().contains(PurpleVariantEffect.PHASED_INFRAME_INSERTION.toString())) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasVariantsInCis(@NotNull List<ReportableVariant> reportableVariants) {
        for (ReportableVariant reportableVariant : reportableVariants) {
            if (reportableVariant.localPhaseSet() != null) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    public static String geneDisplayString(@NotNull ReportableVariant variant, boolean notifyGermline, @Nullable Integer localPhaseSet,
                                           @NotNull String canonicalEffect) {
        String footer = Strings.EMPTY;
        if (notifyGermline) {
            footer = footer + "#";
        }

        if (localPhaseSet != null) {
            footer = footer + "=";
        }

        // TODO Check whether evaluation still works
        if (canonicalEffect.contains(PurpleVariantEffect.PHASED_INFRAME_DELETION.toString())
                || canonicalEffect.contains(PurpleVariantEffect.PHASED_INFRAME_INSERTION.toString())) {
            footer = footer + "+";
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

    @Nullable
    public static String tVAFString(@Nullable String tVAF, boolean hasReliablePurity, Double totalCopyNumber) {
        if (totalCopyNumber == null) {
            return Formats.NA_STRING;
        } else {
            double flooredCopyNumber = Math.max(0, totalCopyNumber);
            long roundedCopyNumber = Math.round(flooredCopyNumber);
            return hasReliablePurity && roundedCopyNumber >= 1 ? tVAF : Formats.NA_STRING;
        }
    }

    @NotNull
    public static String hotspotString(@NotNull Hotspot hotspot) {
        switch (hotspot) {
            case HOTSPOT:
                return "Yes";
            case NEAR_HOTSPOT:
                return "Near";
            default:
                return Strings.EMPTY;
        }
    }

    @NotNull
    public static String biallelicString(@Nullable Boolean biallelic, boolean hasReliablePurity) {
        if (hasReliablePurity && biallelic != null) {
            return biallelic ? "Yes" : "No";
        } else {
            return Formats.NA_STRING;
        }
    }

    @NotNull
    public static String clonalString(double clonalLikelihood) {
        if (clonalLikelihood > 0.95) {
            return ">95%";
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
            return "<5%";
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
                                                @NotNull List<PurpleGainLoss> gainsAndLosses, @NotNull List<HomozygousDisruption> homozygousDisruptions) {
        Set<String> genesDisplay = Sets.newTreeSet();

        for (ReportableVariant variant : reportableVariants) {
            if (Genes.MSI_GENES.contains(variant.gene())) {
                genesDisplay.add(CurationFunctions.curateGeneNamePdf(variant.gene()));
            }
        }

        for (PurpleGainLoss gainLoss : gainsAndLosses) {
            if (Genes.MSI_GENES.contains(gainLoss.gene()) && (gainLoss.interpretation() == CopyNumberInterpretation.PARTIAL_LOSS
                    || gainLoss.interpretation() == CopyNumberInterpretation.FULL_LOSS)) {
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
                                                @NotNull List<PurpleGainLoss> gainsAndLosses, @NotNull List<HomozygousDisruption> homozygousDisruptions) {
        Set<String> genesDisplay = Sets.newTreeSet();

        for (ReportableVariant variant : reportableVariants) {
            if (Genes.HRD_GENES.contains(variant.gene())) {
                genesDisplay.add(CurationFunctions.curateGeneNamePdf(variant.gene()));
            }
        }

        for (PurpleGainLoss gainLoss : gainsAndLosses) {
            if (Genes.HRD_GENES.contains(gainLoss.gene()) && (gainLoss.interpretation() == CopyNumberInterpretation.PARTIAL_LOSS
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
}