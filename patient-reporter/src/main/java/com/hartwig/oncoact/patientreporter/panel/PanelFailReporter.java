package com.hartwig.oncoact.patientreporter.panel;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.hartwig.oncoact.patientreporter.failedreasondb.FailExplanationReporting;
import com.hartwig.oncoact.patientreporter.failedreasondb.FailedDBFile;
import com.hartwig.oncoact.patientreporter.failedreasondb.FailedDatabase;
import com.hartwig.oncoact.patientreporter.failedreasondb.ImmutableFailExplanationReporting;
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
                               boolean correctedReportExtern, @Nullable PanelFailReason reason, @Nullable String failReasonsDatabaseTsv) throws IOException {

        assert failReasonsDatabaseTsv != null;
        Map<String, FailedDatabase> failedDatabaseMap = FailedDBFile.buildFromTsv(failReasonsDatabaseTsv);
        FailedDatabase failedDatabase = failedDatabaseMap.get(reason.identifier());

        FailExplanationReporting failExplanationReporting = ImmutableFailExplanationReporting.builder()
                .reportReason(failedDatabase.reportReason())
                .reportExplanation(failedDatabase.reportExplanation())
                .reportExplanationDetail(failedDatabase.reportExplanationDetail())
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
                .failExplanationReporting(failExplanationReporting)
                .build();
    }
}