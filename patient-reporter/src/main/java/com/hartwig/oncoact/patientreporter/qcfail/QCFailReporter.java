package com.hartwig.oncoact.patientreporter.qcfail;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.clinical.PatientPrimaryTumor;
import com.hartwig.oncoact.clinical.PatientPrimaryTumorFunctions;
import com.hartwig.oncoact.lims.Lims;
import com.hartwig.oncoact.lims.cohort.LimsCohortConfig;
import com.hartwig.oncoact.orange.OrangeJson;
import com.hartwig.oncoact.orange.OrangeRecord;
import com.hartwig.oncoact.orange.peach.PeachEntry;
import com.hartwig.oncoact.orange.purple.PurpleQCStatus;
import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.SampleMetadata;
import com.hartwig.oncoact.patientreporter.SampleReport;
import com.hartwig.oncoact.patientreporter.SampleReportFactory;
import com.hartwig.oncoact.patientreporter.pipeline.PipelineVersion;
import com.hartwig.oncoact.pipeline.PipelineVersionFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class QCFailReporter {

    private static final Logger LOGGER = LogManager.getLogger(QCFailReporter.class);

    @NotNull
    private final QCFailReportData reportData;
    @NotNull
    private final String reportDate;

    public QCFailReporter(@NotNull final QCFailReportData reportData, @NotNull final String reportDate) {
        this.reportData = reportData;
        this.reportDate = reportDate;
    }

    @NotNull
    public QCFailReport run(@NotNull SampleMetadata sampleMetadata, @NotNull PatientReporterConfig config) throws IOException {
        QCFailReason reason = config.qcFailReason();
        assert reason != null;

        String patientId = reportData.limsModel().patientId(sampleMetadata.tumorSampleBarcode());

        PatientPrimaryTumor patientPrimaryTumor =
                PatientPrimaryTumorFunctions.findPrimaryTumorForPatient(reportData.patientPrimaryTumors(), patientId);
        SampleReport sampleReport = SampleReportFactory.fromLimsModel(sampleMetadata,
                reportData.limsModel(),
                patientPrimaryTumor,
                config.allowDefaultCohortConfig());

        if (reason.equals(QCFailReason.SUFFICIENT_TCP_QC_FAILURE) || reason.equals(QCFailReason.INSUFFICIENT_TCP_DEEP_WGS)) {
            if (config.requirePipelineVersionFile()) {
                String pipelineVersionFile = config.pipelineVersionFile();
                assert pipelineVersionFile != null;
                String pipelineVersion = PipelineVersionFile.majorDotMinorVersion(pipelineVersionFile);
                PipelineVersion.checkPipelineVersion(pipelineVersion, config.expectedPipelineVersion(), config.overridePipelineVersion());
            }
        }

        LimsCohortConfig cohort = sampleReport.cohort();

        if (cohort.cohortId().isEmpty()) {
            throw new IllegalStateException("QC fail report not supported for non-cancer study samples: " + sampleMetadata.tumorSampleId());
        }

        String wgsPurityString = null;
        Set<PurpleQCStatus> purpleQc = Sets.newHashSet();
        Set<PeachEntry> pharmacogeneticsGenotypesOverrule = Sets.newHashSet();
        if (reason.isDeepWGSDataAvailable()) {
            LOGGER.info("Loading ORANGE data from {}", new File(config.orangeJson()).getParent());
            OrangeRecord orange = OrangeJson.read(config.orangeJson());

            String formattedPurity = new DecimalFormat("#'%'").format(orange.purple().fit().purity() * 100);

            wgsPurityString = orange.purple().fit().containsTumorCells() ? formattedPurity : Lims.PURITY_NOT_RELIABLE_STRING;
            purpleQc = orange.purple().fit().qcStatus();

            pharmacogeneticsGenotypesOverrule = sampleReport.reportPharmogenetics() ? orange.peach().entries() : Sets.newHashSet();
        }

        LOGGER.info("  QC status: {}", purpleQc.toString());

        Map<String, List<PeachEntry>> pharmacogeneticsMap = Maps.newHashMap();
        for (PeachEntry pharmacogenetics : pharmacogeneticsGenotypesOverrule) {
            if (pharmacogeneticsMap.containsKey(pharmacogenetics.gene())) {
                List<PeachEntry> current = pharmacogeneticsMap.get(pharmacogenetics.gene());
                current.add(pharmacogenetics);
                pharmacogeneticsMap.put(pharmacogenetics.gene(), current);
            } else {
                pharmacogeneticsMap.put(pharmacogenetics.gene(), com.google.common.collect.Lists.newArrayList(pharmacogenetics));
            }
        }

        return ImmutableQCFailReport.builder()
                .sampleReport(sampleReport)
                .qsFormNumber(reason.qcFormNumber())
                .reason(reason)
                .wgsPurityString(wgsPurityString)
                .comments(Optional.ofNullable(config.comments()))
                .isCorrectedReport(config.isCorrectedReport())
                .isCorrectedReportExtern(config.isCorrectedReportExtern())
                .signaturePath(reportData.signaturePath())
                .logoRVAPath(reportData.logoRVAPath())
                .logoCompanyPath(reportData.logoCompanyPath())
                .udiDi(reportData.udiDi())
                .pharmacogeneticsGenotypes(pharmacogeneticsMap)
                .purpleQC(purpleQc)
                .reportDate(reportDate)
                .isWGSReport(true)
                .build();
    }
}
