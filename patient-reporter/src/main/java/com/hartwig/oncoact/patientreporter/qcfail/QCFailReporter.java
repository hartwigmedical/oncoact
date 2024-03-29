package com.hartwig.oncoact.patientreporter.qcfail;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaAllelesReportingFactory;
import com.hartwig.oncoact.orange.OrangeJson;
import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.correction.Correction;
import com.hartwig.oncoact.patientreporter.failedreasondb.FailedReason;
import com.hartwig.oncoact.util.Formats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class QCFailReporter {

    private static final Logger LOGGER = LogManager.getLogger(QCFailReporter.class);

    @NotNull
    private final QCFailReportData reportData;

    public QCFailReporter(@NotNull final QCFailReportData reportData) {
        this.reportData = reportData;
    }

    @NotNull
    public QCFailReport run(@NotNull PatientReporterConfig config) throws IOException {
        QCFailReason reason = config.qcFailReason();
        assert reason != null;

        FailedReason failedDatabase = FailedReason.builder()
                .reportReason(reason.reportReason())
                .reportExplanation(reason.reportExplanation())
                .sampleFailReasonComment(config.sampleFailReasonComment())
                .build();

        String pipelineVersion = null;
        if (reason.isDeepWGSDataAvailable()) {
            pipelineVersion = config.pipelineVersion();
        }

        String wgsPurityString = null;
        Set<PurpleQCStatus> purpleQc = null;
        boolean hasReliablePurity;
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

        LOGGER.info("  QC status: {}", Objects.toString(purpleQc));

        return QCFailReport.builder()
                .qsFormNumber(reason.qcFormNumber())
                .reason(reason)
                .failExplanation(failedDatabase)
                .wgsPurityString(wgsPurityString)
                .comments(Optional.ofNullable(reportData.correction()).map(Correction::comments))
                .isCorrectedReport(Optional.ofNullable(reportData.correction()).map(Correction::isCorrectedReport).orElse(false))
                .isCorrectedReportExtern(Optional.ofNullable(reportData.correction())
                        .map(Correction::isCorrectedReportExtern)
                        .orElse(false))
                .lamaPatientData(reportData.lamaPatientData())
                .diagnosticSiloPatientData(reportData.diagnosticSiloPatientData())
                .signaturePath(reportData.signaturePath())
                .logoRVAPath(reportData.logoRVAPath())
                .logoCompanyPath(reportData.logoCompanyPath())
                .udiDi(reportData.udiDi())
                .pharmacogeneticsGenotypes(pharmacogeneticsMap)
                .hlaAllelesReportingData(hlaReportingData)
                .purpleQC(purpleQc)
                .reportDate(Formats.formatDate(reportData.reportTime().toLocalDate()))
                .pipelineVersion(pipelineVersion)
                .build();
    }
}