package com.hartwig.oncoact.patientreporter.model;

import org.jetbrains.annotations.NotNull;

public interface WgsPatientReport {

    @NotNull
    String reportDate();

    @NotNull
    String receiver();

    @NotNull
    TumorSample tumorSample();

    @NotNull
    Sample referenceSample();

    @NotNull
    Version version();
}