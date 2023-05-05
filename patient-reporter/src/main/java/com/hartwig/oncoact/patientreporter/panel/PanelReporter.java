package com.hartwig.oncoact.patientreporter.panel;

import java.io.IOException;
import java.util.Optional;

import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.pipeline.PipelineVersion;
import com.hartwig.oncoact.pipeline.PipelineVersionFile;

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
    public PanelReport run(@Nullable String comments, boolean correctedReport,
            boolean correctedReportExtern, @NotNull String expectedPipelineVersion, boolean overridePipelineVersion,
            @Nullable String pipelineVersionFile, boolean requirePipelineVersionFile, @NotNull String panelVCFname,
            boolean allowDefaultCohortConfig) throws IOException {

        String pipelineVersion = null;
        if (requirePipelineVersionFile) {
            assert pipelineVersionFile != null;
            pipelineVersion = PipelineVersionFile.majorDotMinorVersion(pipelineVersionFile);
            PipelineVersion.checkPipelineVersion(pipelineVersion, expectedPipelineVersion, overridePipelineVersion);
        }

        return ImmutablePanelReport.builder()
                .qsFormNumber(QsFormNumber.FOR_344.display())
                .pipelineVersion(pipelineVersion)
                .VCFFilename(panelVCFname)
                .comments(Optional.ofNullable(comments))
                .isCorrectedReport(correctedReport)
                .isCorrectedReportExtern(correctedReportExtern)
                .signaturePath(reportData.signaturePath())
                .logoCompanyPath(reportData.logoCompanyPath())
                .reportDate(reportDate)
                .isWGSReport(false)
                .build();
    }
}