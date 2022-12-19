package com.hartwig.oncoact.common.orange.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDate;

import com.google.common.io.Resources;
import com.hartwig.oncoact.common.orange.datamodel.OrangeRecord;
import com.hartwig.oncoact.common.orange.datamodel.OrangeRefGenomeVersion;
import com.hartwig.oncoact.common.orange.datamodel.chord.ChordRecord;
import com.hartwig.oncoact.common.orange.datamodel.cuppa.CuppaPrediction;
import com.hartwig.oncoact.common.orange.datamodel.cuppa.CuppaRecord;
import com.hartwig.oncoact.common.orange.datamodel.lilac.LilacHlaAllele;
import com.hartwig.oncoact.common.orange.datamodel.lilac.LilacRecord;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxBreakend;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxBreakendType;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxCodingType;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxFusion;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxFusionDriverLikelihood;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxFusionType;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxHomozygousDisruption;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxRecord;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxRegionType;
import com.hartwig.oncoact.common.orange.datamodel.linx.LinxStructuralVariant;
import com.hartwig.oncoact.common.orange.datamodel.peach.PeachEntry;
import com.hartwig.oncoact.common.orange.datamodel.peach.PeachRecord;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleCodingEffect;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleCopyNumber;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleCopyNumberInterpretation;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleDriver;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleDriverType;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleHotspotType;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleRecord;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleVariant;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleVariantEffect;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleVariantType;
import com.hartwig.oncoact.common.orange.datamodel.virus.VirusDriverLikelihood;
import com.hartwig.oncoact.common.orange.datamodel.virus.VirusInterpretation;
import com.hartwig.oncoact.common.orange.datamodel.virus.VirusInterpreterEntry;
import com.hartwig.oncoact.common.orange.datamodel.virus.VirusInterpreterRecord;
import com.hartwig.oncoact.common.orange.datamodel.virus.VirusQCStatus;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class OrangeJsonTest {

    private static final String MINIMALLY_EMPTY_ORANGE_JSON = Resources.getResource("orange/minimally.empty.orange.json").getPath();
    private static final String MINIMALLY_POPULATED_ORANGE_JSON = Resources.getResource("orange/minimally.populated.orange.json").getPath();
    private static final String REAL_ORANGE_JSON = Resources.getResource("orange/real.orange.json").getPath();

    private static final double EPSILON = 1.0E-2;

    @Test
    public void canReadMinimallyEmptyOrangeRecordJson() throws IOException {
        assertNotNull(OrangeJson.read(MINIMALLY_EMPTY_ORANGE_JSON));
    }

    @Test
    public void canReadRealOrangeRecordJson() throws IOException {
        assertNotNull(OrangeJson.read(REAL_ORANGE_JSON));
    }

    @Test
    public void canReadMinimallyPopulatedOrangeRecordJson() throws IOException {
        OrangeRecord record = OrangeJson.read(MINIMALLY_POPULATED_ORANGE_JSON);

        assertEquals("TEST", record.sampleId());
        assertEquals(LocalDate.of(2022, 1, 20), record.experimentDate());
        assertEquals(OrangeRefGenomeVersion.V37, record.refGenomeVersion());

        assertPurple(record.purple());
        assertLinx(record.linx());
        assertPeach(record.peach());
        assertCuppa(record.cuppa());
        assertVirusInterpreter(record.virusInterpreter());
        assertLilac(record.lilac());
        assertChord(record.chord());
    }

    private static void assertPurple(@NotNull PurpleRecord purple) {
        assertTrue(purple.fit().hasReliableQuality());
        assertFalse(purple.fit().hasReliablePurity());
        assertEquals(0.12, purple.fit().purity(), EPSILON);
        assertEquals(3.1, purple.fit().ploidy(), EPSILON);

        assertEquals("MSS", purple.characteristics().microsatelliteStabilityStatus());
        assertEquals(13.71, purple.characteristics().tumorMutationalBurden(), EPSILON);
        assertEquals("HIGH", purple.characteristics().tumorMutationalBurdenStatus());
        assertEquals(185, purple.characteristics().tumorMutationalLoad());
        assertEquals("HIGH", purple.characteristics().tumorMutationalLoadStatus());

        assertEquals(3, purple.drivers().size());
        PurpleDriver driver1 = findDriverByGene(purple.drivers(), "SF3B1");
        assertEquals("ENST00000335508", driver1.transcript());
        assertEquals(PurpleDriverType.MUTATION, driver1.type());
        assertEquals(0.2, driver1.driverLikelihood(), EPSILON);

        PurpleDriver driver2 = findDriverByGene(purple.drivers(), "SMAD4");
        assertEquals("ENST00000342988", driver2.transcript());
        assertEquals(PurpleDriverType.DEL, driver2.type());
        assertEquals(1.0, driver2.driverLikelihood(), EPSILON);

        PurpleDriver driver3 = findDriverByGene(purple.drivers(), "BRCA1");
        assertEquals("ENST00000471181", driver3.transcript());
        assertEquals(PurpleDriverType.GERMLINE_MUTATION, driver3.type());
        assertEquals(0.8, driver3.driverLikelihood(), EPSILON);

        assertEquals(2, purple.variants().size());
        PurpleVariant variant1 = findVariantByGene(purple.variants(), "SF3B1");
        assertTrue(variant1.reported());
        assertEquals(PurpleVariantType.SNP, variant1.type());
        assertEquals("2", variant1.chromosome());
        assertEquals(198266779, variant1.position());
        assertEquals("G", variant1.ref());
        assertEquals("A", variant1.alt());
        assertEquals(2.03, variant1.variantCopyNumber(), EPSILON);
        assertEquals(3.02, variant1.adjustedCopyNumber(), EPSILON);
        assertEquals(PurpleHotspotType.NON_HOTSPOT, variant1.hotspot());
        assertEquals(0.0, variant1.subclonalLikelihood(), EPSILON);
        assertFalse(variant1.biallelic());
        assertNull(variant1.localPhaseSets());
        assertEquals("ENST00000335508", variant1.canonicalImpact().transcript());
        assertEquals("c.2153C>T", variant1.canonicalImpact().hgvsCodingImpact());
        assertEquals("p.Pro718Leu", variant1.canonicalImpact().hgvsProteinImpact());
        assertEquals(2153, (int) variant1.canonicalImpact().affectedCodon());
        assertEquals(12, (int) variant1.canonicalImpact().affectedExon());
        assertFalse(variant1.canonicalImpact().spliceRegion());
        assertEquals(1, variant1.canonicalImpact().effects().size());
        assertTrue(variant1.canonicalImpact().effects().contains(PurpleVariantEffect.MISSENSE));
        assertEquals(PurpleCodingEffect.MISSENSE, variant1.canonicalImpact().codingEffect());
        assertTrue(variant1.otherImpacts().isEmpty());

        PurpleVariant variant2 = findVariantByGene(purple.variants(), "BRCA1");
        assertTrue(variant2.reported());
        assertEquals(PurpleVariantType.SNP, variant2.type());
        assertEquals("17", variant2.chromosome());
        assertEquals(41209068, variant2.position());
        assertEquals("C", variant2.ref());
        assertEquals("T", variant2.alt());
        assertEquals(1.0, variant2.variantCopyNumber(), EPSILON);
        assertEquals(2.0, variant2.adjustedCopyNumber(), EPSILON);
        assertEquals(PurpleHotspotType.HOTSPOT, variant2.hotspot());
        assertEquals(0.2, variant2.subclonalLikelihood(), EPSILON);
        assertFalse(variant2.biallelic());
        assertEquals(2, variant2.localPhaseSets().size());
        assertTrue(variant2.localPhaseSets().contains(1));
        assertTrue(variant2.localPhaseSets().contains(2));
        assertEquals("ENST00000471181", variant2.canonicalImpact().transcript());
        assertEquals("c.5340+1G>A", variant2.canonicalImpact().hgvsCodingImpact());
        assertEquals("p.?", variant2.canonicalImpact().hgvsProteinImpact());
        assertNull(variant2.canonicalImpact().affectedCodon());
        assertNull(variant2.canonicalImpact().affectedExon());
        assertTrue(variant2.canonicalImpact().spliceRegion());
        assertEquals(2, variant2.canonicalImpact().effects().size());
        assertTrue(variant2.canonicalImpact().effects().contains(PurpleVariantEffect.SPLICE_DONOR));
        assertTrue(variant2.canonicalImpact().effects().contains(PurpleVariantEffect.INTRONIC));
        assertEquals(PurpleCodingEffect.SPLICE, variant2.canonicalImpact().codingEffect());
        assertTrue(variant2.otherImpacts().isEmpty());

        assertEquals(1, purple.copyNumbers().size());
        PurpleCopyNumber copyNumber = purple.copyNumbers().iterator().next();
        assertEquals("SMAD4", copyNumber.gene());
        assertEquals(PurpleCopyNumberInterpretation.FULL_LOSS, copyNumber.interpretation());
        assertEquals(0, copyNumber.minCopies());
        assertEquals(1, copyNumber.maxCopies());
    }

    @NotNull
    private static PurpleDriver findDriverByGene(@NotNull Iterable<PurpleDriver> drivers, @NotNull String geneToFind) {
        for (PurpleDriver driver : drivers) {
            if (driver.gene().equals(geneToFind)) {
                return driver;
            }
        }

        throw new IllegalStateException("Could not find driver for gene: " + geneToFind);
    }

    @NotNull
    private static PurpleVariant findVariantByGene(@NotNull Iterable<PurpleVariant> variants, @NotNull String geneToFind) {
        for (PurpleVariant variant : variants) {
            if (variant.gene().equals(geneToFind)) {
                return variant;
            }
        }

        throw new IllegalStateException("Could not find variant for gene: " + geneToFind);
    }

    private static void assertLinx(@NotNull LinxRecord linx) {
        assertEquals(1, linx.structuralVariants().size());
        LinxStructuralVariant structuralVariant = linx.structuralVariants().iterator().next();
        assertEquals(1, structuralVariant.svId());
        assertEquals(2, structuralVariant.clusterId());

        assertEquals(1, linx.homozygousDisruptions().size());
        LinxHomozygousDisruption homozygousDisruption = linx.homozygousDisruptions().iterator().next();
        assertEquals("NF1", homozygousDisruption.gene());

        assertEquals(1, linx.breakends().size());
        LinxBreakend breakend = linx.breakends().iterator().next();
        assertFalse(breakend.reported());
        assertEquals(1, breakend.svId());
        assertEquals("NF1", breakend.gene());
        assertEquals(LinxBreakendType.DUP, breakend.type());
        assertEquals(1.1, breakend.junctionCopyNumber(), EPSILON);
        assertEquals(1.0, breakend.undisruptedCopyNumber(), EPSILON);
        assertEquals(LinxRegionType.EXONIC, breakend.regionType());
        assertEquals(LinxCodingType.UTR_3P, breakend.codingType());

        assertEquals(1, linx.fusions().size());
        LinxFusion fusion = linx.fusions().iterator().next();
        assertTrue(fusion.reported());
        assertEquals(LinxFusionType.KNOWN_PAIR, fusion.type());
        assertEquals("TMPRSS2", fusion.geneStart());
        assertEquals("ENST00000332149", fusion.geneTranscriptStart());
        assertEquals(1, fusion.fusedExonUp());
        assertEquals("ETV4", fusion.geneEnd());
        assertEquals("ENST00000319349", fusion.geneTranscriptEnd());
        assertEquals(2, fusion.fusedExonDown());
        assertEquals(LinxFusionDriverLikelihood.HIGH, fusion.driverLikelihood());
    }

    private static void assertPeach(@NotNull PeachRecord peach) {
        assertEquals(1, peach.entries().size());
        PeachEntry entry = peach.entries().iterator().next();
        assertEquals("DPYD", entry.gene());
        assertEquals("*1_HOM", entry.haplotype());
        assertEquals("Normal Function", entry.function());
    }

    private static void assertCuppa(@NotNull CuppaRecord cuppa) {
        assertEquals(1, cuppa.predictions().size());
        CuppaPrediction prediction = cuppa.predictions().iterator().next();
        assertEquals("Melanoma", prediction.cancerType());
        assertEquals(0.996, prediction.likelihood(), EPSILON);
    }

    private static void assertVirusInterpreter(@NotNull VirusInterpreterRecord virusInterpreter) {
        assertEquals(2, virusInterpreter.entries().size());
        VirusInterpreterEntry entry1 = findVirusByName(virusInterpreter.entries(), "Human papillomavirus 16");
        assertTrue(entry1.reported());
        assertEquals(VirusQCStatus.NO_ABNORMALITIES, entry1.qcStatus());
        assertEquals(VirusInterpretation.HPV, entry1.interpretation());
        assertEquals(1, entry1.integrations());
        assertEquals(VirusDriverLikelihood.HIGH, entry1.driverLikelihood());

        VirusInterpreterEntry entry2 = findVirusByName(virusInterpreter.entries(), "Human betaherpesvirus 6B");
        assertFalse(entry2.reported());
        assertEquals(VirusQCStatus.NO_ABNORMALITIES, entry2.qcStatus());
        assertNull(entry2.interpretation());
        assertEquals(0, entry2.integrations());
        assertEquals(VirusDriverLikelihood.LOW, entry2.driverLikelihood());
    }

    @NotNull
    private static VirusInterpreterEntry findVirusByName(@NotNull Iterable<VirusInterpreterEntry> entries, @NotNull String nameToFind) {
        for (VirusInterpreterEntry entry : entries) {
            if (entry.name().equals(nameToFind)) {
                return entry;
            }
        }

        throw new IllegalStateException("Could not find virus with name: " + nameToFind);
    }

    private static void assertLilac(@NotNull LilacRecord lilac) {
        assertEquals("PASS", lilac.qc());

        assertEquals(1, lilac.alleles().size());
        LilacHlaAllele allele = lilac.alleles().iterator().next();
        assertEquals("A*01:01", allele.allele());
        assertEquals(6.1, allele.tumorCopyNumber(), EPSILON);
        assertEquals(5.0, allele.somaticMissense(), EPSILON);
        assertEquals(4.0, allele.somaticNonsenseOrFrameshift(), EPSILON);
        assertEquals(3.0, allele.somaticSplice(), EPSILON);
        assertEquals(1.0, allele.somaticInframeIndel(), EPSILON);
    }

    private static void assertChord(@NotNull ChordRecord chord) {
        assertEquals("HR_PROFICIENT", chord.hrStatus());
    }
}