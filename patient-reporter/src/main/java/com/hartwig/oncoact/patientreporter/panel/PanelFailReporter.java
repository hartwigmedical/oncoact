package com.hartwig.oncoact.patientreporter.panel;

import java.io.IOException;

import com.hartwig.oncoact.patientreporter.failedreasondb.FailedReason;
import com.hartwig.oncoact.util.Formats;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PanelFailReporter {

    @NotNull
    private final QCFailPanelReportData reportData;

    public PanelFailReporter(@NotNull final QCFailPanelReportData reportData) {
        this.reportData = reportData;
    }

    @NotNull
    public PanelFailReport run(@Nullable PanelFailReason reason, @Nullable String sampleFailReasonComment) throws IOException {
        assert reason != null;

        FailedReason failedDatabase = FailedReason.builder()
                .reportReason(reason.reportReason())
                .reportExplanation(reason.reportExplanation())
                .sampleFailReasonComment(sampleFailReasonComment)
                .build();

        return PanelFailReport.builder()
                .qsFormNumber(reason.qcFormNumber())
                .comments(reportData.comments())
                .isCorrectedReport(reportData.correctedReport())
                .isCorrectedReportExtern(reportData.correctedReportExtern())
                .signaturePath(reportData.signaturePath())
                .logoCompanyPath(reportData.logoCompanyPath())
                .reportDate(Formats.formatDate(reportData.reportTime().toLocalDate()))
                .isWGSReport(false)
                .panelFailReason(reason)
                .failExplanation(failedDatabase)
                .diagnosticSiloPatientData(reportData.diagnosticSiloPatientData())
                .lamaPatientData(reportData.lamaPatientData())
                .build();
    }
}