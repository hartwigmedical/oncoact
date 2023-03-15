package com.hartwig.oncoact.patientreporter;

import com.hartwig.oncoact.lims.Lims;

import org.jetbrains.annotations.NotNull;

public interface ReportData {

    @NotNull
    String patientPrimaryTumors();

    @NotNull
    Lims limsModel();

    @NotNull
    String signaturePath();

    @NotNull
    String logoRVAPath();

    @NotNull
    String logoCompanyPath();

    @NotNull
    String udiDi();
}
