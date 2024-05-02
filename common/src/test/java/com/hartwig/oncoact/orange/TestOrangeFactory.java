package com.hartwig.oncoact.orange;

import java.time.LocalDate;
import java.util.Set;

import com.google.common.io.Resources;
import com.hartwig.hmftools.datamodel.chord.ChordRecord;
import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.cuppa.CuppaData;
import com.hartwig.hmftools.datamodel.cuppa.ImmutableCuppaData;
import com.hartwig.hmftools.datamodel.flagstat.ImmutableFlagstat;
import com.hartwig.hmftools.datamodel.hla.ImmutableLilacRecord;
import com.hartwig.hmftools.datamodel.hla.LilacRecord;
import com.hartwig.hmftools.datamodel.linx.FusionLikelihoodType;
import com.hartwig.hmftools.datamodel.linx.ImmutableLinxRecord;
import com.hartwig.hmftools.datamodel.linx.LinxBreakend;
import com.hartwig.hmftools.datamodel.linx.LinxBreakendType;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.linx.LinxFusionType;
import com.hartwig.hmftools.datamodel.linx.LinxRecord;
import com.hartwig.hmftools.datamodel.metrics.ImmutableWGSMetrics;
import com.hartwig.hmftools.datamodel.orange.ImmutableOrangePlots;
import com.hartwig.hmftools.datamodel.orange.ImmutableOrangeRecord;
import com.hartwig.hmftools.datamodel.orange.ImmutableOrangeSample;
import com.hartwig.hmftools.datamodel.orange.OrangePlots;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.hmftools.datamodel.orange.OrangeRefGenomeVersion;
import com.hartwig.hmftools.datamodel.orange.OrangeSample;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.hmftools.datamodel.purple.CopyNumberInterpretation;
import com.hartwig.hmftools.datamodel.purple.HotspotType;
import com.hartwig.hmftools.datamodel.purple.ImmutablePurpleRecord;
import com.hartwig.hmftools.datamodel.purple.PurpleCharacteristics;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleCopyNumber;
import com.hartwig.hmftools.datamodel.purple.PurpleDriverType;
import com.hartwig.hmftools.datamodel.purple.PurpleFit;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleRecord;
import com.hartwig.hmftools.datamodel.purple.PurpleTumorMutationalStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleVariant;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;
import com.hartwig.hmftools.datamodel.virus.ImmutableVirusInterpreterData;
import com.hartwig.hmftools.datamodel.virus.VirusBreakendQCStatus;
import com.hartwig.hmftools.datamodel.virus.VirusInterpretation;
import com.hartwig.hmftools.datamodel.virus.VirusInterpreterData;
import com.hartwig.hmftools.datamodel.virus.VirusInterpreterEntry;
import com.hartwig.hmftools.datamodel.virus.VirusLikelihoodType;
import com.hartwig.oncoact.orange.chord.TestChordFactory;
import com.hartwig.oncoact.orange.cuppa.TestCuppaFactory;
import com.hartwig.oncoact.orange.lilac.TestLilacFactory;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;
import com.hartwig.oncoact.orange.peach.TestPeachFactory;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.orange.virus.TestVirusInterpreterFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestOrangeFactory {

    private static final String EMPTY_CIRCOS_PLOT = Resources.getResource("orange/plot/empty.circos.png").getPath();

    private TestOrangeFactory() {
    }

    @NotNull
    public static OrangeRecord createMinimalTestOrangeRecord() {
        return ImmutableOrangeRecord.builder()
                .sampleId("TEST")
                .tumorSample(createMinimalOrangeSample())
                .samplingDate(LocalDate.of(2022, 1, 20))
                .refGenomeVersion(OrangeRefGenomeVersion.V37)
                .purple(createMinimalTestPurpleRecord())
                .linx(ImmutableLinxRecord.builder().build())
                .peach(Set.of())
                .cuppa(createTestCuppaRecord())
                .virusInterpreter(ImmutableVirusInterpreterData.builder().build())
                .lilac(createMinimalTestLilacRecord())
                .chord(createTestChordRecord())
                .plots(createMinimalTestOrangePlots())
                .build();
    }

    @NotNull
    public static OrangeSample createMinimalOrangeSample() {
        return ImmutableOrangeSample.builder().flagstat(createMinimalFlagStat()).metrics(createMinimalWgsMetrics()).build();
    }

    @NotNull
    private static ImmutableFlagstat createMinimalFlagStat() {
        return ImmutableFlagstat.builder().mappedProportion(0).secondaryCount(0).supplementaryCount(0).uniqueReadCount(0).build();
    }

    @NotNull
    private static ImmutableWGSMetrics createMinimalWgsMetrics() {
        return ImmutableWGSMetrics.builder()
                .meanCoverage(0)
                .sdCoverage(0)
                .medianCoverage(0)
                .madCoverage(0)
                .pctExcMapQ(0)
                .pctExcDupe(0)
                .pctExcUnpaired(0)
                .pctExcBaseQ(0)
                .pctExcOverlap(0)
                .pctExcCapped(0)
                .pctExcTotal(0)
                .build();
    }

    @NotNull
    private static PurpleRecord createMinimalTestPurpleRecord() {
        return ImmutablePurpleRecord.builder()
                .fit(TestPurpleFactory.fitBuilder().build())
                .characteristics(TestPurpleFactory.characteristicsBuilder().build())
                .build();
    }

    @NotNull
    private static LilacRecord createMinimalTestLilacRecord() {
        return ImmutableLilacRecord.builder().qc(Strings.EMPTY).build();
    }

    @NotNull
    private static OrangePlots createMinimalTestOrangePlots() {
        return ImmutableOrangePlots.builder()
                .sageTumorBQRPlot(Strings.EMPTY)
                .purpleInputPlot(Strings.EMPTY)
                .purpleClonalityPlot(Strings.EMPTY)
                .purpleCopyNumberPlot(Strings.EMPTY)
                .purpleVariantCopyNumberPlot(Strings.EMPTY)
                .purplePurityRangePlot(Strings.EMPTY)
                .purpleFinalCircosPlot(EMPTY_CIRCOS_PLOT)
                .build();
    }

    @NotNull
    public static OrangeRecord createProperTestOrangeRecord() {
        return ImmutableOrangeRecord.builder()
                .from(createMinimalTestOrangeRecord())
                .purple(createTestPurpleRecord())
                .linx(createTestLinxRecord())
                .peach(createTestPeachRecord())
                .cuppa(createTestCuppaRecord())
                .virusInterpreter(createTestVirusInterpreterRecord())
                .lilac(createTestLilacRecord())
                .chord(createTestChordRecord())
                .build();
    }

    @NotNull
    private static PurpleRecord createTestPurpleRecord() {
        PurpleVariant somaticVariant = TestPurpleFactory.variantBuilder()
                .reported(true)
                .gene("BRAF")
                .adjustedCopyNumber(6.0)
                .variantCopyNumber(4.1)
                .hotspot(HotspotType.HOTSPOT)
                .subclonalLikelihood(0.02)
                .biallelic(false)
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder()
                        .hgvsCodingImpact("c.something")
                        .hgvsProteinImpact("p.Val600Glu")
                        .inSpliceRegion(false)
                        .addEffects(PurpleVariantEffect.MISSENSE)
                        .codingEffect(PurpleCodingEffect.MISSENSE)
                        .build())
                .build();

        PurpleVariant germlineVariant = TestPurpleFactory.variantBuilder()
                .reported(true)
                .gene("KRAS")
                .adjustedCopyNumber(6.0)
                .variantCopyNumber(4.1)
                .hotspot(HotspotType.HOTSPOT)
                .subclonalLikelihood(0.02)
                .biallelic(false)
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder()
                        .hgvsCodingImpact("c.something")
                        .hgvsProteinImpact("p.Val600Glu")
                        .inSpliceRegion(false)
                        .addEffects(PurpleVariantEffect.MISSENSE)
                        .codingEffect(PurpleCodingEffect.MISSENSE)
                        .build())
                .build();

        PurpleCopyNumber somaticCopyNumber =
                TestPurpleFactory.copyNumberBuilder().chromosome("1").start(10).end(20).averageTumorCopyNumber(2.1).build();

        PurpleGainLoss somaticGain = TestPurpleFactory.gainLossBuilder()
                .gene("MYC")
                .interpretation(CopyNumberInterpretation.FULL_GAIN)
                .minCopies(38)
                .maxCopies(40)
                .build();

        PurpleGainLoss somaticLoss = TestPurpleFactory.gainLossBuilder()
                .gene("PTEN")
                .interpretation(CopyNumberInterpretation.FULL_LOSS)
                .minCopies(0)
                .maxCopies(0)
                .build();

        PurpleGainLoss germlineLoss = TestPurpleFactory.gainLossBuilder()
                .gene("TP53")
                .interpretation(CopyNumberInterpretation.FULL_LOSS)
                .minCopies(0)
                .maxCopies(0)
                .build();

        return ImmutablePurpleRecord.builder()
                .from(createMinimalTestPurpleRecord())
                .fit(createTestPurpleFit())
                .characteristics(createTestPurpleCharacteristics())
                .addSomaticDrivers(TestPurpleFactory.driverBuilder()
                        .gene(somaticVariant.gene())
                        .type(PurpleDriverType.MUTATION)
                        .driverLikelihood(1D)
                        .build())
                .addSomaticDrivers(TestPurpleFactory.driverBuilder()
                        .gene(germlineVariant.gene())
                        .type(PurpleDriverType.MUTATION)
                        .driverLikelihood(1D)
                        .build())
                .addSomaticDrivers(TestPurpleFactory.driverBuilder()
                        .gene(somaticGain.gene())
                        .type(PurpleDriverType.AMP)
                        .driverLikelihood(1D)
                        .build())
                .addSomaticDrivers(TestPurpleFactory.driverBuilder()
                        .gene(somaticLoss.gene())
                        .type(PurpleDriverType.DEL)
                        .driverLikelihood(1D)
                        .build())
                .addSomaticDrivers(TestPurpleFactory.driverBuilder()
                        .gene(germlineLoss.gene())
                        .type(PurpleDriverType.DEL)
                        .driverLikelihood(1D)
                        .build())
                .addAllSomaticVariants(somaticVariant)
                .addReportableSomaticVariants(somaticVariant)
                .addAllGermlineVariants(germlineVariant)
                .addReportableGermlineVariants(germlineVariant)
                .addAllSomaticCopyNumbers(somaticCopyNumber)
                .addAllSomaticGainsLosses(somaticGain, somaticLoss)
                .addReportableSomaticGainsLosses(somaticGain, somaticLoss)
                .addAllGermlineFullLosses(somaticGain, somaticLoss)
                .addReportableGermlineFullLosses(somaticGain, somaticLoss)
                .build();
    }

    @NotNull
    private static PurpleFit createTestPurpleFit() {
        return TestPurpleFactory.fitBuilder().purity(0.12).ploidy(3.1).build();
    }

    @NotNull
    private static PurpleCharacteristics createTestPurpleCharacteristics() {
        return TestPurpleFactory.characteristicsBuilder()
                .microsatelliteIndelsPerMb(0.1)
                .microsatelliteStatus(PurpleMicrosatelliteStatus.MSS)
                .tumorMutationalBurdenPerMb(13D)
                .tumorMutationalBurdenStatus(PurpleTumorMutationalStatus.HIGH)
                .tumorMutationalLoad(189)
                .tumorMutationalLoadStatus(PurpleTumorMutationalStatus.HIGH)
                .build();
    }

    @NotNull
    private static LinxRecord createTestLinxRecord() {
        LinxBreakend breakend1 = TestLinxFactory.breakendBuilder()
                .reported(true)
                .svId(1)
                .gene("RB1")
                .type(LinxBreakendType.DEL)
                .junctionCopyNumber(0.8)
                .undisruptedCopyNumber(2.1)
                .build();

        LinxBreakend breakend2 = TestLinxFactory.breakendBuilder()
                .reported(true)
                .svId(1)
                .gene("PTEN")
                .type(LinxBreakendType.DEL)
                .junctionCopyNumber(1D)
                .undisruptedCopyNumber(1D)
                .build();

        LinxFusion fusion = TestLinxFactory.fusionBuilder()
                .reported(true)
                .reportedType(LinxFusionType.KNOWN_PAIR)
                .geneStart("EML4")
                .fusedExonUp(2)
                .geneEnd("ALK")
                .fusedExonDown(4)
                .driverLikelihood(FusionLikelihoodType.HIGH)
                .build();

        return ImmutableLinxRecord.builder()
                .addAllSomaticStructuralVariants(TestLinxFactory.structuralVariantBuilder().svId(1).clusterId(1).build())
                .addSomaticHomozygousDisruptions(TestLinxFactory.homozygousDisruptionBuilder().gene("TP53").build())
                .addGermlineHomozygousDisruptions(TestLinxFactory.homozygousDisruptionBuilder().gene("PTEN").build())
                .addAllSomaticBreakends(breakend1)
                .addAllGermlineBreakends(breakend2)
                .addReportableSomaticBreakends(breakend1)
                .addReportableGermlineBreakends(breakend2)
                .addAllSomaticFusions(fusion)
                .addReportableSomaticFusions(fusion)
                .build();
    }

    @NotNull
    private static Set<PeachGenotype> createTestPeachRecord() {
        return Set.of(TestPeachFactory.builder()
                .gene("DPYD")
                .haplotype("1* HOM")
                .function("Normal function")
                .linkedDrugs("5-Fluorouracil")
                .urlPrescriptionInfo("https://www.pharmgkb.org/guidelineAnnotation/PA166104939")
                .panelVersion("peach_prod_v1.3")
                .repoVersion("1.7")
                .build());
    }

    @NotNull
    private static CuppaData createTestCuppaRecord() {
        return ImmutableCuppaData.builder()
                .addPredictions(TestCuppaFactory.builder().cancerType("Melanoma").likelihood(0.996).build())
                .simpleDups32To200B(0)
                .maxComplexSize(0)
                .telomericSGLs(0)
                .lineCount(0)
                .build();
    }

    @NotNull
    private static VirusInterpreterData createTestVirusInterpreterRecord() {
        VirusInterpreterEntry virus = TestVirusInterpreterFactory.builder()
                .reported(true)
                .name("Human papillomavirus type 16")
                .qcStatus(VirusBreakendQCStatus.NO_ABNORMALITIES)
                .interpretation(VirusInterpretation.HPV)
                .integrations(3)
                .driverLikelihood(VirusLikelihoodType.HIGH)
                .meanCoverage(0)
                .build();

        return ImmutableVirusInterpreterData.builder().addAllViruses(virus).addReportableViruses(virus).build();
    }

    @NotNull
    private static LilacRecord createTestLilacRecord() {
        return ImmutableLilacRecord.builder()
                .qc("PASS")
                .addAlleles(TestLilacFactory.builder().allele("A*01:01").tumorCopyNumber(1.2).build())
                .build();
    }

    @NotNull
    private static ChordRecord createTestChordRecord() {
        return TestChordFactory.builder().brca1Value(0).brca2Value(0).hrdType(Strings.EMPTY).hrStatus(ChordStatus.HR_PROFICIENT).build();
    }
}
