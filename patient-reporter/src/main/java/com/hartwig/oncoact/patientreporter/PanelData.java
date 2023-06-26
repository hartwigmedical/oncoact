package com.hartwig.oncoact.patientreporter;

import com.hartwig.lama.client.model.PatientReporterData;

import com.hartwig.silo.client.model.PatientInformationResponse;
import org.jetbrains.annotations.NotNull;

public interface PanelData {

    @NotNull
    PatientReporterData lamaPatientData();

    @NotNull
    PatientInformationResponse diagnosticSiloPatientData();

    @NotNull
    String signaturePath();

    @NotNull
    String logoCompanyPath();
}
