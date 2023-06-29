package com.hartwig.oncoact.patientreporter.algo;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.lama.client.model.TumorType;
import com.hartwig.oncoact.cuppa.MolecularTissueOriginReporting;
import com.hartwig.oncoact.cuppa.MolecularTissueOriginReportingFactory;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaAllelesReportingFactory;
import com.hartwig.oncoact.orange.OrangeJson;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.hmftools.datamodel.cuppa.CuppaPrediction;
import com.hartwig.hmftools.datamodel.cuppa.CuppaData;
import com.hartwig.hmftools.datamodel.cuppa.ImmutableCuppaPrediction;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.pipeline.PipelineVersion;
import com.hartwig.oncoact.pipeline.PipelineVersionFile;
import com.hartwig.oncoact.protect.ImmutableProtectEvidence;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.ProtectEvidenceFile;
import com.hartwig.oncoact.rose.RoseConclusionFile;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantSource;

import com.hartwig.serve.datamodel.ImmutableTreatment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class AnalysedPatientReporter {

    private static final Logger LOGGER = LogManager.getLogger(AnalysedPatientReporter.class);

    @NotNull
    private final AnalysedReportData reportData;
    @NotNull
    private final String reportDate;

    public AnalysedPatientReporter(@NotNull final AnalysedReportData reportData, @NotNull final String reportDate) {
        this.reportData = reportData;
        this.reportDate = reportDate;
    }

    @NotNull
    public AnalysedPatientReport run(@NotNull PatientReporterConfig config) throws IOException {

        String roseTsvFile = config.roseTsv();
        String clinicalSummary = config.addRose() && roseTsvFile != null ? RoseConclusionFile.read(roseTsvFile) : Strings.EMPTY;

        String specialRemark = reportData.specialRemarkModel().findSpecialRemarkForSample(reportData.lamaPatientData().getTumorSampleId());

        String pipelineVersion = Strings.EMPTY;
        if (config.requirePipelineVersionFile()) {
            String pipelineVersionFile = config.pipelineVersionFile();
            assert pipelineVersionFile != null;
            pipelineVersion = PipelineVersionFile.majorDotMinorVersion(pipelineVersionFile);
            PipelineVersion.checkPipelineVersion(pipelineVersion, config.expectedPipelineVersion(), config.overridePipelineVersion());
        }

        GenomicAnalyzer genomicAnalyzer = new GenomicAnalyzer(reportData.germlineReportingModel());

        OrangeRecord orange = OrangeJson.read(config.orangeJson());
        List<ProtectEvidence> reportableEvidence = extractReportableEvidenceItems(config.protectEvidenceTsv());

        boolean flagGermlineOnReport = reportData.lamaPatientData().getReportSettings().getFlagGermlineOnReport();
        GenomicAnalysis genomicAnalysis = genomicAnalyzer.run(orange, reportableEvidence, flagGermlineOnReport);
        GenomicAnalysis filteredAnalysis = ConsentFilterFunctions.filter(genomicAnalysis, flagGermlineOnReport);
        GenomicAnalysis overruledAnalysis = QualityOverruleFunctions.overrule(filteredAnalysis);
        GenomicAnalysis curatedAnalysis = CurationFunctions.curate(overruledAnalysis);

        String qcForm = determineForNumber(curatedAnalysis.hasReliablePurity(), curatedAnalysis.impliedPurity());

        CuppaPrediction best = bestPrediction(orange.cuppa());
        MolecularTissueOriginReporting molecularTissueOriginReporting = MolecularTissueOriginReportingFactory.create(best);
        LOGGER.info(" Predicted cancer type '{}' with likelihood {}", best.cancerType(), best.likelihood());

        Set<PeachGenotype> pharmacogeneticsGenotypes = curatedAnalysis.purpleQCStatus().contains(PurpleQCStatus.FAIL_CONTAMINATION) ? Sets.newHashSet() : orange.peach();

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

        HlaAllelesReportingData hlaReportingData = HlaAllelesReportingFactory.convertToReportData(orange.lilac(), curatedAnalysis.hasReliablePurity(), curatedAnalysis.purpleQCStatus());

        AnalysedPatientReport report = ImmutableAnalysedPatientReport.builder()
                .lamaPatientData(reportData.lamaPatientData())
                .diagnosticSiloPatientData(reportData.diagnosticSiloPatientData())
                .qsFormNumber(qcForm)
                .clinicalSummary(clinicalSummary)
                .specialRemark(specialRemark)
                .pipelineVersion(pipelineVersion)
                .genomicAnalysis(curatedAnalysis)
                .molecularTissueOriginReporting(curatedAnalysis.purpleQCStatus().contains(PurpleQCStatus.FAIL_CONTAMINATION) || !curatedAnalysis.hasReliablePurity()
                        ? null
                        : molecularTissueOriginReporting)
                .molecularTissueOriginPlotPath(config.cuppaPlot())
                .circosPlotPath(config.purpleCircosPlot())
                .comments(Optional.ofNullable(config.comments()))
                .isCorrectedReport(config.isCorrectedReport())
                .isCorrectedReportExtern(config.isCorrectedReportExtern())
                .signaturePath(reportData.signaturePath())
                .logoRVAPath(reportData.logoRVAPath())
                .logoCompanyPath(reportData.logoCompanyPath())
                .udiDi(reportData.udiDi())
                .pharmacogeneticsGenotypes(pharmacogeneticsGenotypesMap)
                .hlaAllelesReportingData(hlaReportingData)
                .reportDate(reportDate)
                .isWGSReport(true)
                .build();

        printReportState(report);

        return report;
    }

    @NotNull
    @VisibleForTesting
    static String determineForNumber(boolean hasReliablePurity, double purity) {
        return hasReliablePurity && purity > ReportResources.PURITY_CUTOFF ? QsFormNumber.FOR_080.display() : QsFormNumber.FOR_209.display();
    }

    @NotNull
    private static List<ProtectEvidence> extractReportableEvidenceItems(@NotNull String protectEvidenceTsv) throws IOException {
        LOGGER.info("Loading PROTECT data from {}", new File(protectEvidenceTsv).getParent());
        List<ProtectEvidence> evidences = ProtectEvidenceFile.read(protectEvidenceTsv);

        List<ProtectEvidence> reportableEvidenceItems = Lists.newArrayList();
        for (ProtectEvidence evidence : evidences) {
            if (evidence.reported()) {
                //TODO; switch to reportableEvidenceItems.add(evidence) when ready to use for reporting
                reportableEvidenceItems.add(ImmutableProtectEvidence.builder().from(evidence).
                        treatment(ImmutableTreatment.builder().from(evidence.treatment())
                                .relevantTreatmentApproaches(Sets.newHashSet())
                                .sourceRelevantTreatmentApproaches(Sets.newHashSet()).build())
                        .build());
            }
        }
        LOGGER.info(" Loaded {} reportable evidence items from {}", reportableEvidenceItems.size(), protectEvidenceTsv);

        return reportableEvidenceItems;
    }

    @NotNull
    private static CuppaPrediction bestPrediction(@NotNull CuppaData cuppa) {
        CuppaPrediction best = null;
        for (CuppaPrediction prediction : cuppa.predictions()) {
            if (best == null || prediction.likelihood() > best.likelihood()) {
                best = prediction;
            }
        }

        if (best == null) {
            LOGGER.warn("No best CUPPA prediction found");
            return ImmutableCuppaPrediction.builder().cancerType("Unknown").likelihood(0D).build();
        }

        return best;
    }

    private static void printReportState(@NotNull AnalysedPatientReport report) {
        PatientReporterData lamaPatientData = report.lamaPatientData();

        LocalDate tumorArrivalDate = lamaPatientData.getTumorArrivalDate();
        String formattedTumorArrivalDate = tumorArrivalDate != null ? DateTimeFormatter.ofPattern("dd-MMM-yyyy").format(tumorArrivalDate) : "N/A";

        LOGGER.info("Printing clinical and laboratory data for {}", lamaPatientData.getReportingId());
        LOGGER.info(" Tumor sample arrived at HMF on {}", formattedTumorArrivalDate);
        TumorType primaryTumorType = lamaPatientData.getPrimaryTumorType();
        if (primaryTumorType != null) {
            LOGGER.info(" Primary tumor details: {} ({})", primaryTumorType.getLocation(), primaryTumorType.getType());
        }
        LOGGER.info(" Shallow seq purity: {}", lamaPatientData.getShallowPurity());
        LOGGER.info(" Lab SOPs used: {}", lamaPatientData.getSopString());
        LOGGER.info(" Clinical summary present: {}", (report.clinicalSummary() != null ? "yes" : "no"));
        LOGGER.info(" Special remark present: {}", (!report.specialRemark().isEmpty() ? "yes" : "no"));

        LOGGER.info(" Germline reporting level: {}", lamaPatientData.getReportSettings().getFlagGermlineOnReport());

        GenomicAnalysis analysis = report.genomicAnalysis();

        LOGGER.info("Printing genomic analysis results for {}:", lamaPatientData.getReportingId());
        if (report.molecularTissueOriginReporting() != null) {
            LOGGER.info(" Molecular tissue origin conclusion: {}", report.molecularTissueOriginReporting().interpretCancerType());
        }
        LOGGER.info(" Somatic variants to report: {}", analysis.reportableVariants().size());
        if (lamaPatientData.getReportSettings().getFlagGermlineOnReport()) {
            LOGGER.info("  Number of variants known to exist in germline: {}", germlineOnly(analysis.reportableVariants()).size());
        } else {
            LOGGER.info("  Germline variants and evidence have been removed since no consent has been given");
        }
        LOGGER.info(" Number of gains and losses to report: {}", analysis.gainsAndLosses().size());
        LOGGER.info(" Gene fusions to report: {}", analysis.geneFusions().size());
        LOGGER.info(" Homozygous disruptions to report: {}", analysis.homozygousDisruptions().size());
        LOGGER.info(" Gene disruptions to report: {}", analysis.geneDisruptions().size());
        LOGGER.info(" Viruses to report: {}", analysis.reportableViruses().size());
        LOGGER.info(" Pharmacogenetics to report: {}", report.pharmacogeneticsGenotypes().size());

        LOGGER.info(" CHORD analysis HRD prediction: {} ({})", analysis.hrdValue(), analysis.hrdStatus());
        LOGGER.info(" Microsatellite indels per Mb: {} ({})", analysis.microsatelliteIndelsPerMb(), analysis.microsatelliteStatus());
        LOGGER.info(" Tumor mutational load: {} ({})", analysis.tumorMutationalLoad(), analysis.tumorMutationalLoadStatus());
        LOGGER.info(" Tumor mutational burden: {}", analysis.tumorMutationalBurden());

        LOGGER.info("Printing actionability results for {}", lamaPatientData.getReportingId());
        LOGGER.info(" Tumor-specific evidence items found: {}", analysis.tumorSpecificEvidence().size());
        LOGGER.info(" Clinical trials matched to molecular profile: {}", analysis.clinicalTrials().size());
        LOGGER.info(" Off-label evidence items found: {}", analysis.offLabelEvidence().size());
    }

    @NotNull
    private static List<ReportableVariant> germlineOnly(@NotNull List<ReportableVariant> variants) {
        List<ReportableVariant> germlineOnly = Lists.newArrayList();
        for (ReportableVariant variant : variants) {
            if (variant.source() == ReportableVariantSource.GERMLINE) {
                germlineOnly.add(variant);
            }
        }
        return germlineOnly;
    }
}