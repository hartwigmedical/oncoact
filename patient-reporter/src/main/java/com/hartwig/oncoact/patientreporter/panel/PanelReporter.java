package com.hartwig.oncoact.patientreporter.panel;

import java.io.IOException;
import java.util.Optional;

import com.hartwig.oncoact.lims.cohort.LimsCohortConfig;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.SampleMetadata;
import com.hartwig.oncoact.patientreporter.SampleReport;
import com.hartwig.oncoact.patientreporter.SampleReportFactory;
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
    public PanelReport run(@NotNull SampleMetadata sampleMetadata, @Nullable String comments, boolean correctedReport,
            boolean correctedReportExtern, @NotNull String expectedPipelineVersion, boolean overridePipelineVersion,
            @Nullable String pipelineVersionFile, boolean requirePipelineVersionFile, @NotNull String panelVCFname,
            boolean allowDefaultCohortConfig) throws IOException {

        SampleReport sampleReport =
                SampleReportFactory.fromLimsModel(sampleMetadata, reportData.limsModel(), allowDefaultCohortConfig);

        String pipelineVersion = null;
        if (requirePipelineVersionFile) {
            assert pipelineVersionFile != null;
            pipelineVersion = PipelineVersionFile.majorDotMinorVersion(pipelineVersionFile);
            PipelineVersion.checkPipelineVersion(pipelineVersion, expectedPipelineVersion, overridePipelineVersion);
        }

        LimsCohortConfig cohort = sampleReport.cohort();

        if (cohort.cohortId().isEmpty()) {
            throw new IllegalStateException("QC fail report not supported for non-cancer study samples: " + sampleMetadata.tumorSampleId());
        }

        return ImmutablePanelReport.builder()
                .sampleReport(sampleReport)
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