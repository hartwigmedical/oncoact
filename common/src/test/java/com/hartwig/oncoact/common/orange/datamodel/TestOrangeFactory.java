package com.hartwig.oncoact.common.orange.datamodel;

import java.time.LocalDate;

import com.hartwig.oncoact.common.orange.datamodel.chord.ChordRecord;
import com.hartwig.oncoact.common.orange.datamodel.chord.ImmutableChordRecord;
import com.hartwig.oncoact.common.orange.datamodel.cuppa.CuppaRecord;
import com.hartwig.oncoact.common.orange.datamodel.cuppa.ImmutableCuppaRecord;
import com.hartwig.oncoact.common.orange.datamodel.cuppa.TestCuppaFactory;
import com.hartwig.oncoact.common.orange.datamodel.lilac.ImmutableLilacRecord;
import com.hartwig.oncoact.common.orange.datamodel.lilac.LilacRecord;
import com.hartwig.oncoact.common.orange.datamodel.lilac.TestLilacFactory;
import com.hartwig.oncoact.common.orange.datamodel.linx.ImmutableLinxRecord;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxBreakendType;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxFusionDriverLikelihood;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxFusionType;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxRecord;
import com.hartwig.oncoact.common.orange.datamodel.linx.TestLinxFactory;
import com.hartwig.oncoact.common.orange.datamodel.peach.ImmutablePeachRecord;
import com.hartwig.oncoact.common.orange.datamodel.peach.PeachRecord;
import com.hartwig.oncoact.common.orange.datamodel.peach.TestPeachFactory;
import com.hartwig.oncoact.common.orange.datamodel.purple.ImmutablePurpleRecord;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleCharacteristics;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleCodingEffect;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleCopyNumberInterpretation;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleDriverType;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleFit;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleHotspotType;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleRecord;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleVariantEffect;
import com.hartwig.oncoact.common.orange.datamodel.purple.TestPurpleFactory;
import com.hartwig.oncoact.common.orange.datamodel.virus.ImmutableVirusInterpreterRecord;
import com.hartwig.oncoact.common.orange.datamodel.virus.TestVirusInterpreterFactory;
import com.hartwig.oncoact.common.orange.datamodel.virus.VirusDriverLikelihood;
import com.hartwig.oncoact.common.orange.datamodel.virus.VirusInterpretation;
import com.hartwig.oncoact.common.orange.datamodel.virus.VirusInterpreterRecord;
import com.hartwig.oncoact.common.orange.datamodel.virus.VirusQCStatus;

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
                .chord(createMinimalTestChordRecord())
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
    private static ChordRecord createMinimalTestChordRecord() {
        return ImmutableChordRecord.builder().hrStatus(Strings.EMPTY).build();
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
        return ImmutablePurpleRecord.builder()
                .from(createMinimalTestPurpleRecord())
                .fit(createTestPurpleFit())
                .characteristics(createTestPurpleCharacteristics())
                .addDrivers(TestPurpleFactory.driverBuilder().gene("BRAF").type(PurpleDriverType.MUTATION).driverLikelihood(1D).build())
                .addDrivers(TestPurpleFactory.driverBuilder().gene("MYC").type(PurpleDriverType.AMP).driverLikelihood(1D).build())
                .addDrivers(TestPurpleFactory.driverBuilder().gene("PTEN").type(PurpleDriverType.DEL).driverLikelihood(1D).build())
                .addVariants(TestPurpleFactory.variantBuilder()
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
                        .build())
                .addCopyNumbers(TestPurpleFactory.copyNumberBuilder()
                        .gene("MYC")
                        .interpretation(PurpleCopyNumberInterpretation.FULL_GAIN)
                        .minCopies(38)
                        .maxCopies(40)
                        .build())
                .addCopyNumbers(TestPurpleFactory.copyNumberBuilder()
                        .gene("PTEN")
                        .interpretation(PurpleCopyNumberInterpretation.FULL_LOSS)
                        .minCopies(0)
                        .maxCopies(0)
                        .build())
                .build();
    }

    @NotNull
    private static PurpleFit createTestPurpleFit() {
        return TestPurpleFactory.fitBuilder().hasReliableQuality(true).hasReliablePurity(true).purity(0.98).ploidy(3.1).build();
    }

    @NotNull
    private static PurpleCharacteristics createTestPurpleCharacteristics() {
        return TestPurpleFactory.characteristicsBuilder()
                .microsatelliteStabilityStatus("MSS")
                .tumorMutationalBurden(13D)
                .tumorMutationalBurdenStatus("HIGH")
                .tumorMutationalLoad(189)
                .tumorMutationalLoadStatus("HIGH")
                .build();
    }

    @NotNull
    private static LinxRecord createTestLinxRecord() {
        return ImmutableLinxRecord.builder()
                .addStructuralVariants(TestLinxFactory.structuralVariantBuilder().svId(1).clusterId(1).build())
                .addHomozygousDisruptions(TestLinxFactory.homozygousDisruptionBuilder().gene("TP53").build())
                .addBreakends(TestLinxFactory.breakendBuilder()
                        .reported(true)
                        .svId(1)
                        .gene("RB1")
                        .type(LinxBreakendType.DEL)
                        .junctionCopyNumber(0.8)
                        .undisruptedCopyNumber(2.1)
                        .build())
                .addBreakends(TestLinxFactory.breakendBuilder()
                        .reported(true)
                        .svId(1)
                        .gene("PTEN")
                        .type(LinxBreakendType.DEL)
                        .junctionCopyNumber(1D)
                        .undisruptedCopyNumber(1D)
                        .build())
                .addFusions(TestLinxFactory.fusionBuilder()
                        .reported(true)
                        .type(LinxFusionType.KNOWN_PAIR)
                        .geneStart("EML4")
                        .fusedExonUp(2)
                        .geneEnd("ALK")
                        .fusedExonDown(4)
                        .driverLikelihood(LinxFusionDriverLikelihood.HIGH)
                        .build())
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
        return ImmutableVirusInterpreterRecord.builder()
                .addEntries(TestVirusInterpreterFactory.builder()
                        .reported(true)
                        .name("Human papillomavirus type 16")
                        .qcStatus(VirusQCStatus.NO_ABNORMALITIES)
                        .interpretation(VirusInterpretation.HPV)
                        .integrations(3)
                        .driverLikelihood(VirusDriverLikelihood.HIGH)
                        .build())
                .build();
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
        return ImmutableChordRecord.builder().hrStatus("HR_PROFICIENT").build();
    }
}
