package com.hartwig.oncoact.orange;

import java.time.LocalDate;

import com.hartwig.oncoact.orange.chord.ChordRecord;
import com.hartwig.oncoact.orange.chord.ChordStatus;
import com.hartwig.oncoact.orange.chord.TestChordFactory;
import com.hartwig.oncoact.orange.cuppa.CuppaRecord;
import com.hartwig.oncoact.orange.cuppa.ImmutableCuppaRecord;
import com.hartwig.oncoact.orange.cuppa.TestCuppaFactory;
import com.hartwig.oncoact.orange.lilac.ImmutableLilacRecord;
import com.hartwig.oncoact.orange.lilac.LilacRecord;
import com.hartwig.oncoact.orange.lilac.TestLilacFactory;
import com.hartwig.oncoact.orange.linx.ImmutableLinxRecord;
import com.hartwig.oncoact.orange.linx.LinxBreakend;
import com.hartwig.oncoact.orange.linx.LinxBreakendType;
import com.hartwig.oncoact.orange.linx.LinxFusion;
import com.hartwig.oncoact.orange.linx.LinxFusionDriverLikelihood;
import com.hartwig.oncoact.orange.linx.LinxFusionType;
import com.hartwig.oncoact.orange.linx.LinxRecord;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;
import com.hartwig.oncoact.orange.peach.ImmutablePeachRecord;
import com.hartwig.oncoact.orange.peach.PeachRecord;
import com.hartwig.oncoact.orange.peach.TestPeachFactory;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleRecord;
import com.hartwig.oncoact.orange.purple.PurpleCharacteristics;
import com.hartwig.oncoact.orange.purple.PurpleCodingEffect;
import com.hartwig.oncoact.orange.purple.PurpleDriverType;
import com.hartwig.oncoact.orange.purple.PurpleFit;
import com.hartwig.oncoact.orange.purple.PurpleGainLoss;
import com.hartwig.oncoact.orange.purple.PurpleGainLossInterpretation;
import com.hartwig.oncoact.orange.purple.PurpleHotspotType;
import com.hartwig.oncoact.orange.purple.PurpleMicrosatelliteStatus;
import com.hartwig.oncoact.orange.purple.PurpleRecord;
import com.hartwig.oncoact.orange.purple.PurpleTumorMutationalStatus;
import com.hartwig.oncoact.orange.purple.PurpleVariant;
import com.hartwig.oncoact.orange.purple.PurpleVariantEffect;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.orange.virus.ImmutableVirusInterpreterRecord;
import com.hartwig.oncoact.orange.virus.TestVirusInterpreterFactory;
import com.hartwig.oncoact.orange.virus.VirusDriverLikelihood;
import com.hartwig.oncoact.orange.virus.VirusInterpretation;
import com.hartwig.oncoact.orange.virus.VirusInterpreterEntry;
import com.hartwig.oncoact.orange.virus.VirusInterpreterRecord;
import com.hartwig.oncoact.orange.virus.VirusQCStatus;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestOrangeFactory {

    private TestOrangeFactory() {
    }

    @NotNull
    public static OrangeRecord createMinimalTestOrangeRecord() {
        return ImmutableOrangeRecord.builder()
                .sampleId("TEST")
                .experimentDate(LocalDate.of(2021, 5, 6))
                .refGenomeVersion(OrangeRefGenomeVersion.V37)
                .purple(createMinimalTestPurpleRecord())
                .linx(ImmutableLinxRecord.builder().build())
                .peach(ImmutablePeachRecord.builder().build())
                .cuppa(ImmutableCuppaRecord.builder().build())
                .virusInterpreter(ImmutableVirusInterpreterRecord.builder().build())
                .lilac(createMinimalTestLilacRecord())
                .chord(TestChordFactory.builder().build())
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
        PurpleVariant variant = TestPurpleFactory.variantBuilder()
                .reported(true)
                .gene("BRAF")
                .adjustedCopyNumber(6.0)
                .variantCopyNumber(4.1)
                .hotspot(PurpleHotspotType.HOTSPOT)
                .subclonalLikelihood(0.02)
                .biallelic(false)
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder()
                        .hgvsCodingImpact("c.something")
                        .hgvsProteinImpact("p.Val600Glu")
                        .spliceRegion(false)
                        .addEffects(PurpleVariantEffect.MISSENSE)
                        .codingEffect(PurpleCodingEffect.MISSENSE)
                        .build())
                .build();

        PurpleGainLoss gain = TestPurpleFactory.gainLossBuilder()
                .gene("MYC")
                .interpretation(PurpleGainLossInterpretation.FULL_GAIN)
                .minCopies(38)
                .maxCopies(40)
                .build();

        PurpleGainLoss loss = TestPurpleFactory.gainLossBuilder()
                .gene("PTEN")
                .interpretation(PurpleGainLossInterpretation.FULL_LOSS)
                .minCopies(0)
                .maxCopies(0)
                .build();

        return ImmutablePurpleRecord.builder()
                .from(createMinimalTestPurpleRecord())
                .fit(createTestPurpleFit())
                .characteristics(createTestPurpleCharacteristics())
                .addSomaticDrivers(TestPurpleFactory.driverBuilder()
                        .gene(variant.gene())
                        .type(PurpleDriverType.MUTATION)
                        .driverLikelihood(1D)
                        .build())
                .addSomaticDrivers(TestPurpleFactory.driverBuilder()
                        .gene(gain.gene())
                        .type(PurpleDriverType.AMP)
                        .driverLikelihood(1D)
                        .build())
                .addSomaticDrivers(TestPurpleFactory.driverBuilder()
                        .gene(loss.gene())
                        .type(PurpleDriverType.DEL)
                        .driverLikelihood(1D)
                        .build())
                .addAllSomaticVariants(variant)
                .addReportableSomaticVariants(variant)
                .addAllSomaticGainsLosses(gain, loss)
                .addReportableSomaticGainsLosses(gain, loss)
                .build();
    }

    @NotNull
    private static PurpleFit createTestPurpleFit() {
        return TestPurpleFactory.fitBuilder().hasSufficientQuality(true).containsTumorCells(true).purity(0.98).ploidy(3.1).build();
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
                .type(LinxFusionType.KNOWN_PAIR)
                .geneStart("EML4")
                .fusedExonUp(2)
                .geneEnd("ALK")
                .fusedExonDown(4)
                .driverLikelihood(LinxFusionDriverLikelihood.HIGH)
                .build();

        return ImmutableLinxRecord.builder()
                .addStructuralVariants(TestLinxFactory.structuralVariantBuilder().svId(1).clusterId(1).build())
                .addHomozygousDisruptions(TestLinxFactory.homozygousDisruptionBuilder().gene("TP53").build())
                .addAllBreakends(breakend1, breakend2)
                .addReportableBreakends(breakend1, breakend2)
                .addAllFusions(fusion)
                .addReportableFusions(fusion)
                .build();
    }

    @NotNull
    private static PeachRecord createTestPeachRecord() {
        return ImmutablePeachRecord.builder()
                .addEntries(TestPeachFactory.builder().gene("DPYD").haplotype("1* HOM").function("Normal function").build())
                .build();
    }

    @NotNull
    private static CuppaRecord createTestCuppaRecord() {
        return ImmutableCuppaRecord.builder()
                .addPredictions(TestCuppaFactory.builder().cancerType("Melanoma").likelihood(0.996).build())
                .build();
    }

    @NotNull
    private static VirusInterpreterRecord createTestVirusInterpreterRecord() {
        VirusInterpreterEntry virus = TestVirusInterpreterFactory.builder()
                .reported(true)
                .name("Human papillomavirus type 16")
                .qcStatus(VirusQCStatus.NO_ABNORMALITIES)
                .interpretation(VirusInterpretation.HPV)
                .integrations(3)
                .driverLikelihood(VirusDriverLikelihood.HIGH)
                .build();

        return ImmutableVirusInterpreterRecord.builder().addAllViruses(virus).addReportableViruses(virus).build();
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
        return TestChordFactory.builder().hrStatus(ChordStatus.HR_PROFICIENT).build();
    }
}
