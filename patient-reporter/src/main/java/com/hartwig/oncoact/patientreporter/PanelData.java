package com.hartwig.oncoact.patientreporter;

import java.util.List;

import com.hartwig.oncoact.common.clinical.PatientPrimaryTumor;
import com.hartwig.oncoact.common.lims.Lims;

import org.jetbrains.annotations.NotNull;

public interface PanelData {

    @NotNull
    List<PatientPrimaryTumor> patientPrimaryTumors();

    @NotNull
    Lims limsModel();

    @NotNull
    String signaturePath();

    @NotNull
    String logoCompanyPath();
}
