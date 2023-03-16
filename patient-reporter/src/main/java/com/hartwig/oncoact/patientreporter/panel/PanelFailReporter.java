package com.hartwig.oncoact.patientreporter.panel;

import java.util.Optional;

import com.hartwig.oncoact.clinical.PatientPrimaryTumor;
import com.hartwig.oncoact.clinical.PatientPrimaryTumorFunctions;
import com.hartwig.oncoact.lims.cohort.LimsCohortConfig;
import com.hartwig.oncoact.reporting.SampleMetadata;
import com.hartwig.oncoact.reporting.SampleReport;
import com.hartwig.oncoact.patientreporter.SampleReportFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PanelFailReporter {

    @NotNull
    private final QCFailPanelReportData reportData;
    @NotNull
    private final String reportDate;

    public PanelFailReporter(@NotNull final QCFailPanelReportData reportData, @NotNull final String reportDate) {
        this.reportData = reportData;
        this.reportDate = reportDate;
    }

    @NotNull
    public PanelFailReport run(@NotNull SampleMetadata sampleMetadata, @Nullable String comments, boolean correctedReport,
            boolean correctedReportExtern, @Nullable PanelFailReason reason, boolean allowDefaultCohortConfig)  {
        String patientId = reportData.limsModel().patientId(sampleMetadata.tumorSampleBarcode());

        PatientPrimaryTumor patientPrimaryTumor =
                PatientPrimaryTumorFunctions.findPrimaryTumorForPatient(reportData.patientPrimaryTumors(), patientId);
        SampleReport sampleReport =
                SampleReportFactory.fromLimsModel(sampleMetadata, reportData.limsModel(), patientPrimaryTumor, allowDefaultCohortConfig);


        LimsCohortConfig cohort = sampleReport.cohort();

        if (cohort.cohortId().isEmpty()) {
            throw new IllegalStateException("QC fail report not supported for non-cancer study samples: " + sampleMetadata.tumorSampleId());
        }

        return ImmutablePanelFailReport.builder()
                .sampleReport(sampleReport)
                .qsFormNumber(reason.qcFormNumber())
                .comments(Optional.ofNullable(comments))
                .isCorrectedReport(correctedReport)
                .isCorrectedReportExtern(correctedReportExtern)
                .signaturePath(reportData.signaturePath())
                .logoCompanyPath(reportData.logoCompanyPath())
                .reportDate(reportDate)
                .isWGSReport(false)
                .panelFailReason(reason)
                .build();
    }
}