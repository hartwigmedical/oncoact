package com.hartwig.oncoact.patientreporter.panel;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.hartwig.oncoact.patientreporter.qcfail.FailedDBFile;
import com.hartwig.oncoact.patientreporter.qcfail.FailedDatabase;
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
                               boolean correctedReportExtern, @Nullable PanelFailReason reason, @Nullable String failDbTsv) throws IOException {

        assert failDbTsv != null;
        Map<String, FailedDatabase> failedDatabaseMap = FailedDBFile.buildFromTsv(failDbTsv);
        FailedDatabase failedDatabase = failedDatabaseMap.get(reason.identifier());

        String reportReason = failedDatabase.reportReason();
        String reportExplanation = failedDatabase.reportExplanation();
        String reportExplanationDetail = failedDatabase.reportExplanationDetail();

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
                .reportReason(reportReason)
                .reportExplanation(reportExplanation)
                .reportExplanationDetail(reportExplanationDetail)
                .build();
    }
}