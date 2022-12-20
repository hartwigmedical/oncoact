package com.hartwig.oncoact.wildtype;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.oncoact.drivergene.TestDriverGeneFactory;
import com.hartwig.oncoact.orange.linx.LinxBreakend;
import com.hartwig.oncoact.orange.linx.LinxFusion;
import com.hartwig.oncoact.orange.linx.LinxHomozygousDisruption;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;
import com.hartwig.oncoact.orange.purple.PurpleGainLoss;
import com.hartwig.oncoact.orange.purple.PurpleGainLossInterpretation;
import com.hartwig.oncoact.orange.purple.PurpleQCStatus;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class WildTypeFactoryTest {

    @Test
    public void canDetermineWildTypeSomatic() {
        Set<ReportableVariant> reportableGermlineVariants = Sets.newHashSet();

        ReportableVariant variantSomatic =
                TestReportableVariantFactory.builder().gene("BRCA2").chromosome("1").position(56412).ref("A").alt("C").build();
        Set<ReportableVariant> reportableSomaticVariants = Sets.newHashSet(variantSomatic);

        Set<PurpleGainLoss> reportableSomaticGainsLosses = Sets.newHashSet();
        Set<LinxFusion> reportableFusions = Sets.newHashSet();
        Set<LinxHomozygousDisruption> homozygousDisruptions = Sets.newHashSet();
        Set<LinxBreakend> reportableBreakends = Sets.newHashSet();
        List<DriverGene> driverGenes = createDriverList(Lists.newArrayList("BRCA2"));

        List<WildTypeGene> wildTypes = WildTypeFactory.determineWildTypeGenes(reportableGermlineVariants,
                reportableSomaticVariants,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                reportableBreakends,
                driverGenes);
        assertEquals(0, wildTypes.size());
    }

    @Test
    public void canDetermineWildTypeGermline() {
        ReportableVariant variantGermline =
                TestReportableVariantFactory.builder().gene("BRCA1").chromosome("1").position(56412).ref("A").alt("C").build();
        Set<ReportableVariant> reportableGermlineVariants = Sets.newHashSet(variantGermline);

        Set<ReportableVariant> reportableSomaticVariants = Sets.newHashSet();
        Set<PurpleGainLoss> reportableSomaticGainsLosses = Sets.newHashSet();
        Set<LinxFusion> reportableFusions = Sets.newHashSet();
        Set<LinxHomozygousDisruption> homozygousDisruptions = Sets.newHashSet();
        Set<LinxBreakend> reportableBreakends = Sets.newHashSet();

        List<DriverGene> driverGenes = createDriverList(Lists.newArrayList("BRCA1"));

        List<WildTypeGene> wildTypes = WildTypeFactory.determineWildTypeGenes(reportableGermlineVariants,
                reportableSomaticVariants,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                reportableBreakends,
                driverGenes);
        assertEquals(0, wildTypes.size());
    }

    @Test
    public void canDetermineWildTypeCNV() {
        Set<ReportableVariant> reportableGermlineVariants = Sets.newHashSet();
        Set<ReportableVariant> reportableSomaticVariants = Sets.newHashSet();
        PurpleGainLoss reportableAmp =
                TestPurpleFactory.gainLossBuilder().gene("KRAS").interpretation(PurpleGainLossInterpretation.FULL_GAIN).build();
        PurpleGainLoss reportableDel =
                TestPurpleFactory.gainLossBuilder().gene("APC").interpretation(PurpleGainLossInterpretation.FULL_LOSS).build();
        Set<PurpleGainLoss> reportableSomaticGainsLosses = Sets.newHashSet(reportableAmp, reportableDel);
        Set<LinxFusion> reportableFusions = Sets.newHashSet();
        Set<LinxHomozygousDisruption> homozygousDisruptions = Sets.newHashSet();
        Set<LinxBreakend> reportableBreakends = Sets.newHashSet();

        List<DriverGene> driverGenes = createDriverList(Lists.newArrayList("APC", "KRAS"));

        List<WildTypeGene> wildTypes = WildTypeFactory.determineWildTypeGenes(reportableGermlineVariants,
                reportableSomaticVariants,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                reportableBreakends,
                driverGenes);
        assertEquals(0, wildTypes.size());
    }

    @Test
    public void canDetermineWildTypeFusion5prime() {
        Set<ReportableVariant> reportableGermlineVariants = Sets.newHashSet();
        Set<ReportableVariant> reportableSomaticVariants = Sets.newHashSet();
        Set<PurpleGainLoss> reportableSomaticGainsLosses = Sets.newHashSet();
        LinxFusion reportedFusionMatch = createFusion("BAG4", "EGFR");
        Set<LinxFusion> reportableFusions = Sets.newHashSet(reportedFusionMatch);
        Set<LinxHomozygousDisruption> homozygousDisruptions = Sets.newHashSet();
        Set<LinxBreakend> reportableBreakends = Sets.newHashSet();

        List<DriverGene> driverGenes = createDriverList(Lists.newArrayList("BAG4"));

        List<WildTypeGene> wildTypes = WildTypeFactory.determineWildTypeGenes(reportableGermlineVariants,
                reportableSomaticVariants,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                reportableBreakends,
                driverGenes);
        assertEquals(0, wildTypes.size());
    }

    @Test
    public void canDetermineWildTypeFusion3prime() {
        Set<ReportableVariant> reportableGermlineVariants = Sets.newHashSet();
        Set<ReportableVariant> reportableSomaticVariants = Sets.newHashSet();
        Set<PurpleGainLoss> reportableSomaticGainsLosses = Sets.newHashSet();
        LinxFusion reportedFusionMatch = createFusion("EGFR", "BAG4");
        Set<LinxFusion> reportableFusions = Sets.newHashSet(reportedFusionMatch);
        Set<LinxHomozygousDisruption> homozygousDisruptions = Sets.newHashSet();
        Set<LinxBreakend> reportableBreakends = Sets.newHashSet();

        List<DriverGene> driverGenes = createDriverList(Lists.newArrayList("BAG4"));

        List<WildTypeGene> wildTypes = WildTypeFactory.determineWildTypeGenes(reportableGermlineVariants,
                reportableSomaticVariants,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                reportableBreakends,
                driverGenes);
        assertEquals(0, wildTypes.size());
    }

    @Test
    public void canDetermineWildTypeHomozygousDisruption() {
        Set<ReportableVariant> reportableGermlineVariants = Sets.newHashSet();
        Set<ReportableVariant> reportableSomaticVariants = Sets.newHashSet();
        Set<PurpleGainLoss> reportableSomaticGainsLosses = Sets.newHashSet();
        Set<LinxFusion> reportableFusions = Sets.newHashSet();
        LinxHomozygousDisruption homozygousDisruption = createHomozygousDisruption("NRAS");
        Set<LinxHomozygousDisruption> homozygousDisruptions = Sets.newHashSet(homozygousDisruption);
        Set<LinxBreakend> reportableBreakends = Sets.newHashSet();

        List<DriverGene> driverGenes = createDriverList(Lists.newArrayList("NRAS"));

        List<WildTypeGene> wildTypes = WildTypeFactory.determineWildTypeGenes(reportableGermlineVariants,
                reportableSomaticVariants,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                reportableBreakends,
                driverGenes);
        assertEquals(0, wildTypes.size());
    }

    @Test
    public void canDetermineWildTypeBreakend() {
        Set<ReportableVariant> reportableGermlineVariants = Sets.newHashSet();
        Set<ReportableVariant> reportableSomaticVariants = Sets.newHashSet();
        Set<PurpleGainLoss> reportableSomaticGainsLosses = Sets.newHashSet();
        Set<LinxFusion> reportableFusions = Sets.newHashSet();
        Set<LinxHomozygousDisruption> homozygousDisruptions = Sets.newHashSet();

        LinxBreakend breakend = createBreakend("MYC");
        Set<LinxBreakend> reportableBreakends = Sets.newHashSet(breakend);

        List<DriverGene> driverGenes = createDriverList(Lists.newArrayList("MYC"));

        List<WildTypeGene> wildTypes = WildTypeFactory.determineWildTypeGenes(reportableGermlineVariants,
                reportableSomaticVariants,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                reportableBreakends,
                driverGenes);
        assertEquals(0, wildTypes.size());
    }

    @Test
    public void canDetermineWildType() {
        ReportableVariant variantGermline =
                TestReportableVariantFactory.builder().gene("BRCA1").chromosome("1").position(56412).ref("A").alt("C").build();
        Set<ReportableVariant> reportableGermlineVariants = Sets.newHashSet(variantGermline);

        ReportableVariant variantSomatic =
                TestReportableVariantFactory.builder().gene("BRCA2").chromosome("1").position(56412).ref("A").alt("C").build();
        Set<ReportableVariant> reportableSomaticVariants = Sets.newHashSet(variantSomatic);

        PurpleGainLoss reportableAmp =
                TestPurpleFactory.gainLossBuilder().gene("KRAS").interpretation(PurpleGainLossInterpretation.FULL_GAIN).build();
        PurpleGainLoss reportableDel =
                TestPurpleFactory.gainLossBuilder().gene("APC").interpretation(PurpleGainLossInterpretation.FULL_LOSS).build();
        Set<PurpleGainLoss> reportableSomaticGainsLosses = Sets.newHashSet(reportableAmp, reportableDel);

        LinxFusion reportedFusionMatch = createFusion("BAG4", "FGFR1");
        Set<LinxFusion> reportableFusions = Sets.newHashSet(reportedFusionMatch);

        LinxHomozygousDisruption homozygousDisruption = createHomozygousDisruption("NRAS");
        Set<LinxHomozygousDisruption> homozygousDisruptions = Sets.newHashSet(homozygousDisruption);

        LinxBreakend breakend = createBreakend("MYC");
        Set<LinxBreakend> reportableBreakends = Sets.newHashSet(breakend);

        List<DriverGene> driverGenes =
                createDriverList(Lists.newArrayList("BRCA1", "BRCA2", "APC", "KRAS", "BAG4", "FGFR1", "NRAS", "EGFR", "MYC"));

        List<WildTypeGene> wildTypes = WildTypeFactory.determineWildTypeGenes(reportableGermlineVariants,
                reportableSomaticVariants,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                reportableBreakends,
                driverGenes);
        assertEquals(1, wildTypes.size());
    }

    @Test
    public void canFilterWildType() {
        ReportableVariant variantGermline =
                TestReportableVariantFactory.builder().gene("BRCA1").chromosome("1").position(56412).ref("A").alt("C").build();
        Set<ReportableVariant> reportableGermlineVariants = Sets.newHashSet(variantGermline);

        ReportableVariant variantSomatic =
                TestReportableVariantFactory.builder().gene("BRCA2").chromosome("1").position(56412).ref("A").alt("C").build();
        Set<ReportableVariant> reportableSomaticVariants = Sets.newHashSet(variantSomatic);

        PurpleGainLoss reportableAmp =
                TestPurpleFactory.gainLossBuilder().gene("KRAS").interpretation(PurpleGainLossInterpretation.FULL_GAIN).build();
        PurpleGainLoss reportableDel =
                TestPurpleFactory.gainLossBuilder().gene("APC").interpretation(PurpleGainLossInterpretation.FULL_LOSS).build();
        Set<PurpleGainLoss> reportableSomaticGainsLosses = Sets.newHashSet(reportableAmp, reportableDel);

        LinxFusion reportedFusionMatch = createFusion("BAG4", "FGFR1");
        Set<LinxFusion> reportableFusions = Sets.newHashSet(reportedFusionMatch);

        LinxHomozygousDisruption homozygousDisruption = createHomozygousDisruption("NRAS");
        Set<LinxHomozygousDisruption> homozygousDisruptions = Sets.newHashSet(homozygousDisruption);

        LinxBreakend breakend = createBreakend("MYC");
        Set<LinxBreakend> reportableBreakends = Sets.newHashSet(breakend);

        List<DriverGene> driverGenes =
                createDriverList(Lists.newArrayList("BRCA1", "BRCA2", "APC", "KRAS", "BAG4", "FGFR1", "NRAS", "EGFR", "MYC"));

        List<WildTypeGene> wildTypes = WildTypeFactory.determineWildTypeGenes(reportableGermlineVariants,
                reportableSomaticVariants,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                reportableBreakends,
                driverGenes);

        Set<PurpleQCStatus> purpleQCStatusSetPASS = Sets.newHashSet();
        purpleQCStatusSetPASS.add(PurpleQCStatus.PASS);
        assertEquals(1, WildTypeFactory.filterQCWildTypes(purpleQCStatusSetPASS, wildTypes).size());

        Set<PurpleQCStatus> purpleQCStatusSetWarnDeleted = Sets.newHashSet();
        purpleQCStatusSetWarnDeleted.add(PurpleQCStatus.WARN_DELETED_GENES);
        assertEquals(1, WildTypeFactory.filterQCWildTypes(purpleQCStatusSetWarnDeleted, wildTypes).size());

        Set<PurpleQCStatus> purpleQCStatusSetFailPurity = Sets.newHashSet();
        purpleQCStatusSetFailPurity.add(PurpleQCStatus.FAIL_NO_TUMOR);
        assertEquals(0, WildTypeFactory.filterQCWildTypes(purpleQCStatusSetFailPurity, wildTypes).size());

        Set<PurpleQCStatus> purpleQCStatusSetWarnPurity = Sets.newHashSet();
        purpleQCStatusSetWarnPurity.add(PurpleQCStatus.WARN_LOW_PURITY);
        assertEquals(0, WildTypeFactory.filterQCWildTypes(purpleQCStatusSetWarnPurity, wildTypes).size());
    }

    @NotNull
    private static List<DriverGene> createDriverList(@NotNull List<String> genes) {
        List<DriverGene> driverGeneList = Lists.newArrayList();
        for (String gene : genes) {
            driverGeneList.add(TestDriverGeneFactory.builder().gene(gene).build());
        }
        return driverGeneList;
    }

    @NotNull
    private static LinxHomozygousDisruption createHomozygousDisruption(@NotNull String gene) {
        return TestLinxFactory.homozygousDisruptionBuilder().gene(gene).build();
    }

    @NotNull
    private static LinxBreakend createBreakend(@NotNull String gene) {
        return TestLinxFactory.breakendBuilder().gene(gene).build();
    }

    @NotNull
    private static LinxFusion createFusion(@NotNull String geneStart, @NotNull String geneEnd) {
        return TestLinxFactory.fusionBuilder().geneStart(geneStart).geneEnd(geneEnd).reported(true).build();
    }
}