package com.hartwig.oncoact.patientreporter.panel;

import java.io.IOException;
import java.util.Optional;

import com.hartwig.oncoact.patientreporter.failedreasondb.FailedReason;
import com.hartwig.oncoact.patientreporter.failedreasondb.ImmutableFailedReason;
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
    public PanelFailReport run(@Nullable String comments, boolean correctedReport,
                               boolean correctedReportExtern, @Nullable PanelFailReason reason, @Nullable String sampleFailReasonComment) throws IOException {
        assert reason != null;

        FailedReason failedDatabase = ImmutableFailedReason.builder()
                .reportReason(reason.reportReason())
                .reportExplanation(reason.reportExplanation())
                .reportExplanationDetail(reason.reportExplanationDetail())
                .sampleFailReasonComment(sampleFailReasonComment)
                .build();


        return ImmutablePanelFailReport.builder()
                .qsFormNumber(reason.qcFormNumber())
                .comments(Optional.ofNullable(comments))
                .isCorrectedReport(correctedReport)
                .isCorrectedReportExtern(correctedReportExtern)
                .signaturePath(reportData.signaturePath())
                .logoCompanyPath(reportData.logoCompanyPath())
                .reportDate(reportDate)
                .isWGSReport(false)
                .panelFailReason(reason)
                .failExplanation(failedDatabase)
                .build();
    }
}