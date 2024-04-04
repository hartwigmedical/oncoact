package com.hartwig.oncoact.patientreporter.panel;

import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PanelReporter {

    @NotNull
    private final QCFailPanelReportData reportData;

    public PanelReporter(@NotNull final QCFailPanelReportData reportData) {
        this.reportData = reportData;
    }

    @NotNull
    public PanelReport run(@NotNull String panelVCFname) throws IOException {

        return ImmutablePanelReport.builder()
                .qsFormNumber(QsFormNumber.FOR_344.number)
                .pipelineVersion(reportData.pipelineVersion())
                .VCFFilename(panelVCFname)
                .comments(reportData.comments())
                .isCorrectedReport(reportData.correctedReport())
                .isCorrectedReportExtern(reportData.correctedReportExtern())
                .signaturePath(reportData.signaturePath())
                .logoCompanyPath(reportData.logoCompanyPath())
                .lamaPatientData(reportData.lamaPatientData())
                .diagnosticSiloPatientData(reportData.diagnosticSiloPatientData())
                .reportDate(Formats.formatDate(reportData.reportTime().toLocalDate()))
                .isWGSReport(false)
                .build();
    }
}