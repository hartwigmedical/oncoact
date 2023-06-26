package com.hartwig.oncoact.patientreporter;

import com.hartwig.lama.client.model.PatientReporterData;

import com.hartwig.silo.client.model.PatientInformationResponse;
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
}
