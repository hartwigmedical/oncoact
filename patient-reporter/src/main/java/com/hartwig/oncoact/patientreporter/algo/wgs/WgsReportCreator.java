package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.cuppa.CuppaData;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaAllelesReportingFactory;
import com.hartwig.oncoact.orange.OrangeJson;
import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.algo.*;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.lama.LamaInterpretation;
import com.hartwig.oncoact.patientreporter.model.WgsReport;
import com.hartwig.oncoact.protect.ImmutableProtectEvidence;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.ProtectEvidenceFile;
import com.hartwig.oncoact.util.Formats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WgsReportCreator {

    private static final Logger LOGGER = LogManager.getLogger(WgsReportCreator.class);
    private final AnalysedReportData reportData;

    public WgsReportCreator(@NotNull final AnalysedReportData reportData) {
        this.reportData = reportData;
    }

    @NotNull
    public WgsReport run(@NotNull PatientReporterConfig config) throws IOException {
        String roseTsvFile = config.roseTsv();

        String pipelineVersion = config.pipelineVersion();

        GenomicAnalyzer genomicAnalyzer = new GenomicAnalyzer(reportData.germlineReportingModel(), reportData.clinicalTranscriptsModel());

        OrangeRecord orange = OrangeJson.read(config.orangeJson());
        List<ProtectEvidence> reportableEvidence = extractReportableEvidenceItems(config.protectEvidenceTsv());

        boolean flagGermlineOnReport = reportData.lamaPatientData().getReportSettings().getFlagGermlineOnReport();
        boolean reportGermlineOnReport = reportData.lamaPatientData().getReportSettings().getReportGermline();

        GenomicAnalysis genomicAnalysis = genomicAnalyzer.run(orange, reportableEvidence, flagGermlineOnReport);
        GenomicAnalysis filteredAnalysis = ConsentFilterFunctions.filter(genomicAnalysis, flagGermlineOnReport, reportGermlineOnReport);
        GenomicAnalysis overruledAnalysis = QualityOverruleFunctions.overrule(filteredAnalysis);
        GenomicAnalysis curatedAnalysis = CurationFunctions.curate(overruledAnalysis);

        String qcForm = determineForNumber(curatedAnalysis.hasReliablePurity(), curatedAnalysis.impliedPurity());

        Set<PeachGenotype> pharmacogeneticsGenotypes =
                curatedAnalysis.purpleQCStatus().contains(PurpleQCStatus.FAIL_CONTAMINATION) ? Sets.newHashSet() : orange.peach();

        Map<String, List<PeachGenotype>> pharmacogeneticsGenotypesMap = Maps.newHashMap();
        for (PeachGenotype pharmacogeneticsGenotype : pharmacogeneticsGenotypes) {
            if (pharmacogeneticsGenotypesMap.containsKey(pharmacogeneticsGenotype.gene())) {
                List<PeachGenotype> current = pharmacogeneticsGenotypesMap.get(pharmacogeneticsGenotype.gene());
                current.add(pharmacogeneticsGenotype);
                pharmacogeneticsGenotypesMap.put(pharmacogeneticsGenotype.gene(), current);
            } else {
                pharmacogeneticsGenotypesMap.put(pharmacogeneticsGenotype.gene(), Lists.newArrayList(pharmacogeneticsGenotype));
            }
        }

        HlaAllelesReportingData hlaReportingData = HlaAllelesReportingFactory.convertToReportData(orange.lilac(),
                curatedAnalysis.hasReliablePurity(),
                curatedAnalysis.purpleQCStatus());

        CuppaData cuppa = orange.cuppa();

        WgsReport report = WgsReport.builder()
                .reportDate(getReportDate())
                .receiver(getReceiver())
                .summary(SummaryCreator.createSummary(curatedAnalysis, pharmacogeneticsGenotypesMap, hlaReportingData, cuppa, reportData.correction(), roseTsvFile))
                .tumorSample(TumorSampleCreator.createTumorSample(reportData.lamaPatientData(), reportData.diagnosticSiloPatientData()))
                .build();

        // TODO printReportState(report);

        return report;
    }

    @NotNull
    private static List<ProtectEvidence> extractReportableEvidenceItems(@NotNull String protectEvidenceTsv) throws IOException {
        LOGGER.info("Loading PROTECT data from {}", new File(protectEvidenceTsv).getParent());
        List<ProtectEvidence> evidences = ProtectEvidenceFile.read(protectEvidenceTsv);

        List<ProtectEvidence> reportableEvidenceItems = Lists.newArrayList();
        for (ProtectEvidence evidence : evidences) {
            if (evidence.reported()) {
                reportableEvidenceItems.add(ImmutableProtectEvidence.builder().from(evidence).build());
            }
        }
        LOGGER.info(" Loaded {} reportable evidence items from {}", reportableEvidenceItems.size(), protectEvidenceTsv);

        return reportableEvidenceItems;
    }

    @VisibleForTesting
    static String determineForNumber(boolean hasReliablePurity, double purity) {
        return hasReliablePurity && purity > ReportResources.PURITY_CUTOFF
                ? QsFormNumber.FOR_080.display()
                : QsFormNumber.FOR_209.display();
    }

    @NotNull
    private String getReceiver() {
        return LamaInterpretation.hospitalContactReport(reportData.lamaPatientData());
    }

    @NotNull
    private String getReportDate() {
        return Formats.formatDate(reportData.reportTime().toLocalDate());
    }
}