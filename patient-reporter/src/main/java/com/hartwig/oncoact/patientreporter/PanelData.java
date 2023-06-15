package com.hartwig.oncoact.patientreporter;

import com.hartwig.lama.client.model.PatientReporterData;

import org.jetbrains.annotations.NotNull;

public interface PanelData {

    @NotNull
    PatientReporterData lamaPatientData();

    @NotNull
    String signaturePath();

    @NotNull
    String logoCompanyPath();
}
