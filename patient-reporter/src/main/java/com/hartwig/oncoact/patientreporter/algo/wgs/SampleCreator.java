package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.oncoact.patientreporter.model.Sample;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

class SampleCreator {
    static Sample createSample(
            @NotNull String barcode,
            @NotNull LocalDate date
    ) {
        return Sample.builder()
                .sampleBarcode(barcode)
                .arrivalDate(date)
                .build();
    }
}