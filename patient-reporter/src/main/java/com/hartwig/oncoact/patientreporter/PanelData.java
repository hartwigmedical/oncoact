package com.hartwig.oncoact.patientreporter;

import java.util.List;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.clinical.PatientPrimaryTumor;
import com.hartwig.oncoact.lims.Lims;

import org.jetbrains.annotations.NotNull;

public interface PanelData {

    @NotNull
    List<PatientPrimaryTumor> patientPrimaryTumors();

    @NotNull
    Lims limsModel();

    @NotNull
    PatientReporterData patientReporterData();

    @NotNull
    String signaturePath();

    @NotNull
    String logoCompanyPath();
}
