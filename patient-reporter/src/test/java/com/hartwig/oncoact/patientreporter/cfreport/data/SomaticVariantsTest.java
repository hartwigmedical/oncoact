package com.hartwig.oncoact.patientreporter.cfreport.data;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.orange.linx.LinxHomozygousDisruption;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;
import com.hartwig.oncoact.orange.purple.PurpleGainLoss;
import com.hartwig.oncoact.orange.purple.PurpleGainLossInterpretation;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;

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
    public void canExtractMSIGenes() {
        ReportableVariant variant1 = TestReportableVariantFactory.builder().gene("MLH1").build();
        ReportableVariant variant2 = TestReportableVariantFactory.builder().gene("BRAF").build();

        List<ReportableVariant> variants = Lists.newArrayList(variant1, variant2);

        PurpleGainLoss baseGainLoss = createGainLoss("1", "p.12");
        PurpleGainLoss gainLoss1 = TestPurpleFactory.gainLossBuilder()
                .from(baseGainLoss)
                .gene("MSH2")
                .interpretation(PurpleGainLossInterpretation.FULL_LOSS)
                .build();
        PurpleGainLoss gainLoss2 = TestPurpleFactory.gainLossBuilder()
                .from(baseGainLoss)
                .gene("MSH6")
                .interpretation(PurpleGainLossInterpretation.PARTIAL_LOSS)
                .build();
        PurpleGainLoss gainLoss3 = TestPurpleFactory.gainLossBuilder()
                .from(baseGainLoss)
                .gene("EPCAM")
                .interpretation(PurpleGainLossInterpretation.FULL_GAIN)
                .build();

        List<PurpleGainLoss> gainLosses = Lists.newArrayList(gainLoss1, gainLoss2, gainLoss3);

        List<LinxHomozygousDisruption> homozygousDisruption = Lists.newArrayList(createHomozygousDisruption("PMS2"));

        assertEquals(4, SomaticVariants.determineMSIGenes(variants, gainLosses, homozygousDisruption).size());
    }

    @Test
    public void canExtractHRDGenes() {
        ReportableVariant variant1 = TestReportableVariantFactory.builder().gene("BRCA1").build();
        ReportableVariant variant2 = TestReportableVariantFactory.builder().gene("BRAF").build();

        List<ReportableVariant> variants = Lists.newArrayList(variant1, variant2);

        PurpleGainLoss baseGainLoss = createGainLoss("1", "p.12");
        PurpleGainLoss gainLoss1 = TestPurpleFactory.gainLossBuilder()
                .from(baseGainLoss)
                .gene("BRCA2")
                .interpretation(PurpleGainLossInterpretation.FULL_LOSS)
                .build();
        PurpleGainLoss gainLoss2 = TestPurpleFactory.gainLossBuilder()
                .from(baseGainLoss)
                .gene("PALB2")
                .interpretation(PurpleGainLossInterpretation.PARTIAL_LOSS)
                .build();
        PurpleGainLoss gainLoss3 = TestPurpleFactory.gainLossBuilder()
                .from(baseGainLoss)
                .gene("RAD51B")
                .interpretation(PurpleGainLossInterpretation.FULL_GAIN)
                .build();

        List<PurpleGainLoss> gainLosses = Lists.newArrayList(gainLoss1, gainLoss2, gainLoss3);

        List<LinxHomozygousDisruption> homozygousDisruption = Lists.newArrayList(createHomozygousDisruption("RAD51C"));

        assertEquals(4, SomaticVariants.determineHRDGenes(variants, gainLosses, homozygousDisruption).size());
    }

    @NotNull
    private static LinxHomozygousDisruption createHomozygousDisruption(@NotNull String gene) {
        return TestLinxFactory.homozygousDisruptionBuilder().gene(gene).build();
    }

    @NotNull
    private static PurpleGainLoss createGainLoss(@NotNull String chromosome, @NotNull String chromosomeBand) {
        return TestPurpleFactory.gainLossBuilder().chromosome(chromosome).chromosomeBand(chromosomeBand).build();
    }
}