package com.hartwig.oncoact.patientreporter.panel;

import java.util.Optional;

import com.hartwig.oncoact.clinical.PatientPrimaryTumor;
import com.hartwig.oncoact.clinical.PatientPrimaryTumorFunctions;
import com.hartwig.oncoact.lims.cohort.LimsCohortConfig;
import com.hartwig.oncoact.patientreporter.SampleMetadata;
import com.hartwig.oncoact.patientreporter.SampleReport;
import com.hartwig.oncoact.patientreporter.SampleReportFactory;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReporter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PanelFailReporter {

    private static final Logger LOGGER = LogManager.getLogger(QCFailReporter.class);

    @NotNull
    private final QCFailPanelReportData reportData;
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
                .isWGSreport(false)
                .panelFailReason(reason)
                .build();
    }
}