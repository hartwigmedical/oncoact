package com.hartwig.oncoact.patientreporter.algo;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.PurpleGeneCopyNumber;

import org.jetbrains.annotations.NotNull;

public class InterpretPurpleGeneCopyNumbersFactory {

    private InterpretPurpleGeneCopyNumbersFactory() {
    }

    @NotNull
    public static List<InterpretPurpleGeneCopyNumbers> convert(@NotNull List<PurpleGeneCopyNumber> suspectGeneCopyNumbersWithLOH) {
        List<InterpretPurpleGeneCopyNumbers> InterpretLOHGenes = Lists.newArrayList();

        for (PurpleGeneCopyNumber LOHGene : suspectGeneCopyNumbersWithLOH) {
            InterpretLOHGenes.add(ImmutableInterpretPurpleGeneCopyNumbers.builder()
                    .chromosome(LOHGene.chromosome())
                    .chromosomeBand(LOHGene.chromosomeBand())
                    .geneName(LOHGene.gene())
                    .minCopyNumber(LOHGene.minCopyNumber())
                    .minMinorAlleleCopyNumber(LOHGene.minMinorAlleleCopyNumber())
                    .build());
        }
        return InterpretLOHGenes;
    }
}
