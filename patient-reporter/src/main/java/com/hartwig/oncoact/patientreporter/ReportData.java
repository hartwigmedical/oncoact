package com.hartwig.oncoact.patientreporter;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.lims.Lims;

import org.jetbrains.annotations.NotNull;

public interface ReportData {

    @NotNull
    Lims limsModel();

    @NotNull
    PatientReporterData patientReporterData();

    @NotNull
    String signaturePath();

    @NotNull
    String logoRVAPath();

    @NotNull
    String logoCompanyPath();

    @NotNull
    String udiDi();
}
