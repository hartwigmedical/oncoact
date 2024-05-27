package com.hartwig.oncoact.protect.evidence;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleVariant;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.range.ActionableRange;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.*;

public class VariantEvidenceTest {

    @Test
    public void canDetermineVariantEvidenceForHotspots() {
        String chromosome = "1";
        int position = 10;
        String ref = "C";
        String alt = "T";

        ActionableHotspot hotspot = TestServeFactory.hotspotBuilder().chromosome(chromosome).position(position).ref(ref).alt(alt).build();

        VariantEvidence variantEvidence = new VariantEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(hotspot),
                Lists.newArrayList(),
                Lists.newArrayList(),
                Lists.newArrayList());

        ReportableVariant variantMatch = TestReportableVariantFactory.builder()
                .gene("reportable")
                .chromosome(chromosome)
                .position(position)
                .ref(ref)
                .alt(alt)
                .build();

        ReportableVariant variantWrongPosition = TestReportableVariantFactory.builder()
                .gene("wrong position")
                .chromosome(chromosome)
                .position(position + 1)
                .ref(ref)
                .alt(alt)
                .build();

        ReportableVariant variantWrongAlt = TestReportableVariantFactory.builder()
                .gene("wrong alt")
                .chromosome(chromosome)
                .position(position)
                .ref(ref)
                .alt("G")
                .build();

        PurpleVariant unreportedMatch = TestPurpleFactory.variantBuilder()
                .reported(false)
                .gene("unreported")
                .chromosome(chromosome)
                .position(position)
                .ref(ref)
                .alt(alt)
                .build();

        List<ProtectEvidence> evidences = variantEvidence.evidence(Sets.newHashSet(variantMatch, variantWrongAlt, variantWrongPosition),
                Sets.newHashSet(),
                Sets.newHashSet(unreportedMatch),
                Sets.newHashSet(), null);

        assertEquals(2, evidences.size());

        ProtectEvidence reportedEvidence = findByGene(evidences, "reportable");
        assertTrue(reportedEvidence.reported());
        assertEquals(reportedEvidence.sources().size(), 1);
        assertEquals(EvidenceType.HOTSPOT_MUTATION, reportedEvidence.sources().iterator().next().evidenceType());

