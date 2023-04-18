package com.hartwig.oncoact.patientreporter;

import java.util.List;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.clinical.PatientPrimaryTumor;
import com.hartwig.oncoact.lims.Lims;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReportData {

    @NotNull
    List<PatientPrimaryTumor> patientPrimaryTumors();

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
