package com.hartwig.oncoact.patientreporter;

import java.time.LocalDateTime;
import java.util.Optional;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PanelData {

    @NotNull
    PatientReporterData lamaPatientData();

    @Nullable
    PatientInformationResponse diagnosticSiloPatientData();

    @NotNull
    String signaturePath();

    @NotNull
    String logoCompanyPath();

    @NotNull
    Optional<String> comments();

    boolean correctedReport();

    boolean correctedReportExtern();

    @NotNull
    String pipelineVersion();

    @NotNull
    LocalDateTime reportTime();
}
