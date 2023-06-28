package com.hartwig.oncoact.patientreporter.qcfail;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaAllelesReportingFactory;
import com.hartwig.oncoact.orange.OrangeJson;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.failedreasondb.FailedDBFile;
import com.hartwig.oncoact.patientreporter.failedreasondb.FailedDatabase;
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
    public QCFailReport run(@NotNull PatientReporterConfig config) throws IOException {
        QCFailReason reason = config.qcFailReason();
        assert reason != null;

        String failReasonsDatabaseTsv = config.failReasonsDatabaseTsv();
        assert failReasonsDatabaseTsv != null;
        Map<String, FailedDatabase> failedDatabaseMap = FailedDBFile.buildFromTsv(failReasonsDatabaseTsv);
        FailedDatabase failedDatabase = failedDatabaseMap.get(config.qcFailReason().identifier());

        String reportReason = failedDatabase.reportReason();
        String reportExplanation = failedDatabase.reportExplanation();
        String reportExplanationDetail = failedDatabase.reportExplanationDetail();

        if (reason.equals(QCFailReason.SUFFICIENT_TCP_QC_FAILURE) || reason.equals(QCFailReason.INSUFFICIENT_TCP_DEEP_WGS)) {
            if (config.requirePipelineVersionFile()) {
                String pipelineVersionFile = config.pipelineVersionFile();
                assert pipelineVersionFile != null;
                String pipelineVersion = PipelineVersionFile.majorDotMinorVersion(pipelineVersionFile);
                PipelineVersion.checkPipelineVersion(pipelineVersion, config.expectedPipelineVersion(), config.overridePipelineVersion());
            }
        }

        String wgsPurityString = null;
        Set<PurpleQCStatus> purpleQc = Sets.newHashSet();
        boolean hasReliablePurity = false;
        HlaAllelesReportingData hlaReportingData = null;
        Map<String, List<PeachGenotype>> pharmacogeneticsMap = Maps.newHashMap();

        if (reason.isDeepWGSDataAvailable()) {
            OrangeRecord orange = OrangeJson.read(config.orangeJson());

            String formattedPurity = new DecimalFormat("#'%'").format(orange.purple().fit().purity() * 100);
            hasReliablePurity = orange.purple().fit().containsTumorCells();

            wgsPurityString = hasReliablePurity ? formattedPurity : "N/A";
            purpleQc = orange.purple().fit().qc().status();

            Set<PeachGenotype> pharmacogeneticsGenotypes = Sets.newHashSet();
            if (reason.isDeepWGSDataAvailable() && !purpleQc.contains(PurpleQCStatus.FAIL_CONTAMINATION)) {
                pharmacogeneticsGenotypes = orange.peach();
            }

            for (PeachGenotype pharmacogenetics : pharmacogeneticsGenotypes) {
                if (pharmacogeneticsMap.containsKey(pharmacogenetics.gene())) {
                    List<PeachGenotype> curent = pharmacogeneticsMap.get(pharmacogenetics.gene());
                    curent.add(pharmacogenetics);
                    pharmacogeneticsMap.put(pharmacogenetics.gene(), curent);
                } else {
                    pharmacogeneticsMap.put(pharmacogenetics.gene(), Lists.newArrayList(pharmacogenetics));
                }
            }
            hlaReportingData = HlaAllelesReportingFactory.convertToReportData(orange.lilac(), hasReliablePurity, purpleQc);
        }

        LOGGER.info("  QC status: {}", purpleQc.toString());

        return ImmutableQCFailReport.builder()
                .qsFormNumber(reason.qcFormNumber())
                .reason(reason)
                .reportReason(reportReason)
                .reportExplanation(reportExplanation)
                .reportExplanationDetail(reportExplanationDetail)
                .wgsPurityString(wgsPurityString)
                .comments(Optional.ofNullable(config.comments()))
                .isCorrectedReport(config.isCorrectedReport())
                .isCorrectedReportExtern(config.isCorrectedReportExtern())
                .signaturePath(reportData.signaturePath())
                .logoRVAPath(reportData.logoRVAPath())
                .logoCompanyPath(reportData.logoCompanyPath())
                .udiDi(reportData.udiDi())
                .pharmacogeneticsGenotypes(pharmacogeneticsMap)
                .hlaAllelesReportingData(hlaReportingData)
                .purpleQC(purpleQc)
                .reportDate(reportDate)
                .isWGSReport(true)
                .build();
    }
}