        ProtectEvidence unreportedEvidence = findByGene(evidences, "unreported");
        assertFalse(unreportedEvidence.reported());
        assertEquals(unreportedEvidence.sources().size(), 1);
        assertEquals(EvidenceType.HOTSPOT_MUTATION, unreportedEvidence.sources().iterator().next().evidenceType());
    }

    @Test
    public void canDetermineVariantEvidenceForRanges() {
        String chromosome = "1";
        int start = 5;
        int end = 15;
        MutationType mutationType = MutationType.MISSENSE;

        ActionableRange codon = TestServeFactory.rangeBuilder()
                .gene("codon")
                .chromosome(chromosome)
                .start(start)
                .end(end)
                .applicableMutationType(mutationType)
                .source(Knowledgebase.CKB_EVIDENCE)
                .build();

        ActionableRange exon = TestServeFactory.rangeBuilder()
                .gene("exon")
                .chromosome(chromosome)
                .start(start)
                .end(end)
                .applicableMutationType(mutationType)
                .source(Knowledgebase.CKB_EVIDENCE)
                .build();

        ActionableRange codon1 = TestServeFactory.rangeBuilder()
                .gene("codon1")
                .chromosome(chromosome)
                .start(start)
                .end(end)
                .applicableMutationType(mutationType)
                .source(Knowledgebase.CKB_EVIDENCE)
                .build();

        VariantEvidence variantEvidence = new VariantEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(),
                Lists.newArrayList(codon, codon1),
                Lists.newArrayList(exon),
                Lists.newArrayList());

        ReportableVariant variantMatchHigh = TestReportableVariantFactory.builder()
                .gene("exon")
                .chromosome(chromosome)
                .position(start + 1)
                .canonicalHgvsCodingImpact("match")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .driverLikelihood(0.9)
                .build();
        ReportableVariant variantMatchMedium = TestReportableVariantFactory.builder()
                .gene("codon")
                .chromosome(chromosome)
                .position(start + 1)
                .canonicalHgvsCodingImpact("match")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .driverLikelihood(0.5)
                .build();
        ReportableVariant variantMatchLow = TestReportableVariantFactory.builder()
                .gene("codon1")
                .chromosome(chromosome)
                .position(start + 1)
                .canonicalHgvsCodingImpact("match")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .driverLikelihood(0.1)
                .build();
        ReportableVariant variantOutsideRange = TestReportableVariantFactory.builder()
                .gene("gene")
                .chromosome(chromosome)
                .position(start - 1)
                .canonicalHgvsCodingImpact("outside range")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .build();
        ReportableVariant variantWrongGene = TestReportableVariantFactory.builder()
                .gene("wrong gene")
                .chromosome(chromosome)
                .position(start + 1)
                .canonicalHgvsCodingImpact("wrong gene")
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .build();
        ReportableVariant variantWrongMutationType = TestReportableVariantFactory.builder()
                .gene("gene")
                .chromosome(chromosome)
                .position(start + 1)
                .canonicalHgvsCodingImpact("wrong mutation type")
                .canonicalCodingEffect(PurpleCodingEffect.NONSENSE_OR_FRAMESHIFT)
                .build();

        Set<ReportableVariant> reportable = Sets.newHashSet(variantMatchHigh,
                variantMatchMedium,
                variantMatchLow,
                variantOutsideRange,
                variantWrongGene,
                variantWrongMutationType);
        List<ProtectEvidence> evidences = variantEvidence.evidence(reportable, Sets.newHashSet(), Sets.newHashSet(), Sets.newHashSet(), null);

        assertEquals(3, evidences.size());

        ProtectEvidence evidenceHigh = findByGene(evidences, "exon");
        assertTrue(evidenceHigh.reported());
        assertEquals("match", evidenceHigh.event());
        assertEquals(evidenceHigh.sources().size(), 1);
        assertEquals(EvidenceType.EXON_MUTATION, evidenceHigh.sources().iterator().next().evidenceType());

        ProtectEvidence evidenceMedium = findByGene(evidences, "codon");
        assertFalse(evidenceMedium.reported());
        assertEquals("match", evidenceMedium.event());
        assertEquals(evidenceMedium.sources().size(), 1);
        assertEquals(EvidenceType.CODON_MUTATION, evidenceMedium.sources().iterator().next().evidenceType());

        ProtectEvidence evidenceLow = findByGene(evidences, "codon1");
        assertFalse(evidenceLow.reported());
        assertEquals("match", evidenceLow.event());
        assertEquals(evidenceLow.sources().size(), 1);
        assertEquals(EvidenceType.CODON_MUTATION, evidenceLow.sources().iterator().next().evidenceType());
    }

    @Test
    public void canDetermineVariantEvidenceForGenes() {
        String activatedGene = "gene1";
        String inactivatedGene = "gene2";
        String amplifiedGene = "gene3";

        ActionableGene actionableGene1 =
                TestServeFactory.geneBuilder().gene(activatedGene).event(GeneEvent.ACTIVATION).source(Knowledgebase.CKB_EVIDENCE).build();
        ActionableGene actionableGene2 =
                TestServeFactory.geneBuilder().gene(inactivatedGene).event(GeneEvent.INACTIVATION).source(Knowledgebase.CKB_EVIDENCE).build();
        ActionableGene actionableGene3 =
                TestServeFactory.geneBuilder().gene(amplifiedGene).event(GeneEvent.AMPLIFICATION).source(Knowledgebase.CKB_EVIDENCE).build();
        ActionableGene actionableGene4 =
                TestServeFactory.geneBuilder().gene("MLH1").event(GeneEvent.ABSENCE_OF_PROTEIN).source(Knowledgebase.CKB_EVIDENCE).build();
        ActionableGene actionableGene5 =
                TestServeFactory.geneBuilder().gene("KRAS").event(GeneEvent.ABSENCE_OF_PROTEIN).source(Knowledgebase.CKB_EVIDENCE).build();

        VariantEvidence variantEvidence = new VariantEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(),
                Lists.newArrayList(),
                Lists.newArrayList(),
                Lists.newArrayList(actionableGene1, actionableGene2, actionableGene3, actionableGene4, actionableGene5));

        ReportableVariant driverOnActivatedGene = withGeneAndDriverLikelihood(activatedGene, 1D);
        ReportableVariant passengerOnInactivatedGene = withGeneAndDriverLikelihood(inactivatedGene, 0D);
        ReportableVariant driverOnAmplifiedGene = withGeneAndDriverLikelihood(amplifiedGene, 0D);
        ReportableVariant driverOnOtherGene = withGeneAndDriverLikelihood("other", 1D);
        ReportableVariant driverMSI = withGeneAndDriverLikelihood("MLH1", 1D);
        ReportableVariant variantKras = withGeneAndDriverLikelihood("KRAS", 1D);

        Set<ReportableVariant> reportableVariants =
                Sets.newHashSet(driverOnActivatedGene, passengerOnInactivatedGene, driverOnAmplifiedGene, driverOnOtherGene, driverMSI, variantKras);

        Set<PurpleVariant> unreportedVariants = Sets.newHashSet(TestPurpleFactory.variantBuilder()
                .reported(false)
                .gene(activatedGene)
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder().codingEffect(PurpleCodingEffect.NONE).build())
                .build());

        List<ProtectEvidence> evidences =
                variantEvidence.evidence(reportableVariants, Sets.newHashSet(), unreportedVariants, Sets.newHashSet(), null);

        assertEquals(4, evidences.size());

        ProtectEvidence actEvidence = findByGene(evidences, activatedGene);
        assertTrue(actEvidence.reported());
        assertEquals(actEvidence.sources().size(), 1);
        assertEquals(EvidenceType.ACTIVATION, actEvidence.sources().iterator().next().evidenceType());

        ProtectEvidence inactEvidence = findByGene(evidences, inactivatedGene);
        assertFalse(inactEvidence.reported());
        assertEquals(inactEvidence.sources().size(), 1);
        assertEquals(EvidenceType.INACTIVATION, inactEvidence.sources().iterator().next().evidenceType());

        ProtectEvidence MSIEvidence = findByGene(evidences, "MLH1");
        assertTrue(MSIEvidence.reported());
        assertEquals(MSIEvidence.sources().size(), 1);
        assertEquals(EvidenceType.ABSENCE_OF_PROTEIN, MSIEvidence.sources().iterator().next().evidenceType());

        ProtectEvidence absenceEvidence = findByGene(evidences, "KRAS");
        assertFalse(absenceEvidence.reported());
        assertEquals(absenceEvidence.sources().size(), 1);
        assertEquals(EvidenceType.ABSENCE_OF_PROTEIN, absenceEvidence.sources().iterator().next().evidenceType());
    }

    @NotNull
    private static ProtectEvidence findByGene(@NotNull List<ProtectEvidence> evidences, @NotNull String geneToFind) {
        return evidences.stream()
                .filter(x -> Objects.equals(x.gene(), geneToFind))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find evidence for gene: " + geneToFind));
    }

    @NotNull
    private static ReportableVariant withGeneAndDriverLikelihood(@NotNull String gene, double driverLikelihood) {
        return TestReportableVariantFactory.builder()
                .canonicalCodingEffect(PurpleCodingEffect.MISSENSE)
                .gene(gene)
                .driverLikelihood(driverLikelihood)
                .build();
    }
}