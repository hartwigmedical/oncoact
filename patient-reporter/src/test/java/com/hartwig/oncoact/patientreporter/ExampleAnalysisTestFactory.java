package com.hartwig.oncoact.patientreporter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.linx.FusionLikelihoodType;
import com.hartwig.hmftools.datamodel.linx.FusionPhasedType;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.linx.LinxFusionType;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.hmftools.datamodel.purple.Hotspot;
import com.hartwig.hmftools.datamodel.purple.ImmutablePurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleGenotypeStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.PurpleTumorMutationalStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantType;
import com.hartwig.hmftools.datamodel.virus.AnnotatedVirus;
import com.hartwig.hmftools.datamodel.virus.VirusInterpretation;
import com.hartwig.oncoact.copynumber.Chromosome;
import com.hartwig.oncoact.copynumber.ChromosomeArm;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.copynumber.CopyNumberInterpretation;
import com.hartwig.oncoact.copynumber.ImmutableCnPerChromosomeArmData;
import com.hartwig.oncoact.copynumber.PurpleGainLossData;
import com.hartwig.oncoact.cuppa.ImmutableMolecularTissueOriginReporting;
import com.hartwig.oncoact.cuppa.MolecularTissueOriginReporting;
import com.hartwig.oncoact.disruption.GeneDisruption;
import com.hartwig.oncoact.disruption.TestGeneDisruptionFactory;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaReporting;
import com.hartwig.oncoact.hla.ImmutableHlaAllele;
import com.hartwig.oncoact.hla.ImmutableHlaAllelesReportingData;
import com.hartwig.oncoact.hla.ImmutableHlaReporting;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;
import com.hartwig.oncoact.orange.peach.TestPeachFactory;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.orange.virus.TestVirusInterpreterFactory;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.algo.ImmutableAnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.ImmutableGenomicAnalysis;
import com.hartwig.oncoact.patientreporter.algo.InterpretPurpleGeneCopyNumbers;
import com.hartwig.oncoact.patientreporter.algo.QualityOverruleFunctions;
import com.hartwig.oncoact.patientreporter.cfreport.MathUtil;
import com.hartwig.oncoact.patientreporter.cfreport.data.TumorPurity;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ImmutableProtectEvidence;
import com.hartwig.oncoact.protect.KnowledgebaseSource;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestProtectFactory;
import com.hartwig.oncoact.util.Formats;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantSource;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ExampleAnalysisTestFactory {

    private static final String CIRCOS_PLOT_PATH = Resources.getResource("test_run/orange/plot/sample.circos.png").getPath();
    private static final String CUPPA_PLOT_PATH = Resources.getResource("test_run/cuppa/sample.cuppa.chart.png").getPath();

    private static final String UDI_DI = "(01)8720299486058(8012)v5.33/1.0";

    private ExampleAnalysisTestFactory() {
    }

    @NotNull
    public static AnalysedPatientReport createWithCOLO829Data(@NotNull ExampleAnalysisConfig config, @NotNull PurpleQCStatus purpleQCStatus,
            boolean withUnreliablePurityOverrule) {
        String pipelineVersion = "5.33";
        double averageTumorPloidy = 3.1;
        int tumorMutationalLoad = 183;
        double tumorMutationalBurden = 13.737;
        double microsatelliteIndelsPerMb = 0.1256;
        PurpleTumorMutationalStatus tumorMutationalBurdenStatus = PurpleTumorMutationalStatus.LOW;
        PurpleMicrosatelliteStatus microsatelliteStatus = PurpleMicrosatelliteStatus.MSS;
        double hrdValue = 0D;
        ChordStatus hrdStatus = ChordStatus.HR_PROFICIENT;
        String reportDate = Formats.formatDate(LocalDate.now());
        double impliedPurityPercentage = MathUtil.mapPercentage(config.impliedTumorPurity(), TumorPurity.RANGE_MIN, TumorPurity.RANGE_MAX);

        ReportData reportData = PatientReporterTestFactory.loadTestReportData();

        List<ProtectEvidence> tumorSpecificEvidence = createCOLO829TumorSpecificEvidence();
        List<ProtectEvidence> clinicalTrials = createCOLO829ClinicalTrials();
        List<ProtectEvidence> offLabelEvidence = createCOLO829OffLabelEvidence();
        List<ReportableVariant> reportableVariants = createCOLO829SomaticVariants(config.reportGermline());
        Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant = notifyAllGermlineVariants(reportableVariants);
        List<PurpleGainLossData> gainsAndLosses = createCOLO829GainsLosses();
        List<LinxFusion> fusions = Lists.newArrayList();
        List<HomozygousDisruption> homozygousDisruptions = Lists.newArrayList();
        List<GeneDisruption> disruptions = createCOLO829Disruptions();
        List<AnnotatedVirus> viruses = Lists.newArrayList();
        Map<String, List<PeachGenotype>> pharmacogeneticsGenotypes = createTestPharmacogeneticsGenotypes();
        HlaAllelesReportingData hlaData = createTestHlaData();
        List<InterpretPurpleGeneCopyNumbers> LOHGenes = Lists.newArrayList();

        String summaryWithoutGermline = "- Molecular tissue of origin prediction: Melanoma (likelihood: 99.6%).\n "
                + "- TERT (c.-125_-124delCCinsTT) promoter mutation.\n " + "- CDKN2A (p.Gly83fs) inactivation.\n "
                + "- BRAF (p.Val600Glu) activating mutation, possible indication for BRAF and/or MEK inhibitors (clinical trial).\n "
                + "- PTEN (copies: 0) loss, possible indication for PI3K inhibitors (clinical trial).\n";

        String summaryWithoutGermlineLowPurity = "Due to the lower tumor purity" + Formats.formatPercentage(impliedPurityPercentage)
                + " potential (subclonal) DNA aberrations might not have been detected using this test. This result should therefore be "
                + "considered with caution. \n \n " + "- Molecular tissue of origin prediction: Melanoma (likelihood: 99.6%).\n "
                + "- TERT (c.-125_-124delCCinsTT) promoter mutation.\n " + "- CDKN2A (p.Gly83fs) inactivation.\n "
                + "- BRAF (p.Val600Glu) activating mutation, possible indication for BRAF and/or MEK inhibitors (clinical trial).\n "
                + "- PTEN (copies: 0) loss, possible indication for PI3K inhibitors (clinical trial).\n";

        String summaryWithGermline = "- Molecular tissue of origin prediction: Melanoma (likelihood: 99.6%).\n"
                + "- TERT (c.-125_-124delCCinsTT) promoter mutation.\n "
                + "- CDKN2A (p.Gly83fs) inactivation. The observed CDKN2A mutation is also present in the germline of the patient. Referral to a genetic specialist should be considered. \n "
                + "- BRAF (p.Val600Glu) activating mutation, possible indication for BRAF and/or MEK inhibitors (clinical trial).\n "
                + "- PTEN (copies: 0) loss, possible indication for PI3K inhibitors (clinical trial).\n";

        String clinicalSummary;
        if (config.includeSummary() && !config.reportGermline()) {
            if (config.reportGermline()) {
                clinicalSummary = summaryWithGermline;
            } else {
                if (purpleQCStatus == PurpleQCStatus.FAIL_NO_TUMOR || purpleQCStatus == PurpleQCStatus.WARN_LOW_PURITY) {
                    clinicalSummary = summaryWithoutGermlineLowPurity;
                } else {
                    clinicalSummary = summaryWithoutGermline;
                }
            }
        } else {
            clinicalSummary = Strings.EMPTY;
        }

        String specialRemark = "This is a special remark";

        GenomicAnalysis analysis = ImmutableGenomicAnalysis.builder()
                .purpleQCStatus(Sets.newHashSet(purpleQCStatus))
                .impliedPurity(config.impliedTumorPurity())
                .hasReliablePurity(config.hasReliablePurity())
                .hasReliableQuality(true)
                .averageTumorPloidy(averageTumorPloidy)
                .tumorSpecificEvidence(tumorSpecificEvidence)
                .clinicalTrials(clinicalTrials)
                .offLabelEvidence(offLabelEvidence)
                .reportableVariants(reportableVariants)
                .notifyGermlineStatusPerVariant(notifyGermlineStatusPerVariant)
                .microsatelliteIndelsPerMb(microsatelliteIndelsPerMb)
                .microsatelliteStatus(microsatelliteStatus)
                .tumorMutationalLoad(tumorMutationalLoad)
                .tumorMutationalBurdenStatus(tumorMutationalBurdenStatus)
                .tumorMutationalBurden(tumorMutationalBurden)
                .hrdValue(hrdValue)
                .hrdStatus(hrdStatus)
                .gainsAndLosses(gainsAndLosses)
                .cnPerChromosome(extractCnPerChromosome())
                .geneFusions(fusions)
                .geneDisruptions(disruptions)
                .homozygousDisruptions(homozygousDisruptions)
                .reportableViruses(viruses)
                .suspectGeneCopyNumbersWithLOH(LOHGenes)
                .build();

        if (withUnreliablePurityOverrule) {
            GenomicAnalysis unreliablePurity = ImmutableGenomicAnalysis.builder().from(analysis).hasReliablePurity(false).build();
            analysis = QualityOverruleFunctions.overrule(unreliablePurity);
        }

        MolecularTissueOriginReporting molecularTissueOriginReporting = ImmutableMolecularTissueOriginReporting.builder()
                .bestCancerType("Melanoma")
                .bestLikelihood(0.996)
                .interpretCancerType("Melanoma")
                .interpretLikelihood(0.996)
                .build();

        return ImmutableAnalysedPatientReport.builder()
                .lamaPatientData(reportData.lamaPatientData())
                .diagnosticSiloPatientData(reportData.diagnosticSiloPatientData())
                .qsFormNumber(config.qcForNumber().display())
                .clinicalSummary(clinicalSummary)
                .specialRemark(specialRemark)
                .genomicAnalysis(analysis)
                .circosPlotPath(CIRCOS_PLOT_PATH)
                .molecularTissueOriginReporting(molecularTissueOriginReporting)
                .molecularTissueOriginPlotPath(CUPPA_PLOT_PATH)
                .comments(Optional.ofNullable(config.comments()))
                .isCorrectedReport(config.isCorrectionReport())
                .isCorrectedReportExtern(config.isCorrectionReportExtern())
                .signaturePath(reportData.signaturePath())
                .udiDi(UDI_DI)
                .logoRVAPath(reportData.logoRVAPath())
                .logoCompanyPath(reportData.logoCompanyPath())
                .pipelineVersion(pipelineVersion)
                .pharmacogeneticsGenotypes(pharmacogeneticsGenotypes)
                .hlaAllelesReportingData(hlaData)
                .reportDate(reportDate)
                .build();
    }

    @NotNull
    public static AnalysedPatientReport createAnalysisWithAllTablesFilledIn(@NotNull ExampleAnalysisConfig config,
            @NotNull PurpleQCStatus purpleQCStatus) {
        AnalysedPatientReport coloReport = createWithCOLO829Data(config, purpleQCStatus, false);

        List<LinxFusion> fusions = createTestFusions();
        List<AnnotatedVirus> viruses = createTestAnnotatedViruses();
        List<HomozygousDisruption> homozygousDisruptions = createTestHomozygousDisruptions();

        GenomicAnalysis analysis = ImmutableGenomicAnalysis.builder()
                .from(coloReport.genomicAnalysis())
                .geneFusions(fusions)
                .homozygousDisruptions(homozygousDisruptions)
                .reportableViruses(viruses)
                .build();

        return ImmutableAnalysedPatientReport.builder().from(coloReport).genomicAnalysis(analysis).build();
    }

    @NotNull
    private static Map<ReportableVariant, Boolean> notifyAllGermlineVariants(@NotNull List<ReportableVariant> reportableVariants) {
        Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant = Maps.newHashMap();
        for (ReportableVariant variant : reportableVariants) {
            if (variant.alleleCopyNumber() == null) {
                notifyGermlineStatusPerVariant.put(variant, variant.source() == ReportableVariantSource.GERMLINE_ONLY);
            } else {
                notifyGermlineStatusPerVariant.put(variant, variant.source() == ReportableVariantSource.GERMLINE);
            }
        }
        return notifyGermlineStatusPerVariant;
    }

    @NotNull
    public static CnPerChromosomeArmData buildCnPerChromosomeArmData(@NotNull Chromosome chromosome, @NotNull ChromosomeArm chromosomeArm,
            double copyNumber) {
        return ImmutableCnPerChromosomeArmData.builder().chromosome(chromosome).chromosomeArm(chromosomeArm).copyNumber(copyNumber).build();
    }

    @NotNull
    public static List<CnPerChromosomeArmData> extractCnPerChromosome() {
        List<CnPerChromosomeArmData> cnPerChromosomeArm = Lists.newArrayList();
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("1"), ChromosomeArm.P_ARM, 2.577279278488992));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("1"), ChromosomeArm.Q_ARM, 3.924434911196542));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("2"), ChromosomeArm.P_ARM, 3.017299967841595));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("2"), ChromosomeArm.Q_ARM, 3.02120002022585));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("3"), ChromosomeArm.P_ARM, 3.591262261849793));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("3"), ChromosomeArm.Q_ARM, 4.000879324279213));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("4"), ChromosomeArm.P_ARM, 2.0210999604946176));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("4"), ChromosomeArm.Q_ARM, 3.845384394611778));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("5"), ChromosomeArm.P_ARM, 2.0000481443928493));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("5"), ChromosomeArm.Q_ARM, 2.0097004288494347));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("6"), ChromosomeArm.P_ARM, 3.8480004115155264));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("6"), ChromosomeArm.Q_ARM, 2.913192227059896));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("7"), ChromosomeArm.P_ARM, 4.0246200936217384));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("7"), ChromosomeArm.Q_ARM, 4.172738954802649));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("8"), ChromosomeArm.P_ARM, 3.3325999264957695));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("8"), ChromosomeArm.Q_ARM, 3.3429530048942766));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("9"), ChromosomeArm.P_ARM, 2.7291755500808623));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("9"), ChromosomeArm.Q_ARM, 3.6992000400581495));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("10"), ChromosomeArm.P_ARM, 2.5009796653656786));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("10"), ChromosomeArm.Q_ARM, 2.0071487599595574));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("11"), ChromosomeArm.P_ARM, 3.166143699669606));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("11"), ChromosomeArm.Q_ARM, 2.9098638260285616));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("12"), ChromosomeArm.P_ARM, 3.0115999171651855));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("12"), ChromosomeArm.Q_ARM, 3.0031553295325786));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("13"), ChromosomeArm.P_ARM, 3.1564998196285714));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("13"), ChromosomeArm.Q_ARM, 3.146714774779385));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("14"), ChromosomeArm.P_ARM, 3.014099827765714));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("14"), ChromosomeArm.Q_ARM, 3.0138727282866444));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("15"), ChromosomeArm.P_ARM, 3.7023997998702702));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("15"), ChromosomeArm.Q_ARM, 2.546595184525802));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("16"), ChromosomeArm.P_ARM, 3.1922446681639967));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("16"), ChromosomeArm.Q_ARM, 1.9895619634349344));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("17"), ChromosomeArm.P_ARM, 2.9938998740100473));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("17"), ChromosomeArm.Q_ARM, 3.0477000530660465));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("18"), ChromosomeArm.P_ARM, 2.370931614063123));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("18"), ChromosomeArm.Q_ARM, 2.8490432529511334));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("19"), ChromosomeArm.P_ARM, 2.889021334953442));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("19"), ChromosomeArm.Q_ARM, 2.934100089054606));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("20"), ChromosomeArm.P_ARM, 4.0138808962887085));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("20"), ChromosomeArm.Q_ARM, 4.008612580584152));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("21"), ChromosomeArm.P_ARM, 2.991999766033014));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("21"), ChromosomeArm.Q_ARM, 2.9982181161009325));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("22"), ChromosomeArm.P_ARM, 3.9915997247172412));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("22"), ChromosomeArm.Q_ARM, 3.983474946385728));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("X"), ChromosomeArm.P_ARM, 1.9496150872949336));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("X"), ChromosomeArm.Q_ARM, 1.9547000205458256));
        cnPerChromosomeArm.add(buildCnPerChromosomeArmData(Chromosome.fromString("Y"), ChromosomeArm.P_ARM, 0.23189998001646422));
        return cnPerChromosomeArm;
    }

    @NotNull
    private static KnowledgebaseSource createTestProtectSource(@NotNull Knowledgebase source, @NotNull String sourceEvent,
            @NotNull Set<String> sourceUrls, @NotNull EvidenceType protectEvidenceType, @Nullable Integer range,
            @NotNull Set<String> evidenceUrls) {
        return TestProtectFactory.sourceBuilder()
                .name(source)
                .sourceEvent(sourceEvent)
                .sourceUrls(sourceUrls)
                .evidenceType(protectEvidenceType)
                .rangeRank(range)
                .evidenceUrls(evidenceUrls)
                .build();
    }

    @NotNull
    private static List<ProtectEvidence> createCOLO829TumorSpecificEvidence() {
        List<ProtectEvidence> evidenceItemsOnLabel = Lists.newArrayList();

        ImmutableProtectEvidence.Builder onLabelBuilder = TestProtectFactory.builder().onLabel(true).reported(true);
        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Atezolizumab + Cobimetinib + Vemurafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("RAF Inhibitor (Pan)",
                                "MEK2 Inhibitor",
                                "MEK inhibitor (Pan)",
                                "MEK1 Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600X",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                        EvidenceType.CODON_MUTATION,
                        600,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/32534646",
                                "https://www.accessdata.fda.gov/scripts/cder/daf/index.cfm?event=overview.process&varApplNo=761034"))))

                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Binimetinib + Encorafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor",
                                "MEK inhibitor (Pan)",
                                "MEK1 Inhibitor",
                                "BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600X",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                                EvidenceType.CODON_MUTATION,
                                600,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx",
                                        "https://www.esmo.org/Guidelines",
                                        "http://www.ncbi.nlm.nih.gov/pubmed/31566661")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx",
                                        "http://www.ncbi.nlm.nih.gov/pubmed/30959471")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/30219628",
                                        "https://www.accessdata.fda.gov/scripts/cder/daf/index.cfm?event=overview.process&varApplNo=210496",
                                        "https://www.accessdata.fda.gov/scripts/cder/daf/index.cfm?event=overview.process&varApplNo=210498",
                                        "http://www.ncbi.nlm.nih.gov/pubmed/29573941"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Cobimetinib + Vemurafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("RAF Inhibitor (Pan)",
                                "MEK2 Inhibitor",
                                "MEK inhibitor (Pan)",
                                "MEK1 Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600X",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                                EvidenceType.CODON_MUTATION,
                                600,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx",
                                        "https://www.esmo.org/Guidelines",
                                        "http://www.ncbi.nlm.nih.gov/pubmed/31566661")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet(
                                        "https://www.accessdata.fda.gov/scripts/cder/daf/index.cfm?event=overview.process&varApplNo=206192",
                                        "http://www.ncbi.nlm.nih.gov/pubmed/27480103"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Dabrafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("https://www.accessdata.fda.gov/scripts/cder/daf/index.cfm?event=overview.process&varApplNo=202806",
                                        "http://www.ncbi.nlm.nih.gov/pubmed/22735384")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600X",
                                Sets.newHashSet("hhttps://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                                EvidenceType.CODON_MUTATION,
                                600,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Dabrafenib + Trametinib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor",
                                "MEK inhibitor (Pan)",
                                "MEK1 Inhibitor",
                                "BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .reported(true)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600X",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                                EvidenceType.CODON_MUTATION,
                                600,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx",
                                        "https://www.esmo.org/Guidelines",
                                        "http://www.ncbi.nlm.nih.gov/pubmed/31566661")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/25399551",
                                        "https://www.accessdata.fda.gov/scripts/cder/daf/index.cfm?event=overview.process&varApplNo=202806")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/32818466",
                                        "https://ascopubs.org/doi/abs/10.1200/JCO.2020.38.15_suppl.10506",
                                        "http://www.ncbi.nlm.nih.gov/pubmed/34838156",
                                        "https://www.accessdata.fda.gov/scripts/cder/daf/index.cfm?event=overview.process&varApplNo=204114",
                                        "https://ascopubs.org/doi/abs/10.1200/JCO.2019.37.15_suppl.3002",
                                        "https://www.accessdata.fda.gov/scripts/cder/daf/index.cfm?event=overview.process&varApplNo=202806"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Trametinib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor", "MEK inhibitor (Pan)", "MEK1 Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/22663011",
                                "https://www.accessdata.fda.gov/scripts/cder/daf/index.cfm?event=overview.process&varApplNo=204114"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Vemurafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("RAF Inhibitor (Pan)"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/21639808",
                                        "https://www.accessdata.fda.gov/scripts/cder/daf/index.cfm?event=overview.process&varApplNo=202429",
                                        "http://www.ncbi.nlm.nih.gov/pubmed/28961848")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600X",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                                EvidenceType.CODON_MUTATION,
                                600,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Binimetinib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor", "MEK inhibitor (Pan)", "MEK1 Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600X",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                        EvidenceType.CODON_MUTATION,
                        600,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/23414587"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Binimetinib + Encorafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF mutant",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=465"),
                        EvidenceType.ANY_MUTATION,
                        600,
                        Sets.newHashSet("https://www.annalsofoncology.org/article/S0923-7534(21)03282-8/fulltext"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Dabrafenib + Nivolumab + Trametinib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor",
                                "MEK inhibitor (Pan)",
                                "MEK1 Inhibitor",
                                "BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600X",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                        EvidenceType.CODON_MUTATION,
                        600,
                        Sets.newHashSet("https://ascopubs.org/doi/abs/10.1200/JCO.2021.39.15_suppl.9520"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Dabrafenib + Pembrolizumab + Trametinib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor",
                                "MEK inhibitor (Pan)",
                                "MEK1 Inhibitor",
                                "BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        600,
                        Sets.newHashSet("https://meetinglibrary.asco.org/record/186980/abstract"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Dabrafenib + Spartalizumab + Trametinib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor",
                                "MEK inhibitor (Pan)",
                                "MEK1 Inhibitor",
                                "BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600X",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                        EvidenceType.CODON_MUTATION,
                        600,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/33020648"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Selumetinib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor", "MEK inhibitor (Pan)", "MEK1 Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/22048237"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Atezolizumab + Vemurafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("RAF Inhibitor (Pan)"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600X",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                        EvidenceType.CODON_MUTATION,
                        600,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/31171876"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Binimetinib + Encorafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor", "MEK inhibitor (Pan)", "MEK1 Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF act mut",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=696"),
                        EvidenceType.ACTIVATION,
                        null,
                        Sets.newHashSet(

                        ))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Dabrafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF mutant",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=465"),
                        EvidenceType.ANY_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/22608338"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Dabrafenib + KRT-232 + Trametinib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor",
                                "MEK inhibitor (Pan)",
                                "MEK1 Inhibitor",
                                "BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF 600X",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                        EvidenceType.CODON_MUTATION,
                        null,
                        Sets.newHashSet("http://abstracts.asco.org/199/AbstView_199_181299.html"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Encorafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF mutant",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=465"),
                        EvidenceType.ANY_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/28611198"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Encorafenib + Ribociclib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/28351928"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Tovorafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF mutant",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=465"),
                        EvidenceType.ANY_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/37219686"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Vemurafenib + Voruciclib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF mutant",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=465"),
                        EvidenceType.ANY_MUTATION,
                        null,
                        Sets.newHashSet("http://meetinglibrary.asco.org/content/153239-156"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("PTEN")
                .transcript("ENST00000371953")
                .isCanonical(true)
                .event("partial loss")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Temsirolimus")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "PTEN inact mut",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=2095"),
                        EvidenceType.INACTIVATION,
                        null,
                        Sets.newHashSet(
                                "https://aacrjournals.org/cancerres/article/83/8_Supplement/CT231/725279/Abstract-CT231-Temsirolimus-T-in-patients-pts-with"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("PTEN")
                .transcript("ENST00000371953")
                .isCanonical(true)
                .event("partial loss")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("GSK2636771")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "PTEN dec exp",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=13314"),
                        EvidenceType.UNDER_EXPRESSION,
                        null,
                        Sets.newHashSet("http://meetinglibrary.asco.org/content/131727-144"))))
                .build());

        evidenceItemsOnLabel.add(onLabelBuilder.gene("PTEN")
                .transcript("ENST00000371953")
                .isCanonical(true)
                .event("partial loss")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Onatasertib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "PTEN del",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=2408"),
                        EvidenceType.DELETION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/26177599"))))
                .build());

        return evidenceItemsOnLabel;
    }

    @NotNull
    private static List<ProtectEvidence> createCOLO829ClinicalTrials() {
        List<ProtectEvidence> trialsOnLabel = Lists.newArrayList();
        ImmutableProtectEvidence.Builder trialBuilder = TestProtectFactory.builder().onLabel(true).reported(true);

        trialsOnLabel.add(trialBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("COLUMBUS-AD")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.ICLUSION,
                        "BRAF V600E",
                        Sets.newHashSet("https://trial-eye.com/hmf/15589"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet())))
                .build());

        trialsOnLabel.add(trialBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("DRUP")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.ICLUSION,
                                "BRAF ACTIVATING MUTATION",
                                Sets.newHashSet("https://trial-eye.com/hmf/10299"),
                                EvidenceType.ACTIVATION,
                                null,
                                Sets.newHashSet()),
                        createTestProtectSource(Knowledgebase.ICLUSION,
                                "BRAF V600",
                                Sets.newHashSet("https://trial-eye.com/hmf/10299"),
                                EvidenceType.CODON_MUTATION,
                                600,
                                Sets.newHashSet())))
                .build());

        trialsOnLabel.add(trialBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("EBIN (EORTC-1612-MG)")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.ICLUSION,
                        "BRAF V600",
                        Sets.newHashSet("https://trial-eye.com/hmf/11284"),
                        EvidenceType.CODON_MUTATION,
                        600,
                        Sets.newHashSet())))
                .build());

        trialsOnLabel.add(trialBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("KN-8701")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.ICLUSION,
                                "BRAF ACTIVATING MUTATION",
                                Sets.newHashSet("https://www.trial-eye.com/hmf/15876"),
                                EvidenceType.ACTIVATION,
                                null,
                                Sets.newHashSet()),
                        createTestProtectSource(Knowledgebase.ICLUSION,
                                "BRAF V600E",
                                Sets.newHashSet("https://www.trial-eye.com/hmf/15876"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet())))
                .build());

        trialsOnLabel.add(trialBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("NASAM")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.ICLUSION,
                        "BRAF V600E",
                        Sets.newHashSet("https://www.trial-eye.com/hmf/14995"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet())))
                .build());

        trialsOnLabel.add(trialBuilder.gene("PTEN")
                .transcript("ENST00000371953")
                .isCanonical(true)
                .event("partial loss")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("DRUP")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.ICLUSION,
                                "PTEN LOSS",
                                Sets.newHashSet("https://trial-eye.com/hmf/10299"),
                                EvidenceType.DELETION,
                                null,
                                Sets.newHashSet()),
                        createTestProtectSource(Knowledgebase.ICLUSION,
                                "PTEN INACTIVATION MUTATION",
                                Sets.newHashSet("https://trial-eye.com/hmf/10299"),
                                EvidenceType.INACTIVATION,
                                null,
                                Sets.newHashSet())))
                .build());
        return trialsOnLabel;
    }

    @NotNull
    private static List<ProtectEvidence> createCOLO829OffLabelEvidence() {
        List<ProtectEvidence> evidenceItemsOffLabel = Lists.newArrayList();

        ImmutableProtectEvidence.Builder offLabelBuilder = TestProtectFactory.builder().onLabel(false).reported(true);

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Cetuximab")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESISTANT)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/36307056", "https://www.esmo.org/Guidelines"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Cetuximab + Encorafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/36307056",
                                        "https://www.accessdata.fda.gov/scripts/cder/daf/index.cfm?event=overview.process&varApplNo=210496",
                                        "http://www.ncbi.nlm.nih.gov/pubmed/31566309",
                                        "https://www.esmo.org/Guidelines"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Encorafenib + Panitumumab")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Panitumumab")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESISTANT)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "BRAF V600E",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                                EvidenceType.HOTSPOT_MUTATION,
                                null,
                                Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/36307056", "https://www.esmo.org/Guidelines"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Selumetinib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor", "MEK inhibitor (Pan)", "MEK1 Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("https://www.nccn.org/professionals/physician_gls/default.aspx"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("inimetinib + Cetuximab + Encorafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("MEK2 Inhibitor",
                                "MEK inhibitor (Pan)",
                                "MEK1 Inhibitor",
                                "BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.VICC_CIVIC,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/31566309", "http://www.ncbi.nlm.nih.gov/pubmed/36763936"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("RAF Inhibitor (Pan)")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/33356422"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Cetuximab + Vemurafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("RAF Inhibitor (Pan)"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600X",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1186"),
                        EvidenceType.CODON_MUTATION,
                        600,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/26287849"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Ruxolitinib + Vemurafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("RAF Inhibitor (Pan)"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/33979489"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Tovorafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("RAF Inhibitor (Pan)"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("https://ascopubs.org/doi/10.1200/JCO.2023.41.16_suppl.10004"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Alpelisib + Cetuximab + Encorafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF mutant",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=465"),
                        EvidenceType.ANY_MUTATION,
                        null,
                        Sets.newHashSet("http://meetinglibrary.asco.org/content/166237-176",
                                "http://www.ncbi.nlm.nih.gov/pubmed/28363909"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Cetuximab + Encorafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF mutant",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=465"),
                        EvidenceType.ANY_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/28363909"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Dabrafenib + Panitumumab")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("http://meetinglibrary.asco.org/content/131642-144",
                                "http://www.ncbi.nlm.nih.gov/pubmed/29431699"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Encorafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("BRAF Inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet())))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Erlotinib + Vemurafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("RAF Inhibitor (Pan)"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/36638198"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("BRAF")
                .transcript("ENST00000288602")
                .isCanonical(true)
                .event("p.Val600Glu")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Panitumumab + Vemurafenib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("RAF Inhibitor (Pan)"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "BRAF V600E",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                        EvidenceType.HOTSPOT_MUTATION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/25589621"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("CDKN2A (p16)")
                .transcript("ENST00000498124")
                .isCanonical(true)
                .event("p.Ala68fs")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Palbociclib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "CDKN2A mutant",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=4974"),
                        EvidenceType.ANY_MUTATION,
                        null,
                        Sets.newHashSet("https://ascopubs.org/doi/abs/10.1200/JCO.2019.37.15_suppl.9041"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("CDKN2A (p14ARF)")
                .transcript("ENST00000579755")
                .isCanonical(false)
                .event("p.Gly83fs")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Palbociclib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "CDKN2A mutant",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=4974"),
                        EvidenceType.ANY_MUTATION,
                        null,
                        Sets.newHashSet("https://ascopubs.org/doi/abs/10.1200/JCO.2019.37.15_suppl.9041"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("PTEN")
                .transcript("ENST00000371953")
                .isCanonical(true)
                .event("partial loss")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Abiraterone + Ipatasertib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("Akt Inhibitor (Pan)"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "PTEN loss",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=479"),
                        EvidenceType.DELETION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/30037818",
                                "http://www.ncbi.nlm.nih.gov/pubmed/34246347",
                                "https://academic.oup.com/annonc/article/27/suppl_6/718O/2799456/PTEN-loss-as-a-predictive-biomarker-for-the-Akt"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("PTEN")
                .transcript("ENST00000371953")
                .isCanonical(true)
                .event("partial loss")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Apitolisib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "PTEN mutant",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1213"),
                        EvidenceType.ANY_MUTATION,
                        null,
                        Sets.newHashSet("http://meetinglibrary.asco.org/content/132500-144"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("PTEN")
                .transcript("ENST00000371953")
                .isCanonical(true)
                .event("partial loss")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Capivasertib + Paclitaxel")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("Akt Inhibitor (Pan)"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                                "PTEN loss",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=479"),
                                EvidenceType.DELETION,
                                null,
                                Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/31841354")),
                        createTestProtectSource(Knowledgebase.CKB,
                                "PTEN inact mut",
                                Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=2095"),
                                EvidenceType.INACTIVATION,
                                null,
                                Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/31841354"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("PTEN")
                .transcript("ENST00000371953")
                .isCanonical(true)
                .event("partial loss")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Ipatasertib + Paclitaxel")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet())
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "PTEN dec exp",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=13314"),
                        EvidenceType.UNDER_EXPRESSION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/28800861"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("PTEN")
                .transcript("ENST00000371953")
                .isCanonical(true)
                .event("partial loss")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("AZD8186 + Vistusertib")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("PIK3CB inhibitor"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "PTEN loss",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=479"),
                        EvidenceType.DELETION,
                        null,
                        Sets.newHashSet("https://ascopubs.org/doi/10.1200/JCO.2017.35.15_suppl.2570"))))
                .build());

        evidenceItemsOffLabel.add(offLabelBuilder.gene("PTEN")
                .transcript("ENST00000371953")
                .isCanonical(true)
                .event("partial loss")
                .eventIsHighDriver(true)
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("Gemcitabine + LY2780301")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("Akt Inhibitor (Pan)"))
                        .relevantTreatmentApproaches(Sets.newHashSet())
                        .build())
                .onLabel(false)
                .level(EvidenceLevel.C)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Lists.newArrayList(createTestProtectSource(Knowledgebase.CKB,
                        "PTEN loss",
                        Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=479"),
                        EvidenceType.DELETION,
                        null,
                        Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/28750271"))))
                .build());
        return evidenceItemsOffLabel;
    }

    @NotNull
    private static List<ReportableVariant> createCOLO829SomaticVariants(boolean forceCDKN2AVariantToBeGermline) {
        ReportableVariant variant1 = TestReportableVariantFactory.builder()
                .source(ReportableVariantSource.SOMATIC)
                .gene("BRAF")
                .otherImpactClinical(purpleTranscriptImpactClinical("NM_004333",
                        "c.1799T>A",
                        "p.Val600Glu",
                        null,
                        null,
                        false,
                        Lists.newArrayList(PurpleVariantEffect.MISSENSE),
                        PurpleCodingEffect.MISSENSE))
                .transcript("ENST00000288602")
                .isCanonical(true)
                .chromosome("7")
                .position(140453136)
                .ref("A")
                .alt("T")
                .affectedCodon(600)
                .affectedExon(15)
                .canonicalTranscript("ENST00000288602")
                .canonicalEffect("missense")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .canonicalHgvsCodingImpact("c.1799T>A")
                .canonicalHgvsProteinImpact("p.Val600Glu")
                .alleleReadCount(150)
                .totalReadCount(221)
                .alleleCopyNumber(4.1)
                .totalCopyNumber(6.02)
                .minorAlleleCopyNumber(2.01)
                .hotspot(Hotspot.HOTSPOT)
                .driverLikelihood(1D)
                .clonalLikelihood(1D)
                .biallelic(false)
                .genotypeStatus(PurpleGenotypeStatus.HOM_REF)
                .localPhaseSet(null)
                .type(PurpleVariantType.SNP)
                .build();

        ReportableVariant variant2 = TestReportableVariantFactory.builder()
                .source(ReportableVariantSource.SOMATIC)
                .gene("CDKN2A (p16)")
                .otherImpactClinical(purpleTranscriptImpactClinical("NM_000077",
                        "c.203_204delCG",
                        "p.Ala68fs",
                        null,
                        null,
                        false,
                        Lists.newArrayList(PurpleVariantEffect.FRAMESHIFT),
                        PurpleCodingEffect.NONSENSE_OR_FRAMESHIFT))
                .transcript("ENST00000498124")
                .isCanonical(true)
                .chromosome("9")
                .position(21971153)
                .ref("CCG")
                .alt("C")
                .affectedExon(2)
                .affectedCodon(69)
                .canonicalTranscript("ENST00000498124")
                .canonicalEffect("frameshift")
                .canonicalCodingEffect(PurpleCodingEffect.NONSENSE_OR_FRAMESHIFT)
                .canonicalHgvsCodingImpact("c.203_204delCG")
                .canonicalHgvsProteinImpact("p.Ala68fs")
                .alleleReadCount(99)
                .totalReadCount(99)
                .alleleCopyNumber(2.02)
                .minorAlleleCopyNumber(0.0)
                .totalCopyNumber(2.0)
                .hotspot(Hotspot.NEAR_HOTSPOT)
                .clonalLikelihood(1D)
                .driverLikelihood(1D)
                .biallelic(true)
                .genotypeStatus(PurpleGenotypeStatus.HOM_REF)
                .localPhaseSet(null)
                .type(PurpleVariantType.INDEL)
                .build();

        ReportableVariant variant3 = TestReportableVariantFactory.builder()
                .source(ReportableVariantSource.GERMLINE)
                .gene("CDKN2A (p14ARF)")
                .otherImpactClinical(null)
                .transcript("ENST00000579755")
                .isCanonical(false)
                .chromosome("9")
                .position(21971153)
                .ref("CCG")
                .alt("C")
                .affectedCodon(69)
                .affectedExon(2)
                .canonicalTranscript("ENST00000498124")
                .canonicalEffect("frameshift")
                .canonicalCodingEffect(PurpleCodingEffect.NONSENSE_OR_FRAMESHIFT)
                .canonicalHgvsCodingImpact("c.246_247delCG")
                .canonicalHgvsProteinImpact("p.Gly83fs")
                .alleleReadCount(99)
                .totalReadCount(99)
                .alleleCopyNumber(2.02)
                .minorAlleleCopyNumber(0.0)
                .totalCopyNumber(2.0)
                .hotspot(Hotspot.NEAR_HOTSPOT)
                .clonalLikelihood(1D)
                .driverLikelihood(1D)
                .biallelic(true)
                .genotypeStatus(PurpleGenotypeStatus.HOM_REF)
                .localPhaseSet(null)
                .type(PurpleVariantType.INDEL)
                .build();

        ReportableVariant variant4 = TestReportableVariantFactory.builder()
                .source(ReportableVariantSource.GERMLINE_ONLY)
                .gene("TERT")
                .otherImpactClinical(purpleTranscriptImpactClinical("NM_198253",
                        "c.-125_-124delCCinsTT",
                        Strings.EMPTY,
                        null,
                        null,
                        false,
                        Lists.newArrayList(PurpleVariantEffect.UPSTREAM_GENE),
                        PurpleCodingEffect.MISSENSE))
                .transcript("ENST00000310581")
                .isCanonical(true)
                .chromosome("5")
                .position(1295228)
                .ref("GG")
                .alt("AA")
                .affectedCodon(null)
                .affectedExon(null)
                .canonicalTranscript("ENST00000310581")
                .canonicalEffect("upstream_gene")
                .canonicalCodingEffect(PurpleCodingEffect.NONE)
                .canonicalHgvsCodingImpact("c.-125_-124delCCinsTT")
                .canonicalHgvsProteinImpact(Strings.EMPTY)
                .alleleReadCount(56)
                .totalReadCount(65)
                .alleleCopyNumber(null)
                .minorAlleleCopyNumber(null)
                .totalCopyNumber(null)
                .hotspot(null)
                .clonalLikelihood(null)
                .driverLikelihood(null)
                .biallelic(null)
                .genotypeStatus(PurpleGenotypeStatus.HOM_REF)
                .localPhaseSet(4621)
                .type(PurpleVariantType.MNP)
                .build();

        ReportableVariant variant5 = TestReportableVariantFactory.builder()
                .source(ReportableVariantSource.SOMATIC)
                .gene("SF3B1")
                .otherImpactClinical(purpleTranscriptImpactClinical("NM_012433",
                        "c.2153C>T",
                        "p.Pro718Leu",
                        null,
                        null,
                        false,
                        Lists.newArrayList(PurpleVariantEffect.MISSENSE),
                        PurpleCodingEffect.MISSENSE))
                .transcript("ENST00000335508")
                .isCanonical(true)
                .affectedExon(null)
                .affectedCodon(null)
                .chromosome("2")
                .position(198266779)
                .ref("G")
                .alt("A")
                .canonicalTranscript("ENST00000335508")
                .canonicalEffect("missense")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .canonicalHgvsCodingImpact("c.2153C>T")
                .canonicalHgvsProteinImpact("p.Pro718Leu")
                .alleleReadCount(74)
                .totalReadCount(111)
                .alleleCopyNumber(2.03)
                .minorAlleleCopyNumber(1.0)
                .totalCopyNumber(3.02)
                .hotspot(Hotspot.NON_HOTSPOT)
                .clonalLikelihood(1D)
                .driverLikelihood(0.1459)
                .biallelic(false)
                .genotypeStatus(PurpleGenotypeStatus.HOM_REF)
                .localPhaseSet(null)
                .type(PurpleVariantType.SNP)
                .build();

        ReportableVariant variant6 = TestReportableVariantFactory.builder()
                .source(ReportableVariantSource.SOMATIC)
                .gene("TP63")
                .otherImpactClinical(null)
                .transcript("ENST00000264731")
                .isCanonical(true)
                .chromosome("3")
                .position(189604330)
                .ref("G")
                .alt("T")
                .affectedExon(11)
                .affectedCodon(499)
                .canonicalTranscript("ENST00000264731")
                .canonicalEffect("missense")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .canonicalHgvsCodingImpact("c.1497G>T")
                .canonicalHgvsProteinImpact("p.Met499Ile")
                .alleleReadCount(47)
                .totalReadCount(112)
                .alleleCopyNumber(1.68)
                .minorAlleleCopyNumber(1.97)
                .totalCopyNumber(3.98)
                .hotspot(Hotspot.NON_HOTSPOT)
                .clonalLikelihood(1D)
                .driverLikelihood(0D)
                .biallelic(false)
                .genotypeStatus(PurpleGenotypeStatus.HOM_REF)
                .localPhaseSet(null)
                .type(PurpleVariantType.SNP)
                .build();

        return Lists.newArrayList(variant1, variant2, variant3, variant4, variant5, variant6);
    }

    @NotNull
    private static PurpleTranscriptImpact purpleTranscriptImpactClinical(@NotNull String transcript, @NotNull String hgvsCodingImpact,
            @NotNull String hgvsProteinImpact, Integer codon, Integer exon, boolean spliceRegion,
            @NotNull List<PurpleVariantEffect> effects, @NotNull PurpleCodingEffect effect) {
        return ImmutablePurpleTranscriptImpact.builder()
                .transcript(transcript)
                .hgvsCodingImpact(hgvsCodingImpact)
                .hgvsProteinImpact(hgvsProteinImpact)
                .affectedCodon(codon)
                .affectedExon(exon)
                .spliceRegion(spliceRegion)
                .effects(effects)
                .codingEffect(effect)
                .build();
    }

    @NotNull
    private static List<PurpleGainLossData> createCOLO829GainsLosses() {
        PurpleGainLossData gainLoss1 = TestPurpleFactory.gainLossBuilderOncoAct()
                .chromosome("10")
                .chromosomeBand("q23.31")
                .gene("PTEN")
                .transcript("ENST00000371953")
                .isCanonical(true)
                .minCopies(0)
                .maxCopies(2.0136)
                .interpretation(CopyNumberInterpretation.PARTIAL_LOSS)
                .build();

        return Lists.newArrayList(gainLoss1);
    }

    @NotNull
    private static List<LinxFusion> createTestFusions() {
        LinxFusion fusion1 = TestLinxFactory.fusionBuilder()
                .reported(true)
                .reportedType(LinxFusionType.KNOWN_PAIR)
                .name(Strings.EMPTY)
                .geneStart("TMPRSS2")
                .geneContextStart("Intron 5")
                .geneTranscriptStart("ENST00000398585")
                .fusedExonUp(6)
                .geneEnd("PNPLA7")
                .geneContextEnd("Intron 3")
                .geneTranscriptEnd("ENST00000406427")
                .fusedExonDown(7)
                .likelihood(FusionLikelihoodType.HIGH)
                .phased(FusionPhasedType.INFRAME)
                .junctionCopyNumber(0.4)
                .build();

        LinxFusion fusion2 = TestLinxFactory.fusionBuilder()
                .reported(true)
                .reportedType(LinxFusionType.PROMISCUOUS_5)
                .name(Strings.EMPTY)
                .geneStart("CLCN6")
                .geneContextStart("Intron 1")
                .geneTranscriptStart("ENST00000346436")
                .fusedExonUp(6)
                .geneEnd("BRAF")
                .geneContextEnd("Intron 8")
                .geneTranscriptEnd("ENST00000288602")
                .fusedExonDown(7)
                .likelihood(FusionLikelihoodType.LOW)
                .phased(FusionPhasedType.SKIPPED_EXONS)
                .junctionCopyNumber(1D)
                .build();

        return Lists.newArrayList(fusion1, fusion2);
    }

    @NotNull
    private static List<GeneDisruption> createCOLO829Disruptions() {
        GeneDisruption disruption1 = TestGeneDisruptionFactory.builder()
                .location("10q23.31")
                .gene("PTEN")
                .transcriptId("ENST00000371953")
                .isCanonical(true)
                .range("Intron 5 -> Intron 6")
                .type("DEL")
                .junctionCopyNumber(2.0054)
                .undisruptedCopyNumber(0.0)
                .firstAffectedExon(5)
                .clusterId(68)
                .build();

        return Lists.newArrayList(disruption1);
    }

    @NotNull
    private static List<HomozygousDisruption> createTestHomozygousDisruptions() {
        return Lists.newArrayList(TestLinxFactory.homozygousDisruptionBuilder()
                .chromosome("8")
                .chromosomeBand("p22")
                .gene("SGCZ")
                .transcript("123")
                .isCanonical(true)
                .build());
    }

    @NotNull
    public static Map<String, List<PeachGenotype>> createTestPharmacogeneticsGenotypes() {
        Map<String, List<PeachGenotype>> pharmacogeneticsMap = Maps.newHashMap();
        pharmacogeneticsMap.put("UGT1A1",
                Lists.newArrayList(TestPeachFactory.builder()
                        .gene("UGT1A1")
                        .haplotype("*1_HOM")
                        .function("Normal Function")
                        .linkedDrugs("Irinotecan")
                        .urlPrescriptionInfo("https://www.pharmgkb.org/guidelineAnnotation/PA166104951")
                        .panelVersion("peach_prod_v1.3")
                        .repoVersion("1.7")
                        .build()));

        pharmacogeneticsMap.put("DPYD",
                Lists.newArrayList(TestPeachFactory.builder()
                        .gene("DPYD")
                        .haplotype("*1_HOM")
                        .function("Normal Function")
                        .linkedDrugs("5-Fluorouracil;Capecitabine;Tegafur")
                        .urlPrescriptionInfo("https://www.pharmgkb.org/guidelineAnnotation/PA166104939"
                                + "https://www.pharmgkb.org/guidelineAnnotation/PA166104963"
                                + "https://www.pharmgkb.org/guidelineAnnotation/PA166104944")
                        .panelVersion("peach_prod_v1.3")
                        .repoVersion("1.7")
                        .build()));
        return pharmacogeneticsMap;
    }

    @NotNull
    public static HlaAllelesReportingData createTestHlaData() {
        Map<String, List<HlaReporting>> alleles = Maps.newHashMap();

        alleles.put("HLA-A",
                Lists.newArrayList(hlaReportingBuilder().hlaAllele(ImmutableHlaAllele.builder()
                        .gene("HLA-A")
                        .germlineAllele("A*01:01")
                        .build()).germlineCopies(2D).tumorCopies(3.83).somaticMutations("None").interpretation("Yes").build()));
        alleles.put("HLA-B",
                Lists.newArrayList(hlaReportingBuilder().hlaAllele(ImmutableHlaAllele.builder()
                                .gene("HLA-B")
                                .germlineAllele("B*40:02")
                                .build()).germlineCopies(1D).tumorCopies(2D).somaticMutations("None").interpretation("Yes").build(),
                        hlaReportingBuilder().hlaAllele(ImmutableHlaAllele.builder().gene("HLA-B").germlineAllele("B*08:01").build())
                                .germlineCopies(1D)
                                .tumorCopies(1.83)
                                .somaticMutations("None")
                                .interpretation("Yes")
                                .build()));
        alleles.put("HLA-C",
                Lists.newArrayList(hlaReportingBuilder().hlaAllele(ImmutableHlaAllele.builder()
                                .gene("HLA-C")
                                .germlineAllele("C*07:01")
                                .build()).germlineCopies(1D).tumorCopies(1.83).somaticMutations("None").interpretation("Yes").build(),
                        hlaReportingBuilder().hlaAllele(ImmutableHlaAllele.builder().gene("HLA-C").germlineAllele("C*03:04").build())
                                .germlineCopies(1D)
                                .tumorCopies(2D)
                                .somaticMutations("None")
                                .interpretation("Yes")
                                .build()));
        return ImmutableHlaAllelesReportingData.builder().hlaQC("PASS").hlaAllelesReporting(alleles).build();

    }

    @NotNull
    private static ImmutableHlaReporting.Builder hlaReportingBuilder() {
        return ImmutableHlaReporting.builder()
                .hlaAllele(ImmutableHlaAllele.builder().gene(Strings.EMPTY).germlineAllele(Strings.EMPTY).build())
                .germlineCopies(0)
                .tumorCopies(0)
                .somaticMutations(Strings.EMPTY)
                .interpretation(Strings.EMPTY);
    }

    @NotNull
    private static List<AnnotatedVirus> createTestAnnotatedViruses() {
        return Lists.newArrayList(TestVirusInterpreterFactory.builder()
                .reported(true)
                .name("Human papillomavirus type 16")
                .interpretation(VirusInterpretation.HPV)
                .integrations(2)
                .build());
    }
}