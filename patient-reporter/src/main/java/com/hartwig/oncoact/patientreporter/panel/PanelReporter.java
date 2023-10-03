package com.hartwig.oncoact.patientreporter.panel;

import java.io.IOException;

import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.pipeline.PipelineVersion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PanelReporter {

    @NotNull
    private final QCFailPanelReportData reportData;
    @NotNull
    private final String reportDate;

    public PanelReporter(@NotNull final QCFailPanelReportData reportData, @NotNull final String reportDate) {
        this.reportData = reportData;
        this.reportDate = reportDate;
    }

    @NotNull
    public PanelReport run(@NotNull String panelVCFname) throws IOException {

        return ImmutablePanelReport.builder()
                .qsFormNumber(QsFormNumber.FOR_344.display())
                .pipelineVersion(reportData.pipelineVersion())
                .VCFFilename(panelVCFname)
                .comments(reportData.comments())
                .isCorrectedReport(reportData.correctedReport())
                .isCorrectedReportExtern(reportData.correctedReportExtern())
                .signaturePath(reportData.signaturePath())
                .logoCompanyPath(reportData.logoCompanyPath())
                .lamaPatientData(reportData.lamaPatientData())
                .reportDate(reportDate)
                .isWGSReport(false)
                .build();
    }
}