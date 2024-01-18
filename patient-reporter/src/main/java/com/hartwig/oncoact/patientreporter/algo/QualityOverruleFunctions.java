package com.hartwig.oncoact.patientreporter.algo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.variant.ImmutableReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariant;

import org.jetbrains.annotations.NotNull;

public final class QualityOverruleFunctions {

    private QualityOverruleFunctions() {
    }

    @NotNull
    public static GenomicAnalysis overrule(@NotNull GenomicAnalysis genomicAnalysis) {
        Map<ReportableVariant, Boolean> overruledVariantMaps =
                overruleMap(genomicAnalysis.notifyGermlineStatusPerVariant(), genomicAnalysis.hasReliablePurity());

        List<ReportableVariantWithNotify> overruledVariantsWithNotify =
                overruleVariants(genomicAnalysis.reportableVariants(), overruledVariantMaps, genomicAnalysis.hasReliablePurity());

        List<CnPerChromosomeArmData> cnPerChromosomeData = Lists.newArrayList();
        for (CnPerChromosomeArmData cnPerChromosome : genomicAnalysis.cnPerChromosome()) {
            if (genomicAnalysis.hasReliablePurity()) {
                cnPerChromosomeData.add(cnPerChromosome);
            }
        }

        List<CnPerChromosomeArmData> cnPerChromosomeDataSort = sort(cnPerChromosomeData);

        List<ReportableVariant> overruledVariants = Lists.newArrayList();
        Map<ReportableVariant, Boolean> newNotifyPerVariant = Maps.newHashMap();
        for (ReportableVariantWithNotify overruled : overruledVariantsWithNotify) {
            overruledVariants.add(overruled.variant());
            newNotifyPerVariant.put(overruled.variant(), overruled.notifyVariant());
        }

        return ImmutableGenomicAnalysis.builder()
                .from(genomicAnalysis)
                .reportableVariants(overruledVariants)
                .notifyGermlineStatusPerVariant(newNotifyPerVariant)
                .cnPerChromosome(cnPerChromosomeDataSort)
                .suspectGeneCopyNumbersWithLOH(overruleSuspectedLOH(genomicAnalysis.suspectGeneCopyNumbersWithLOH(),
                        genomicAnalysis.hasReliablePurity()))
                .build();
    }

    @NotNull
    public static List<InterpretPurpleGeneCopyNumbers> overruleSuspectedLOH(
            @NotNull List<InterpretPurpleGeneCopyNumbers> LOHPurpleGeneCopyNumbers, boolean hasReliablePurity) {
        List<InterpretPurpleGeneCopyNumbers> suspectedGenesCurated = Lists.newArrayList();

        for (InterpretPurpleGeneCopyNumbers LOHGene : LOHPurpleGeneCopyNumbers) {
            suspectedGenesCurated.add(ImmutableInterpretPurpleGeneCopyNumbers.builder()
                    .from(LOHGene)
                    .minCopyNumber(hasReliablePurity ? LOHGene.minCopyNumber() : null)
                    .minMinorAlleleCopyNumber(hasReliablePurity ? LOHGene.minMinorAlleleCopyNumber() : null)
                    .build());
        }
        return suspectedGenesCurated;
    }

    @NotNull
    public static List<CnPerChromosomeArmData> sort(@NotNull List<CnPerChromosomeArmData> cnPerChromosomeArmData) {
        return cnPerChromosomeArmData.stream().sorted((item1, item2) -> {
            if (item1.chromosome().equals(item2.chromosome())) {
                if (item1.chromosomeArm().equals(item2.chromosomeArm())) {
                    return item1.chromosomeArm().compareTo(item2.chromosomeArm());
                }
                return item1.chromosome().compareTo(item2.chromosome());
            } else {
                return item1.chromosome().compareTo(item2.chromosome());
            }
        }).collect(Collectors.toList());
    }

    @NotNull
    private static Map<ReportableVariant, Boolean> overruleMap(@NotNull Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant,
            boolean hasReliablePurity) {
        Map<ReportableVariant, Boolean> filteredMap = Maps.newHashMap();
        for (Map.Entry<ReportableVariant, Boolean> entry : notifyGermlineStatusPerVariant.entrySet()) {
            filteredMap.put(ImmutableReportableVariant.builder()
                    .from(QualityOverruleFunctions.overruleVariant(entry.getKey(), hasReliablePurity))
                    .build(), entry.getValue());

        }
        return filteredMap;
    }

    @NotNull
    private static List<ReportableVariantWithNotify> overruleVariants(@NotNull List<ReportableVariant> variants,
            @NotNull Map<ReportableVariant, Boolean> notifyVariant, boolean hasReliablePurity) {
        List<ReportableVariantWithNotify> overruledVariants = Lists.newArrayList();

        for (ReportableVariant variant : variants) {
            ReportableVariant newVariant = overruleVariant(variant, hasReliablePurity);
            overruledVariants.add(ImmutableReportableVariantWithNotify.builder()
                    .variant(newVariant)
                    .notifyVariant(notifyVariant.get(newVariant))
                    .build());
        }

        return overruledVariants;
    }

    @NotNull
    private static ReportableVariant overruleVariant(@NotNull ReportableVariant variant, boolean hasReliablePurity) {
        Double totalCopyNumber = variant.totalCopyNumber();
        Double flooredCopyNumber = null;
        Long roundedCopyNumber = null;

        if (totalCopyNumber != null) {
            flooredCopyNumber = Math.max(0, totalCopyNumber);
            roundedCopyNumber = Math.round(flooredCopyNumber);
        }

        if (roundedCopyNumber != null) {
            return ImmutableReportableVariant.builder()
                    .from(variant)
                    .totalCopyNumber(hasReliablePurity && roundedCopyNumber >= 1 ? flooredCopyNumber : Double.NaN)
                    .alleleCopyNumber(hasReliablePurity && roundedCopyNumber >= 1 ? variant.alleleCopyNumber() : Double.NaN)
                    .minorAlleleCopyNumber(hasReliablePurity && roundedCopyNumber >= 1 ? variant.minorAlleleCopyNumber() : Double.NaN)
                    .biallelic(hasReliablePurity && roundedCopyNumber >= 1 ? variant.biallelic() : null)
                    .build();
        } else {
            return ImmutableReportableVariant.builder()
                    .from(variant)
                    .totalCopyNumber(Double.NaN)
                    .alleleCopyNumber(Double.NaN)
                    .minorAlleleCopyNumber(Double.NaN)
                    .biallelic(null)
                    .build();
        }
    }
}