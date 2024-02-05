package com.hartwig.oncoact.patientreporter.cfreport.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.purple.ImmutablePurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleTranscriptImpact;
import com.hartwig.oncoact.copynumber.CopyNumberInterpretation;
import com.hartwig.oncoact.copynumber.PurpleGainLossData;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class SomaticVariantsTest {

    @Test
    public void canExtractCodingFromHGVSCodingImpactField() {
        assertEquals(927, SomaticVariants.extractCodonField("c.927+1G>A"));
        assertEquals(1799, SomaticVariants.extractCodonField("c.1799T>A"));
        assertEquals(423, SomaticVariants.extractCodonField("c.423_427delCCCTG"));
        assertEquals(8390, SomaticVariants.extractCodonField("c.8390delA"));
        assertEquals(-124, SomaticVariants.extractCodonField("c.-124C>T"));
    }

    @Test
    public void candDtermineVariantAnnotationCanonical() {
        assertEquals("c. (p.)", SomaticVariants.determineVariantAnnotationCanonical("c.", "p."));
        assertEquals("c.", SomaticVariants.determineVariantAnnotationCanonical("c.", Strings.EMPTY));
    }

    @Test
    public void determineVariantAnnotationClinical() {
        assertEquals(Strings.EMPTY, SomaticVariants.determineVariantAnnotationClinical(null));
        assertEquals("c. (p.)", SomaticVariants.determineVariantAnnotationClinical(purpleTranscriptImpactTest("transcript", "c.", "p.")));
        assertEquals("c.",
                SomaticVariants.determineVariantAnnotationClinical(purpleTranscriptImpactTest("transcript", "c.", Strings.EMPTY)));
    }

    @NotNull
    private static PurpleTranscriptImpact purpleTranscriptImpactTest(@NotNull String transcript, @NotNull String hgvsCodingImpact,
            @NotNull String hgvsProteinImpact) {
        return ImmutablePurpleTranscriptImpact.builder()
                .transcript(transcript)
                .hgvsCodingImpact(hgvsCodingImpact)
                .hgvsProteinImpact(hgvsProteinImpact)
                .spliceRegion(false)
                .codingEffect(PurpleCodingEffect.UNDEFINED)
                .build();
    }

    @Test
    public void sortCorrectlyOnCodon() {
        ReportableVariant variant1 = TestReportableVariantFactory.builder().canonicalHgvsCodingImpact("c.-300T>A").build();
        ReportableVariant variant2 = TestReportableVariantFactory.builder().canonicalHgvsCodingImpact("c.4000T>A").build();
        ReportableVariant variant3 = TestReportableVariantFactory.builder().canonicalHgvsCodingImpact("c.500T>A").build();

        List<ReportableVariant> variants = Lists.newArrayList(variant1, variant2, variant3);

        List<ReportableVariant> sortedVariants = SomaticVariants.sort(variants);

        assertEquals(variant1, sortedVariants.get(0));
        assertEquals(variant3, sortedVariants.get(1));
        assertEquals(variant2, sortedVariants.get(2));
    }

    @Test
    public void geneCompareToAllCases() {
        Map<String, Double> geneToMaximumDriverLikelihood = new HashMap<>();
        geneToMaximumDriverLikelihood.put("BRAF", 0.6);
        geneToMaximumDriverLikelihood.put("BRCA2", 0.7);
        geneToMaximumDriverLikelihood.put("TERT", 0.7);
        geneToMaximumDriverLikelihood.put("TP53", null);
        geneToMaximumDriverLikelihood.put("RAF1", null);

        // by driver likelihood null vs non-null
        assertTrue(SomaticVariants.geneCompareTo("TP53", "BRAF", geneToMaximumDriverLikelihood) >= 1);
        assertTrue(SomaticVariants.geneCompareTo("BRAF", "TP53", geneToMaximumDriverLikelihood) <= -1);

        // by driver likelihood
        assertTrue(SomaticVariants.geneCompareTo("BRAF", "TERT", geneToMaximumDriverLikelihood) >= 1);
        assertTrue(SomaticVariants.geneCompareTo("TERT", "BRAF", geneToMaximumDriverLikelihood) <= -1);

        // by gene name for likelihood non-null
        assertTrue(SomaticVariants.geneCompareTo("TERT", "BRCA2", geneToMaximumDriverLikelihood) >= 1);
        assertTrue(SomaticVariants.geneCompareTo("BRCA2", "TERT", geneToMaximumDriverLikelihood) <= -1);

        // by gene name for likelihood null
        assertTrue(SomaticVariants.geneCompareTo("TP53", "RAF1", geneToMaximumDriverLikelihood) >= 1);
        assertTrue(SomaticVariants.geneCompareTo("RAF1", "TP53", geneToMaximumDriverLikelihood) <= -1);

        // equal
        assertEquals(0, SomaticVariants.geneCompareTo("BRCA2", "BRCA2", geneToMaximumDriverLikelihood));
        assertEquals(0, SomaticVariants.geneCompareTo("TP53", "TP53", geneToMaximumDriverLikelihood));
    }

    @Test
    public void variantCompareToAllCases() {
        ReportableVariant variant1 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(0.6).canonicalHgvsCodingImpact("c.-300T>A").build();
        ReportableVariant variant2 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(null).canonicalHgvsCodingImpact("c.4000T>A").build();
        ReportableVariant variant3 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(0.7).canonicalHgvsCodingImpact("c.300C>G").build();
        ReportableVariant variant4 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(0.6).canonicalHgvsCodingImpact("").build();
        ReportableVariant variant5 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(null).canonicalHgvsCodingImpact("").build();
        ReportableVariant variant6 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(0.6).canonicalHgvsCodingImpact("c.100C>G").build();
        ReportableVariant variant7 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(null).canonicalHgvsCodingImpact("c.100C>G").build();

        // by driver likelihood null vs non-null
        assertTrue(SomaticVariants.variantCompareTo(variant2, variant1) >= 1);
        assertTrue(SomaticVariants.variantCompareTo(variant1, variant2) <= -1);

        // by driver likelihood
        assertTrue(SomaticVariants.variantCompareTo(variant1, variant3) >= 1);
        assertTrue(SomaticVariants.variantCompareTo(variant3, variant1) <= -1);

        // by empty vs non-empty coding impact for likelihood non-null
        assertTrue(SomaticVariants.variantCompareTo(variant4, variant1) >= 1);
        assertTrue(SomaticVariants.variantCompareTo(variant1, variant4) <= -1);

        // by empty vs non-empty coding impact for likelihood null
        assertTrue(SomaticVariants.variantCompareTo(variant5, variant2) >= 1);
        assertTrue(SomaticVariants.variantCompareTo(variant2, variant5) <= -1);

        // by coding impact position for likelihood non-null
        assertTrue(SomaticVariants.variantCompareTo(variant6, variant1) >= 1);
        assertTrue(SomaticVariants.variantCompareTo(variant1, variant6) <= -1);

        // by coding impact position for likelihood null
        assertTrue(SomaticVariants.variantCompareTo(variant2, variant7) >= 1);
        assertTrue(SomaticVariants.variantCompareTo(variant7, variant2) <= -1);

        // equal variants
        assertEquals(0, SomaticVariants.variantCompareTo(variant1, variant1));
        assertEquals(0, SomaticVariants.variantCompareTo(variant2, variant2));
        assertEquals(0, SomaticVariants.variantCompareTo(variant4, variant4));
    }

    @Test
    public void sortCorrectlyComplicated() {
        ReportableVariant variant1 =
                TestReportableVariantFactory.builder().gene("EGFR").driverLikelihood(null).canonicalHgvsCodingImpact("c.500T>A").build();
        ReportableVariant variant2 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(0.6).canonicalHgvsCodingImpact("c.-300T>A").build();
        ReportableVariant variant3 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(null).canonicalHgvsCodingImpact("c.4000T>A").build();
        ReportableVariant variant4 =
                TestReportableVariantFactory.builder().gene("ZRAF").driverLikelihood(0.9).canonicalHgvsCodingImpact("c.500T>A").build();
        ReportableVariant variant5 =
                TestReportableVariantFactory.builder().gene("BRAF").driverLikelihood(0.6).canonicalHgvsCodingImpact("c.200T>A").build();
        ReportableVariant variant6 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(0.6).canonicalHgvsCodingImpact("").build();
        ReportableVariant variant7 =
                TestReportableVariantFactory.builder().gene("BRCA2").driverLikelihood(null).canonicalHgvsCodingImpact("c.700C>G").build();
        ReportableVariant variant8 =
                TestReportableVariantFactory.builder().gene("BRCA2").driverLikelihood(null).canonicalHgvsCodingImpact("c.600A>G").build();
        ReportableVariant variant9 =
                TestReportableVariantFactory.builder().gene("TERT").driverLikelihood(0.2).canonicalHgvsCodingImpact("c.-100A>G").build();
        ReportableVariant variant10 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(0.6).canonicalHgvsCodingImpact("c.100A>G").build();
        ReportableVariant variant11 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(null).canonicalHgvsCodingImpact("c.100A>G").build();
        ReportableVariant variant12 =
                TestReportableVariantFactory.builder().gene("KRAS").driverLikelihood(null).canonicalHgvsCodingImpact("c.100C>G").build();

        List<ReportableVariant> variants = Lists.newArrayList(variant1,
                variant2,
                variant3,
                variant4,
                variant5,
                variant6,
                variant7,
                variant8,
                variant9,
                variant10,
                variant11,
                variant12);

        List<ReportableVariant> sortedVariants = SomaticVariants.sort(variants);

        assertEquals(variant4, sortedVariants.get(0));
        assertEquals(variant5, sortedVariants.get(1));
        assertEquals(variant2, sortedVariants.get(2));
        assertEquals(variant10, sortedVariants.get(3));
        assertEquals(variant6, sortedVariants.get(4));
        assertEquals(variant11, sortedVariants.get(5));
        assertEquals(variant12, sortedVariants.get(6));
        assertEquals(variant3, sortedVariants.get(7));
        assertEquals(variant9, sortedVariants.get(8));
        assertEquals(variant8, sortedVariants.get(9));
        assertEquals(variant7, sortedVariants.get(10));
        assertEquals(variant1, sortedVariants.get(11));
    }

    @Test
    public void canExtractMSIGenes() {
        ReportableVariant variant1 = TestReportableVariantFactory.builder().gene("MLH1").build();
        ReportableVariant variant2 = TestReportableVariantFactory.builder().gene("BRAF").build();

        List<ReportableVariant> variants = Lists.newArrayList(variant1, variant2);

        PurpleGainLossData baseGainLoss = createGainLoss("1", "p.12");
        PurpleGainLossData gainLoss1 = TestPurpleFactory.gainLossBuilderOncoAct()
                .from(baseGainLoss)
                .gene("MSH2")
                .interpretation(com.hartwig.oncoact.copynumber.CopyNumberInterpretation.FULL_LOSS)
                .build();
        PurpleGainLossData gainLoss2 = TestPurpleFactory.gainLossBuilderOncoAct()
                .from(baseGainLoss)
                .gene("MSH6")
                .interpretation(com.hartwig.oncoact.copynumber.CopyNumberInterpretation.PARTIAL_LOSS)
                .build();
        PurpleGainLossData gainLoss3 = TestPurpleFactory.gainLossBuilderOncoAct()
                .from(baseGainLoss)
                .gene("EPCAM")
                .interpretation(CopyNumberInterpretation.FULL_GAIN)
                .build();

        List<PurpleGainLossData> gainLosses = Lists.newArrayList(gainLoss1, gainLoss2, gainLoss3);

        List<HomozygousDisruption> homozygousDisruption = Lists.newArrayList(createHomozygousDisruption("PMS2"));

        assertEquals(4, SomaticVariants.determineMSIGenes(variants, gainLosses, homozygousDisruption).size());
    }

    @Test
    public void canExtractHRDGenes() {
        ReportableVariant variant1 = TestReportableVariantFactory.builder().gene("BRCA1").build();
        ReportableVariant variant2 = TestReportableVariantFactory.builder().gene("BRAF").build();

        List<ReportableVariant> variants = Lists.newArrayList(variant1, variant2);

        PurpleGainLossData baseGainLoss = createGainLoss("1", "p.12");
        PurpleGainLossData gainLoss1 = TestPurpleFactory.gainLossBuilderOncoAct()
                .from(baseGainLoss)
                .gene("BRCA2")
                .interpretation(com.hartwig.oncoact.copynumber.CopyNumberInterpretation.FULL_LOSS)
                .build();
        PurpleGainLossData gainLoss2 = TestPurpleFactory.gainLossBuilderOncoAct()
                .from(baseGainLoss)
                .gene("PALB2")
                .interpretation(com.hartwig.oncoact.copynumber.CopyNumberInterpretation.PARTIAL_LOSS)
                .build();
        PurpleGainLossData gainLoss3 = TestPurpleFactory.gainLossBuilderOncoAct()
                .from(baseGainLoss)
                .gene("RAD51B")
                .interpretation(com.hartwig.oncoact.copynumber.CopyNumberInterpretation.FULL_GAIN)
                .build();

        List<PurpleGainLossData> gainLosses = Lists.newArrayList(gainLoss1, gainLoss2, gainLoss3);

        List<HomozygousDisruption> homozygousDisruption = Lists.newArrayList(createHomozygousDisruption("RAD51C"));

        assertEquals(4, SomaticVariants.determineHRDGenes(variants, gainLosses, homozygousDisruption).size());
    }

    @NotNull
    private static HomozygousDisruption createHomozygousDisruption(@NotNull String gene) {
        return TestLinxFactory.homozygousDisruptionBuilder().gene(gene).build();
    }

    @NotNull
    private static PurpleGainLossData createGainLoss(@NotNull String chromosome, @NotNull String chromosomeBand) {
        return TestPurpleFactory.gainLossBuilderOncoAct().chromosome(chromosome).chromosomeBand(chromosomeBand).build();
    }
}