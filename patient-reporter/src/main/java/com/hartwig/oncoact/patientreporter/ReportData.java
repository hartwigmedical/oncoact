package com.hartwig.oncoact.patientreporter;

import java.time.LocalDateTime;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.correction.Correction;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReportData {

    @NotNull
    PatientReporterData lamaPatientData();

    @Nullable
    PatientInformationResponse diagnosticSiloPatientData();

    @Nullable
    Correction correction();

    @NotNull
    String signaturePath();

    @NotNull
    String logoRVAPath();

    @NotNull
    String logoCompanyPath();

    @NotNull
    String udiDi();

    @NotNull
    LocalDateTime reportTime();
}
