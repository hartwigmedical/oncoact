package com.hartwig.oncoact.patientreporter.model;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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

    @NotNull
    String user();

    @NotNull
    Optional<String> comments();
}