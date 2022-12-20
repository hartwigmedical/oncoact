package com.hartwig.oncoact.protect.evidence;

import static com.hartwig.oncoact.common.drivercatalog.DriverCategory.TSG;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.common.drivercatalog.panel.DriverGene;
import com.hartwig.oncoact.common.drivercatalog.panel.DriverGeneGermlineReporting;
import com.hartwig.oncoact.common.drivercatalog.panel.ImmutableDriverGene;
import com.hartwig.oncoact.datamodel.ReportableVariant;
import com.hartwig.oncoact.datamodel.TestReportableVariantFactory;
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
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;

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

        List<DriverGene> listDriverGenes =
                createDriverMap(Lists.newArrayList("BRCA1", "BRCA2", "APC", "KRAS", "BAG4", "FGFR1", "NRAS", "EGFR", "MYC"));

        //Test wild-type with somatic variant
        ActionableGene wildTypeSomaticVariant = ImmutableActionableGene.builder()
                .from(TestServeFactory.createTestActionableGene())
                .gene("BRCA2")
                .event(GeneEvent.WILD_TYPE)
                .source(Knowledgebase.CKB)
                .build();

        WildTypeEvidence wildTypeEvidenceSomaticVariant =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildTypeSomaticVariant), listDriverGenes);

        Set<PurpleQCStatus> purpleQCStatusSet = Sets.newHashSet();
        purpleQCStatusSet.add(PurpleQCStatus.PASS);

        List<ProtectEvidence> evidencesWildTypeSomaticVariant = wildTypeEvidenceSomaticVariant.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);

        assertEquals(evidencesWildTypeSomaticVariant.size(), 0);

        //Test wild-type with germline variant
        ActionableGene wildTypeGermlineVariant = ImmutableActionableGene.builder()
                .from(TestServeFactory.createTestActionableGene())
                .gene("BRCA1")
                .event(GeneEvent.WILD_TYPE)
                .source(Knowledgebase.CKB)
                .build();

        WildTypeEvidence wildTypeEvidenceGermlineVariant = new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(wildTypeGermlineVariant),
                listDriverGenes);

        List<ProtectEvidence> evidencesWildTypeGermlineVariant = wildTypeEvidenceGermlineVariant.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(evidencesWildTypeGermlineVariant.size(), 0);

        //Test wild-type with CNV
        ActionableGene wildTypeCNV = ImmutableActionableGene.builder()
                .from(TestServeFactory.createTestActionableGene())
                .gene("APC")
                .event(GeneEvent.WILD_TYPE)
                .source(Knowledgebase.CKB)
                .build();

        WildTypeEvidence wildTypeEvidenceCNV =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildTypeCNV), listDriverGenes);

        List<ProtectEvidence> evidencesWildTypeCNV = wildTypeEvidenceCNV.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(evidencesWildTypeCNV.size(), 0);

        //Test wild-type with fusion  5 prime
        ActionableGene wildTypeFusion5 = ImmutableActionableGene.builder()
                .from(TestServeFactory.createTestActionableGene())
                .gene("BAG4")
                .event(GeneEvent.WILD_TYPE)
                .source(Knowledgebase.CKB)
                .build();

        WildTypeEvidence wildTypeEvidenceFusion5 =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildTypeFusion5), listDriverGenes);

        List<ProtectEvidence> evidencesWildTypeFusion5 = wildTypeEvidenceFusion5.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(evidencesWildTypeFusion5.size(), 0);

        //Test wild-type with fusion  3 prime
        ActionableGene wildTypeFusion3 = ImmutableActionableGene.builder()
                .from(TestServeFactory.createTestActionableGene())
                .gene("BAG4")
                .event(GeneEvent.WILD_TYPE)
                .source(Knowledgebase.CKB)
                .build();

        WildTypeEvidence wildTypeEvidenceFusion3 =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildTypeFusion3), listDriverGenes);

        List<ProtectEvidence> evidencesWildTypeFusion3 = wildTypeEvidenceFusion3.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(evidencesWildTypeFusion3.size(), 0);

        //Test wild-type with homozygous disruption
        ActionableGene wildTypeHomozygousDisruption = ImmutableActionableGene.builder()
                .from(TestServeFactory.createTestActionableGene())
                .gene("NRAS")
                .event(GeneEvent.WILD_TYPE)
                .source(Knowledgebase.CKB)
                .build();

        WildTypeEvidence wildTypeEvidenceHomozygousDisruption = new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(wildTypeHomozygousDisruption),
                listDriverGenes);

        List<ProtectEvidence> evidencesWildTypeHomozygousDisruption = wildTypeEvidenceHomozygousDisruption.evidence(
                reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(evidencesWildTypeHomozygousDisruption.size(), 0);

        //Test wild-type with gene disruption
        ActionableGene wildTypeGeneDisruption = ImmutableActionableGene.builder()
                .from(TestServeFactory.createTestActionableGene())
                .gene("MYC")
                .event(GeneEvent.WILD_TYPE)
                .source(Knowledgebase.CKB)
                .build();

        WildTypeEvidence wildTypeEvidenceGeneDisruption =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildTypeGeneDisruption), listDriverGenes);

        List<ProtectEvidence> evidencesWildTypeGeneDisruption = wildTypeEvidenceGeneDisruption.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(evidencesWildTypeGeneDisruption.size(), 0);

        //Test calling wild type
        ActionableGene wildType = ImmutableActionableGene.builder()
                .from(TestServeFactory.createTestActionableGene())
                .gene("EGFR")
                .event(GeneEvent.WILD_TYPE)
                .source(Knowledgebase.CKB)
                .build();

        WildTypeEvidence wildTypeEvidence =
                new WildTypeEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(wildType), listDriverGenes);

        List<ProtectEvidence> evidencesWildType = wildTypeEvidence.evidence(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                breakends,
                purpleQCStatusSet);
        assertEquals(evidencesWildType.size(), 1);
    }

    @NotNull
    private static List<DriverGene> createDriverMap(@NotNull List<String> genes) {
        List<DriverGene> driverGeneList = Lists.newArrayList();
        for (String gene : genes) {
            driverGeneList.add(createDriverGene(gene));
        }
        return driverGeneList;
    }

    @NotNull
    private static DriverGene createDriverGene(@NotNull String gene) {
        return ImmutableDriverGene.builder()
                .gene(gene)
                .reportMissenseAndInframe(false)
                .reportNonsenseAndFrameshift(false)
                .reportSplice(false)
                .reportDeletion(false)
                .reportDisruption(true)
                .reportAmplification(false)
                .reportSomaticHotspot(false)
                .reportGermlineVariant(DriverGeneGermlineReporting.NONE)
                .reportGermlineHotspot(DriverGeneGermlineReporting.NONE)
                .likelihoodType(TSG)
                .reportGermlineDisruption(true)
                .reportPGX(false)
                .build();
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