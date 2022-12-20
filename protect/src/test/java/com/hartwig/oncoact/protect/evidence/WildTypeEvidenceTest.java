package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.oncoact.drivergene.TestDriverGeneFactory;
import com.hartwig.oncoact.orange.datamodel.linx.ImmutableLinxFusion;
import com.hartwig.oncoact.orange.datamodel.linx.LinxBreakend;
import com.hartwig.oncoact.orange.datamodel.linx.LinxFusion;
import com.hartwig.oncoact.orange.datamodel.linx.LinxHomozygousDisruption;
import com.hartwig.oncoact.orange.datamodel.linx.TestLinxFactory;
import com.hartwig.oncoact.orange.datamodel.purple.PurpleGainLoss;
import com.hartwig.oncoact.orange.datamodel.purple.PurpleGainLossInterpretation;
import com.hartwig.oncoact.orange.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.orange.datamodel.purple.TestPurpleFactory;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class WildTypeEvidenceTest {

    @Test
    public void canDetermineWildType() {
        ReportableVariant variantGermline =
                TestReportableVariantFactory.builder().gene("BRCA1").chromosome("1").position(56412).ref("A").alt("C").build();
        Set<ReportableVariant> reportableGermlineVariant = Sets.newHashSet(variantGermline);

        ReportableVariant variantSomatic =
                TestReportableVariantFactory.builder().gene("BRCA2").chromosome("1").position(56412).ref("A").alt("C").build();
        Set<ReportableVariant> reportableSomaticVariant = Sets.newHashSet(variantSomatic);

        PurpleGainLoss reportableAmp =
                TestPurpleFactory.gainLossBuilder().gene("KRAS").interpretation(PurpleGainLossInterpretation.FULL_GAIN).build();
        PurpleGainLoss reportableDel =
                TestPurpleFactory.gainLossBuilder().gene("APC").interpretation(PurpleGainLossInterpretation.FULL_LOSS).build();
        Set<PurpleGainLoss> reportableSomaticGainsLosses = Sets.newHashSet(reportableAmp, reportableDel);

        LinxFusion reportedFusionMatch = createFusion("BAG4", "FGFR1");
        Set<LinxFusion> reportableFusions = Sets.newHashSet(reportedFusionMatch);

        LinxHomozygousDisruption homozygousDisruption = createHomozygousDisruption("NRAS");
        Set<LinxHomozygousDisruption> homozygousDisruptions = Sets.newHashSet(homozygousDisruption);

        LinxBreakend breakend = TestLinxFactory.breakendBuilder().gene("MYC").build();
        Set<LinxBreakend> breakends = Sets.newHashSet(breakend);

        List<DriverGene> driverGenes =
                createDriverList(Lists.newArrayList("BRCA1", "BRCA2", "APC", "KRAS", "BAG4", "FGFR1", "NRAS", "EGFR", "MYC"));

        //Test wild-type with somatic variant
        ActionableGene wildTypeSomaticVariant = TestServeFactory.geneBuilder().gene("BRCA2").event(GeneEvent.WILD_TYPE).build();

        WildTypeEvidence wildTypeEvidenceSomaticVariant =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildTypeSomaticVariant), driverGenes);

        Set<PurpleQCStatus> purpleQCStatusSet = Sets.newHashSet();
        purpleQCStatusSet.add(PurpleQCStatus.PASS);

        List<ProtectEvidence> evidencesWildTypeSomaticVariant = wildTypeEvidenceSomaticVariant.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);

        assertEquals(0, evidencesWildTypeSomaticVariant.size());

        //Test wild-type with germline variant
        ActionableGene wildTypeGermlineVariant = TestServeFactory.geneBuilder().gene("BRCA1").event(GeneEvent.WILD_TYPE).build();

        WildTypeEvidence wildTypeEvidenceGermlineVariant =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildTypeGermlineVariant), driverGenes);

        List<ProtectEvidence> evidencesWildTypeGermlineVariant = wildTypeEvidenceGermlineVariant.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(0, evidencesWildTypeGermlineVariant.size());

        //Test wild-type with CNV
        ActionableGene wildTypeCNV = TestServeFactory.geneBuilder().gene("APC").event(GeneEvent.WILD_TYPE).build();

        WildTypeEvidence wildTypeEvidenceCNV =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildTypeCNV), driverGenes);

        List<ProtectEvidence> evidencesWildTypeCNV = wildTypeEvidenceCNV.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(0, evidencesWildTypeCNV.size());

        //Test wild-type with fusion  5 prime
        ActionableGene wildTypeFusion5 = TestServeFactory.geneBuilder().gene("BAG4").event(GeneEvent.WILD_TYPE).build();

        WildTypeEvidence wildTypeEvidenceFusion5 =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildTypeFusion5), driverGenes);

        List<ProtectEvidence> evidencesWildTypeFusion5 = wildTypeEvidenceFusion5.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(0, evidencesWildTypeFusion5.size());

        //Test wild-type with fusion  3 prime
        ActionableGene wildTypeFusion3 = TestServeFactory.geneBuilder().gene("BAG4").event(GeneEvent.WILD_TYPE).build();

        WildTypeEvidence wildTypeEvidenceFusion3 =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildTypeFusion3), driverGenes);

        List<ProtectEvidence> evidencesWildTypeFusion3 = wildTypeEvidenceFusion3.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(0, evidencesWildTypeFusion3.size());

        //Test wild-type with homozygous disruption
        ActionableGene wildTypeHomozygousDisruption = TestServeFactory.geneBuilder().gene("NRAS").event(GeneEvent.WILD_TYPE).build();

        WildTypeEvidence wildTypeEvidenceHomozygousDisruption = new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(wildTypeHomozygousDisruption),
                driverGenes);

        List<ProtectEvidence> evidencesWildTypeHomozygousDisruption = wildTypeEvidenceHomozygousDisruption.evidence(
                reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(0, evidencesWildTypeHomozygousDisruption.size());

        //Test wild-type with gene disruption
        ActionableGene wildTypeGeneDisruption = TestServeFactory.geneBuilder().gene("MYC").event(GeneEvent.WILD_TYPE).build();

        WildTypeEvidence wildTypeEvidenceGeneDisruption =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildTypeGeneDisruption), driverGenes);

        List<ProtectEvidence> evidencesWildTypeGeneDisruption = wildTypeEvidenceGeneDisruption.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(0, evidencesWildTypeGeneDisruption.size());

        //Test calling wild type
        ActionableGene wildType = TestServeFactory.geneBuilder().gene("EGFR").event(GeneEvent.WILD_TYPE).build();

        WildTypeEvidence wildTypeEvidence =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildType), driverGenes);

        List<ProtectEvidence> evidencesWildType = wildTypeEvidence.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(1, evidencesWildType.size());
    }

    @NotNull
    private static List<DriverGene> createDriverList(@NotNull List<String> genes) {
        List<DriverGene> driverGeneList = Lists.newArrayList();
        for (String gene : genes) {
            driverGeneList.add(createDriverGene(gene));
        }
        return driverGeneList;
    }

    @NotNull
    private static DriverGene createDriverGene(@NotNull String gene) {
        return TestDriverGeneFactory.builder().gene(gene).build();
    }

    @NotNull
    private static LinxHomozygousDisruption createHomozygousDisruption(@NotNull String gene) {
        return TestLinxFactory.homozygousDisruptionBuilder().gene(gene).build();
    }

    @NotNull
    private static LinxFusion createFusion(@NotNull String geneStart, @NotNull String geneEnd) {
        return linxFusionBuilder(geneStart, geneEnd).build();
    }

    @NotNull
    private static ImmutableLinxFusion.Builder linxFusionBuilder(@NotNull String geneStart, @NotNull String geneEnd) {
        return TestLinxFactory.fusionBuilder().geneStart(geneStart).geneEnd(geneEnd).reported(true);
    }
}