package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.hartwig.oncoact.patientreporter.algo.InterpretPurpleGeneCopyNumbers;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

public final class LohGenes {

    private LohGenes() {
    }

    public static String round(Double copyNumber) {
        return copyNumber == null ? Formats.NA_STRING :  String.valueOf(Math.round(Math.max(0, copyNumber)));
    }

    @NotNull
    public static List<InterpretPurpleGeneCopyNumbers> sort(@NotNull List<InterpretPurpleGeneCopyNumbers> LOHPurpleGeneCopyNumbers) {
        return LOHPurpleGeneCopyNumbers.stream()
                .sorted(Comparator.comparing(InterpretPurpleGeneCopyNumbers::geneName))
                .collect(Collectors.toList());
    }
}
