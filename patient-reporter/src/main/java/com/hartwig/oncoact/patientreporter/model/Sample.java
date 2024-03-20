package com.hartwig.oncoact.patientreporter.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public abstract class Sample {
    @NotNull
    public abstract LocalDate arrivalDate();

    @NotNull
    public abstract String sampleBarcode();
}
