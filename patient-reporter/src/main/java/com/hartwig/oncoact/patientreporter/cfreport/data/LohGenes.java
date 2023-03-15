package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleGeneCopyNumber;
import com.hartwig.oncoact.patientreporter.algo.ImmutableLohGenesReporting;
import com.hartwig.oncoact.patientreporter.algo.LohGenesReporting;

import com.hartwig.oncoact.patientreporter.util.Genes;
import org.jetbrains.annotations.NotNull;

public final class LohGenes {

    private LohGenes() {
    }

    public static long round(double copyNumber) {
        return Math.round(Math.max(0, copyNumber));
    }

    @NotNull
    public static List<PurpleGeneCopyNumber> sort(@NotNull List<PurpleGeneCopyNumber> purpleGeneCopyNumbers) {
        return purpleGeneCopyNumbers.stream()
                .sorted(Comparator.comparing(PurpleGeneCopyNumber::geneName))
                .collect(Collectors.toList());
    }
}
