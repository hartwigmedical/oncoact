package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.linx.ImmutableLinxFusion;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.linx.LinxFusionType;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;
import com.hartwig.oncoact.protect.EventGenerator;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class FusionEvidenceTest {

    @Test
    public void canDetermineFusionEvidence() {
        ActionableGene firstPromiscuous3 = TestServeFactory.geneBuilder()
                .gene("EGFR")
                .event(GeneEvent.FUSION)
                .intervention(ImmutableTreatment.builder().name("treatment 1").build())
                .build();
        ActionableGene secondPromiscuous3 = TestServeFactory.geneBuilder()
                .gene("TP53")
                .event(GeneEvent.FUSION)
                .intervention(ImmutableTreatment.builder().name("treatment 2").build())
                .build();
        ActionableGene thirdPromiscuous3 = TestServeFactory.geneBuilder()
                .gene("PTEN")
                .event(GeneEvent.FUSION)
                .intervention(ImmutableTreatment.builder().name("treatment 3").build())
                .build();
        ActionableGene fourthPromiscuous3 = TestServeFactory.geneBuilder()
                .gene("BRAF")
                .event(GeneEvent.FUSION)
                .intervention(ImmutableTreatment.builder().name("treatment 4").build())
                .build();
        ActionableGene amp = TestServeFactory.geneBuilder()
                .gene("KRAS")
                .event(GeneEvent.AMPLIFICATION)
                .intervention(ImmutableTreatment.builder().name("treatment 5").build())
                .build();
        ActionableFusion fusion = TestServeFactory.fusionBuilder()
                .geneUp("EML4")
                .geneDown("ALK")
                .intervention(ImmutableTreatment.builder().name("treatment 6").build())
                .build();
        ActionableGene other = TestServeFactory.geneBuilder()
                .gene("AB")
                .event(GeneEvent.FUSION)
                .intervention(ImmutableTreatment.builder().name("treatment 7").build())
                .build();
        ActionableFusion igPair = TestServeFactory.fusionBuilder()
                .geneUp("IGH")
                .geneDown("BCL2")
                .intervention(ImmutableTreatment.builder().name("treatment 8").build())
                .build();
        ActionableGene igFusion = TestServeFactory.geneBuilder()
                .gene("IGH")
                .event(GeneEvent.FUSION)
                .intervention(ImmutableTreatment.builder().name("treatment 9").build())
                .build();
        ActionableGene activation = TestServeFactory.geneBuilder()
                .gene("EGFR")
                .event(GeneEvent.ACTIVATION)
                .intervention(ImmutableTreatment.builder().name("treatment 10").build())
                .build();
        ActionableGene anyMutation = TestServeFactory.geneBuilder()
                .gene("EGFR")
                .event(GeneEvent.ANY_MUTATION)
                .intervention(ImmutableTreatment.builder().name("treatment 11").build())
                .build();

        FusionEvidence fusionEvidence = new FusionEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(activation,
                        anyMutation,
                        firstPromiscuous3,
                        secondPromiscuous3,
                        thirdPromiscuous3,
                        fourthPromiscuous3,
                        amp,
                        other,
                        igFusion),
                Lists.newArrayList(fusion, igPair));

        LinxFusion reportedFusionMatch = create("EML4", "ALK", true, LinxFusionType.KNOWN_PAIR);
        LinxFusion reportedFusionUnMatch = create("NRG1", "NRG1", true, LinxFusionType.EXON_DEL_DUP);
        LinxFusion reportedPromiscuousMatch5 = create("BRAF", "other gene", true, LinxFusionType.PROMISCUOUS_5);
        LinxFusion reportedPromiscuousMatch3 = create("other gene", "EGFR", true, LinxFusionType.PROMISCUOUS_3);
        LinxFusion reportedPromiscuousUnMatch3 = create("other gene", "KRAS", true, LinxFusionType.PROMISCUOUS_3);
        LinxFusion reportedPromiscuousNonMatch = create("other gene", "PIK3CA", true, LinxFusionType.PROMISCUOUS_3);
        LinxFusion unreportedPromiscuousMatch = create("other gene", "PTEN", false, LinxFusionType.PROMISCUOUS_3);
        LinxFusion reportedPromiscuousMatch = create("other gene", "CDK4", true, LinxFusionType.PROMISCUOUS_3);
        LinxFusion reportedOtherMatch = create("other gene", "AB", false, LinxFusionType.NONE);
        LinxFusion reportedIgPromiscuous = create("IGH", "other gene", false, LinxFusionType.IG_PROMISCUOUS);
        LinxFusion reportedIgKnown = create("IGH", "BCL2", false, LinxFusionType.IG_KNOWN_PAIR);

        Set<LinxFusion> reportableFusions = Sets.newHashSet(reportedFusionMatch,
                reportedFusionUnMatch,
                reportedPromiscuousMatch5,
                reportedPromiscuousMatch3,
                reportedPromiscuousUnMatch3,
                reportedPromiscuousNonMatch,
                reportedPromiscuousMatch,
                reportedOtherMatch,
                reportedIgPromiscuous,
                reportedIgKnown);
        Set<LinxFusion> allFusions = Sets.newHashSet(unreportedPromiscuousMatch);
        List<ProtectEvidence> evidences = fusionEvidence.evidence(reportableFusions, allFusions, null);

        assertEquals(10, evidences.size());

        ProtectEvidence evidence1 = findByFusion(evidences, reportedFusionMatch, "treatment 6");
        assertTrue(evidence1.reported());
        assertEquals(evidence1.sources().size(), 1);
        assertEquals(EvidenceType.FUSION_PAIR, evidence1.sources().iterator().next().evidenceType());

        ProtectEvidence evidence2 = findByFusion(evidences, reportedPromiscuousMatch5, "treatment 4");
        assertTrue(evidence2.reported());
        assertEquals(evidence2.sources().size(), 1);
        assertEquals(EvidenceType.PROMISCUOUS_FUSION, evidence2.sources().iterator().next().evidenceType());

        ProtectEvidence evidence3 = findByFusion(evidences, reportedPromiscuousMatch3, "treatment 1");
        assertTrue(evidence3.reported());
        assertEquals(evidence3.sources().size(), 1);
        assertEquals(EvidenceType.PROMISCUOUS_FUSION, evidence3.sources().iterator().next().evidenceType());

        ProtectEvidence evidence4 = findByFusion(evidences, reportedOtherMatch, "treatment 7");
        assertFalse(evidence4.reported());
        assertEquals(evidence4.sources().size(), 1);
        assertEquals(EvidenceType.PROMISCUOUS_FUSION, evidence4.sources().iterator().next().evidenceType());

        ProtectEvidence evidence5 = findByFusion(evidences, unreportedPromiscuousMatch, "treatment 3");
        assertFalse(evidence5.reported());
        assertEquals(evidence5.sources().size(), 1);
        assertEquals(EvidenceType.PROMISCUOUS_FUSION, evidence5.sources().iterator().next().evidenceType());

        ProtectEvidence evidence6 = findByFusion(evidences, reportedIgPromiscuous, "treatment 9");
        assertFalse(evidence6.reported());
        assertEquals(evidence6.sources().size(), 1);
        assertEquals(EvidenceType.PROMISCUOUS_FUSION, evidence6.sources().iterator().next().evidenceType());

        ProtectEvidence evidence7 = findByFusion(evidences, reportedIgKnown, "treatment 9");
        assertFalse(evidence7.reported());
        assertEquals(evidence7.sources().size(), 1);
        assertEquals(EvidenceType.PROMISCUOUS_FUSION, evidence7.sources().iterator().next().evidenceType());

        ProtectEvidence evidence8 = findByFusion(evidences, reportedIgKnown, "treatment 8");
        assertFalse(evidence8.reported());
        assertEquals(evidence8.sources().size(), 1);
        assertEquals(EvidenceType.FUSION_PAIR, evidence8.sources().iterator().next().evidenceType());

        ProtectEvidence evidence9 = findByFusion(evidences, reportedPromiscuousMatch3, "treatment 10");
        assertFalse(evidence9.reported());
        assertEquals(evidence9.sources().size(), 1);
        assertEquals(EvidenceType.ACTIVATION, evidence9.sources().iterator().next().evidenceType());

        ProtectEvidence evidence10 = findByFusion(evidences, reportedPromiscuousMatch3, "treatment 11");
        assertFalse(evidence10.reported());
        assertEquals(evidence10.sources().size(), 1);
        assertEquals(EvidenceType.ANY_MUTATION, evidence10.sources().iterator().next().evidenceType());
    }

    @NotNull
    private static ProtectEvidence findByFusion(@NotNull List<ProtectEvidence> evidences, @NotNull LinxFusion fusion,
            @NotNull String treatment) {
        return evidences.stream()
                .filter(x -> Objects.equals(x.event(), EventGenerator.fusionEvent(fusion)) && Objects.equals(x.treatment().name(),
                        treatment))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot find evidence with fusion event: " + EventGenerator.fusionEvent(fusion)));
    }

    @Test
    public void canCorrectlyFilterOnExonRange() {
        int minExonUp = 5;
        int maxExonUp = 7;
        int minExonDown = 2;
        int maxExonDown = 4;

        ActionableFusion fusion = TestServeFactory.fusionBuilder()
                .geneUp("EML4")
                .minExonUp(minExonUp)
                .maxExonUp(maxExonUp)
                .geneDown("ALK")
                .minExonDown(minExonDown)
                .maxExonDown(maxExonDown)
                .build();

        FusionEvidence fusionEvidence =
                new FusionEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(), Lists.newArrayList(fusion));

        ImmutableLinxFusion.Builder builder = linxFusionBuilder("EML4", "ALK", true, LinxFusionType.KNOWN_PAIR);

        Set<LinxFusion> onMinRange = Sets.newHashSet(builder.fusedExonUp(minExonUp).fusedExonDown(minExonDown).build());
        assertEquals(1, fusionEvidence.evidence(onMinRange, Sets.newHashSet(), null).size());

        Set<LinxFusion> onMaxRange = Sets.newHashSet(builder.fusedExonUp(maxExonUp).fusedExonDown(maxExonDown).build());
        assertEquals(1, fusionEvidence.evidence(onMaxRange, Sets.newHashSet(), null).size());

        Set<LinxFusion> upGeneExonTooLow = Sets.newHashSet(builder.fusedExonUp(minExonUp - 1).fusedExonDown(minExonDown).build());
        assertEquals(0, fusionEvidence.evidence(upGeneExonTooLow, Sets.newHashSet(), null).size());

        Set<LinxFusion> upGeneExonTooHigh = Sets.newHashSet(builder.fusedExonUp(maxExonUp + 1).fusedExonDown(minExonDown).build());
        assertEquals(0, fusionEvidence.evidence(upGeneExonTooHigh, Sets.newHashSet(), null).size());

        Set<LinxFusion> downGeneExonTooLow = Sets.newHashSet(builder.fusedExonUp(minExonUp).fusedExonDown(minExonDown - 1).build());
        assertEquals(0, fusionEvidence.evidence(downGeneExonTooLow, Sets.newHashSet(), null).size());

        Set<LinxFusion> downGeneExonTooHigh = Sets.newHashSet(builder.fusedExonUp(maxExonUp).fusedExonDown(maxExonDown + 1).build());
        assertEquals(0, fusionEvidence.evidence(downGeneExonTooHigh, Sets.newHashSet(), null).size());
    }

    @NotNull
    private static LinxFusion create(@NotNull String geneStart, @NotNull String geneEnd, boolean reported, @NotNull LinxFusionType type) {
        return linxFusionBuilder(geneStart, geneEnd, reported, type).build();
    }

    @NotNull
    private static ImmutableLinxFusion.Builder linxFusionBuilder(@NotNull String geneStart, @NotNull String geneEnd, boolean reported,
            @NotNull LinxFusionType type) {
        return TestLinxFactory.fusionBuilder().geneStart(geneStart).geneEnd(geneEnd).reported(reported).reportedType(type);
    }
}