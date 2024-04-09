package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.oncoact.patientreporter.algo.InterpretPurpleGeneCopyNumbers;
import com.hartwig.oncoact.patientreporter.model.LohEvent;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class LohCreator {

    static List<LohEvent> createLohEventHrd(
            @NotNull List<InterpretPurpleGeneCopyNumbers> suspectGeneCopyNumbersWithLOH,
            @NotNull ChordStatus status
    ) {
        List<LohEvent> lohEvents = Lists.newArrayList();
        if (status == ChordStatus.HR_DEFICIENT) {
            for (InterpretPurpleGeneCopyNumbers copyNumbers : sort(suspectGeneCopyNumbersWithLOH)) {
                lohEvents.add(LohEvent.builder()
                        .location(copyNumbers.chromosome() + copyNumbers.chromosomeBand())
                        .gene(copyNumbers.geneName())
                        .tumorMinorAlleleCopies(roundCopyNumber(copyNumbers.minMinorAlleleCopyNumber()))
                        .tumorCopies(roundCopyNumber((copyNumbers.minCopyNumber())))
                        .build());
            }
        }
        return lohEvents;
    }

    @NotNull
    public static List<InterpretPurpleGeneCopyNumbers> sort(@NotNull List<InterpretPurpleGeneCopyNumbers> LOHPurpleGeneCopyNumbers) {
        return LOHPurpleGeneCopyNumbers.stream()
                .sorted(Comparator.comparing(InterpretPurpleGeneCopyNumbers::geneName))
                .collect(Collectors.toList());
    }

    static List<LohEvent> createLohEventMSI(
            @NotNull List<InterpretPurpleGeneCopyNumbers> suspectGeneCopyNumbersWithLOH,
            @NotNull PurpleMicrosatelliteStatus status
    ) {
        List<LohEvent> lohEvents = Lists.newArrayList();
        if (status == PurpleMicrosatelliteStatus.MSI) {
            for (InterpretPurpleGeneCopyNumbers copyNumbers : sort(suspectGeneCopyNumbersWithLOH)) {
                lohEvents.add(LohEvent.builder()
                        .location(copyNumbers.chromosome() + copyNumbers.chromosomeBand())
                        .gene(copyNumbers.geneName())
                        .tumorMinorAlleleCopies(roundCopyNumber(copyNumbers.minMinorAlleleCopyNumber()))
                        .tumorCopies(roundCopyNumber((copyNumbers.minCopyNumber())))
                        .build());
            }
        }
        return lohEvents;
    }

    public static String roundCopyNumber(Double copyNumber) {
        return copyNumber == null ? Formats.NA_STRING : String.valueOf(Math.round(Math.max(0, copyNumber)));
    }
}