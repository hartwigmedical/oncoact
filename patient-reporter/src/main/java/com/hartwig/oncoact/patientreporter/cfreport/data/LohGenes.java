package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.hartwig.oncoact.patientreporter.algo.LohGenesReporting;

import org.jetbrains.annotations.NotNull;

public final class LohGenes {

    private LohGenes() {
    }

    @NotNull
    public static List<LohGenesReporting> sort(@NotNull List<LohGenesReporting> lohGenes) {
        return lohGenes.stream()
                .sorted(Comparator.comparing((LohGenesReporting lohGenesReporting) -> lohGenesReporting.gene()))
                .collect(Collectors.toList());
    }
}
