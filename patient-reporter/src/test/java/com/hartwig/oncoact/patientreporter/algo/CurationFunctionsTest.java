package com.hartwig.oncoact.patientreporter.algo;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.oncoact.disruption.GeneDisruption;
import com.hartwig.oncoact.disruption.TestGeneDisruptionFactory;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ImmutableKnowledgebaseSource;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestProtectFactory;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantSource;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CurationFunctionsTest {

    private static final String GENE_CDKN2A_CANONICAL = "CDKN2A (p16)";
    private static final String GENE_CDKN2A_NON_CANONICAL = "CDKN2A (p14ARF)";

    @Test
    public void canCurateTumorSpecificEvidence() {
        List<ProtectEvidence> tumorSpecificEvidence = createTestEvidences();
        List<ProtectEvidence> curated = CurationFunctions.curateEvidence(tumorSpecificEvidence);

        assertEquals(curated.size(), 3);
        assertEquals(findByGeneProtect(curated, "KRAS", false), "KRAS");
        assertEquals(findByGeneProtect(curated, GENE_CDKN2A_CANONICAL, true), GENE_CDKN2A_CANONICAL);
        assertEquals(findByGeneProtect(curated, GENE_CDKN2A_NON_CANONICAL, false), GENE_CDKN2A_NON_CANONICAL);
    }

    @Test
    public void canCurateClinicalTrials() {
        List<ProtectEvidence> clinicalTrials = createTestEvidences();
        List<ProtectEvidence> curated = CurationFunctions.curateEvidence(clinicalTrials);

        assertEquals(curated.size(), 3);
        assertEquals(findByGeneProtect(curated, "KRAS", false), "KRAS");
        assertEquals(findByGeneProtect(curated, GENE_CDKN2A_CANONICAL, true), GENE_CDKN2A_CANONICAL);
        assertEquals(findByGeneProtect(curated, GENE_CDKN2A_NON_CANONICAL, false), GENE_CDKN2A_NON_CANONICAL);
    }

    @Test
    public void canCurateOffLabelEvidence() {
        List<ProtectEvidence> offLabelEvidence = createTestEvidences();
        List<ProtectEvidence> curated = CurationFunctions.curateEvidence(offLabelEvidence);

        assertEquals(curated.size(), 3);
        assertEquals(findByGeneProtect(curated, "KRAS", false), "KRAS");
        assertEquals(findByGeneProtect(curated, GENE_CDKN2A_CANONICAL, true), GENE_CDKN2A_CANONICAL);
        assertEquals(findByGeneProtect(curated, GENE_CDKN2A_NON_CANONICAL, false), GENE_CDKN2A_NON_CANONICAL);
    }

    @NotNull
    private static String findByGeneProtect(@NotNull List<ProtectEvidence> evidences, @NotNull String gene, boolean isCanonical) {
        for (ProtectEvidence evidence : evidences) {
            if (evidence.gene().equals(gene) && evidence.isCanonical().equals(isCanonical)) {
                return evidence.gene();
            }
        }
        throw new IllegalStateException("Could not find evidence with gene and canonical: " + gene + " " + isCanonical);
    }

    @Test
    public void canCurateReportableVariants() {
        List<ReportableVariant> variants = createTestVariants();
        List<ReportableVariant> curated = CurationFunctions.curateReportableVariants(variants);

        assertEquals(curated.size(), 3);
        assertEquals(findByGeneVariant(curated, "KRAS", false), "KRAS");
        assertEquals(findByGeneVariant(curated, GENE_CDKN2A_CANONICAL, true), GENE_CDKN2A_CANONICAL);
        assertEquals(findByGeneVariant(curated, GENE_CDKN2A_NON_CANONICAL, false), GENE_CDKN2A_NON_CANONICAL);
    }

    @Test
    public void canCurateNotifyGermlineStatusPerVariant() {
        ReportableVariant somaticVariant1 =
                TestReportableVariantFactory.builder().gene("KRAS").isCanonical(false).source(ReportableVariantSource.SOMATIC).build();
        ReportableVariant somaticVariant2 =
                TestReportableVariantFactory.builder().gene("CDKN2A").isCanonical(true).source(ReportableVariantSource.SOMATIC).build();
        ReportableVariant germlineVariant1 =
                TestReportableVariantFactory.builder().gene("CDKN2A").isCanonical(false).source(ReportableVariantSource.GERMLINE).build();

        Map<ReportableVariant, Boolean> notifyGermlineVariants = Maps.newHashMap();
        notifyGermlineVariants.put(somaticVariant1, false);
        notifyGermlineVariants.put(somaticVariant2, true);
        notifyGermlineVariants.put(germlineVariant1, false);

        Map<ReportableVariant, Boolean> map = CurationFunctions.curateNotifyGermlineStatusPerVariant(notifyGermlineVariants);
        List<ReportableVariant> curated = Lists.newArrayList(map.keySet());

        assertEquals(curated.size(), 3);
        assertEquals(findByGeneVariant(curated, "KRAS", false), "KRAS");
        assertEquals(findByGeneVariant(curated, GENE_CDKN2A_CANONICAL, true), GENE_CDKN2A_CANONICAL);
        assertEquals(findByGeneVariant(curated, GENE_CDKN2A_NON_CANONICAL, false), GENE_CDKN2A_NON_CANONICAL);
    }

    @NotNull
    private static String findByGeneVariant(@NotNull List<ReportableVariant> variants, @NotNull String gene, boolean isCanonical) {
        for (ReportableVariant variant : variants) {
            if (variant.gene().equals(gene) && variant.isCanonical() == isCanonical) {
                return variant.gene();
            }
        }
        throw new IllegalStateException("Could not find gene with canonical: " + gene + " " + isCanonical);
    }

    @Test
    public void canCurateGainsAndLosses() {
        List<PurpleGainLoss> gainsLosses = createTestGainsLosses();
        List<PurpleGainLoss> curated = CurationFunctions.curateGainsAndLosses(gainsLosses);

        assertEquals(curated.size(), 3);
        assertEquals(findByGeneGainLoss(curated, "BRAF", true), "BRAF");
        assertEquals(findByGeneGainLoss(curated, GENE_CDKN2A_CANONICAL, true), GENE_CDKN2A_CANONICAL);
        assertEquals(findByGeneGainLoss(curated, GENE_CDKN2A_NON_CANONICAL, false), GENE_CDKN2A_NON_CANONICAL);
    }

    @NotNull
    private static String findByGeneGainLoss(@NotNull List<PurpleGainLoss> gainsLosses, @NotNull String gene, boolean isCanonical) {
        for (PurpleGainLoss gainLoss : gainsLosses) {
            if (gainLoss.gene().equals(gene) && gainLoss.isCanonical() == isCanonical) {
                return gainLoss.gene();
            }
        }
        throw new IllegalStateException("Could not find gene with canonical: " + gene + " " + isCanonical);
    }

    @Test
    public void canCurateGeneDisruptions() {
        List<GeneDisruption> disruptions = createTestGeneDisruptions();
        List<GeneDisruption> curated = CurationFunctions.curateGeneDisruptions(disruptions);

        assertEquals(curated.size(), 3);
        assertEquals(findByGeneDisruption(curated, "NRAS", true), "NRAS");
        assertEquals(findByGeneDisruption(curated, GENE_CDKN2A_CANONICAL, true), GENE_CDKN2A_CANONICAL);
        assertEquals(findByGeneDisruption(curated, GENE_CDKN2A_NON_CANONICAL, false), GENE_CDKN2A_NON_CANONICAL);
    }

    @NotNull
    private static String findByGeneDisruption(@NotNull List<GeneDisruption> disruptions, @NotNull String gene, boolean isCanonical) {
        for (GeneDisruption disruption : disruptions) {
            if (disruption.gene().equals(gene) && disruption.isCanonical() == isCanonical) {
                return disruption.gene();
            }
        }
        throw new IllegalStateException("Could not find gene with canonical: " + gene + " " + isCanonical);
    }

    @Test
    public void canCurateHomozygousDisruptions() {
        List<HomozygousDisruption> homozygousDisruptions = createTestHomozygousDisruptions();
        List<HomozygousDisruption> curated = CurationFunctions.curateHomozygousDisruptions(homozygousDisruptions);

        assertEquals(curated.size(), 3);
        assertEquals(findByGeneHomozygousDisruption(curated, "NRAS", true), "NRAS");
        assertEquals(findByGeneHomozygousDisruption(curated, GENE_CDKN2A_CANONICAL, true), GENE_CDKN2A_CANONICAL);
        assertEquals(findByGeneHomozygousDisruption(curated, GENE_CDKN2A_NON_CANONICAL, false), GENE_CDKN2A_NON_CANONICAL);
    }

    @NotNull
    private static String findByGeneHomozygousDisruption(@NotNull List<HomozygousDisruption> disruptions, @NotNull String gene,
            boolean isCanonical) {
        for (HomozygousDisruption disruption : disruptions) {
            if (disruption.gene().equals(gene) && disruption.isCanonical() == isCanonical) {
                return disruption.gene();
            }
        }

        throw new IllegalStateException("Could not find gene with canonical: " + gene + " " + isCanonical);
    }

    @NotNull
    private static List<ProtectEvidence> createTestEvidences() {
        ProtectEvidence evidence1 = TestProtectFactory.builder()
                .gene("KRAS")
                .isCanonical(false)
                .event("amp")
                .germline(true)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("TryMe")
                        .treatmentApproachesDrugClass(Sets.newHashSet())
                        .treatmentApproachesTherapy(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Sets.newHashSet(ImmutableKnowledgebaseSource.builder()
                        .name(Knowledgebase.ICLUSION)
                        .sourceEvent(Strings.EMPTY)
                        .sourceUrls(Sets.newHashSet())
                        .evidenceType(EvidenceType.AMPLIFICATION)
                        .build()))
                .build();

        ProtectEvidence evidence2 = TestProtectFactory.builder()
                .gene("CDKN2A")
                .isCanonical(true)
                .event("amp")
                .germline(true)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("TryMe")
                        .treatmentApproachesDrugClass(Sets.newHashSet())
                        .treatmentApproachesTherapy(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Sets.newHashSet(ImmutableKnowledgebaseSource.builder()
                        .name(Knowledgebase.ICLUSION)
                        .sourceEvent(Strings.EMPTY)
                        .sourceUrls(Sets.newHashSet())
                        .evidenceType(EvidenceType.AMPLIFICATION)
                        .build()))
                .build();

        ProtectEvidence evidence3 = TestProtectFactory.builder()
                .gene("CDKN2A")
                .isCanonical(false)
                .event("amp")
                .germline(true)
                .reported(true)
                .treatment(ImmutableTreatment.builder()
                        .name("TryMe")
                        .treatmentApproachesDrugClass(Sets.newHashSet())
                        .treatmentApproachesTherapy(Sets.newHashSet())
                        .build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Sets.newHashSet(ImmutableKnowledgebaseSource.builder()
                        .name(Knowledgebase.ICLUSION)
                        .sourceEvent(Strings.EMPTY)
                        .sourceUrls(Sets.newHashSet())
                        .evidenceType(EvidenceType.AMPLIFICATION)
                        .build()))
                .build();
        return Lists.newArrayList(evidence1, evidence2, evidence3);
    }

    @NotNull
    private static List<ReportableVariant> createTestVariants() {
        ReportableVariant somaticVariant1 =
                TestReportableVariantFactory.builder().gene("KRAS").isCanonical(false).source(ReportableVariantSource.SOMATIC).build();
        ReportableVariant somaticVariant2 =
                TestReportableVariantFactory.builder().gene("CDKN2A").isCanonical(true).source(ReportableVariantSource.SOMATIC).build();
        ReportableVariant germlineVariant1 =
                TestReportableVariantFactory.builder().gene("CDKN2A").isCanonical(false).source(ReportableVariantSource.GERMLINE).build();
        return Lists.newArrayList(somaticVariant1, somaticVariant2, germlineVariant1);
    }

    @NotNull
    private static List<PurpleGainLoss> createTestGainsLosses() {
        PurpleGainLoss gainLoss1 = TestPurpleFactory.gainLossBuilder().gene("BRAF").isCanonical(true).build();
        PurpleGainLoss gainLoss2 = TestPurpleFactory.gainLossBuilder().gene("CDKN2A").isCanonical(true).build();
        PurpleGainLoss gainLoss3 = TestPurpleFactory.gainLossBuilder().gene("CDKN2A").isCanonical(false).build();
        return Lists.newArrayList(gainLoss1, gainLoss2, gainLoss3);
    }

    @NotNull
    private static List<GeneDisruption> createTestGeneDisruptions() {
        GeneDisruption disruption1 = TestGeneDisruptionFactory.builder().gene("NRAS").isCanonical(true).build();
        GeneDisruption disruption2 = TestGeneDisruptionFactory.builder().gene("CDKN2A").isCanonical(true).build();
        GeneDisruption disruption3 = TestGeneDisruptionFactory.builder().gene("CDKN2A").isCanonical(false).build();
        return Lists.newArrayList(disruption1, disruption2, disruption3);
    }

    @NotNull
    private static List<HomozygousDisruption> createTestHomozygousDisruptions() {
        HomozygousDisruption homozygousDisruption1 = TestLinxFactory.homozygousDisruptionBuilder().gene("NRAS").isCanonical(true).build();
        HomozygousDisruption homozygousDisruption2 = TestLinxFactory.homozygousDisruptionBuilder().gene("CDKN2A").isCanonical(true).build();
        HomozygousDisruption homozygousDisruption3 =
                TestLinxFactory.homozygousDisruptionBuilder().gene("CDKN2A").isCanonical(false).build();
        return Lists.newArrayList(homozygousDisruption1, homozygousDisruption2, homozygousDisruption3);
    }
}