package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.hartwig.oncoact.hla.HlaReporting;

import org.jetbrains.annotations.NotNull;

public final class HLAAllele {

    private HLAAllele() {
    }

    @NotNull
    public static List<HlaReporting> sort(@NotNull List<HlaReporting> alleles) {
        return alleles.stream()
                .sorted(Comparator.comparing((HlaReporting lilacReporting) -> lilacReporting.hlaAllele().gene())
                        .thenComparing(germlineAllele -> germlineAllele.hlaAllele().germlineAllele()))
                .collect(Collectors.toList());
    }
}