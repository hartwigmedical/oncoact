package com.hartwig.oncoact.patientreporter;

import com.hartwig.lama.client.model.PatientReporterData;

import org.jetbrains.annotations.NotNull;

public interface ReportData {

    @NotNull
    PatientReporterData lamaPatientData();

    @NotNull
    String signaturePath();

    @NotNull
    String logoRVAPath();

    @NotNull
    String logoCompanyPath();

    @NotNull
    String udiDi();
}
