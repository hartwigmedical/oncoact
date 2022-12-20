package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.orange.datamodel.linx.LinxHomozygousDisruption;
import com.hartwig.oncoact.orange.datamodel.linx.TestLinxFactory;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class DisruptionEvidenceTest {

    @Test
    public void canDetermineEvidenceForHomozygousDisruptions() {
        String geneAmp = "geneAmp";
        String geneInact = "geneInact";
        String geneDel = "geneDel";
        String geneUnder = "geneUnder";

        ActionableGene amp = TestServeFactory.geneBuilder().gene(geneAmp).event(GeneEvent.AMPLIFICATION).build();
        ActionableGene inactivation = TestServeFactory.geneBuilder().gene(geneInact).event(GeneEvent.INACTIVATION).build();
        ActionableGene deletion = TestServeFactory.geneBuilder().gene(geneDel).event(GeneEvent.DELETION).build();
        ActionableGene underexpression = TestServeFactory.geneBuilder().gene(geneUnder).event(GeneEvent.UNDEREXPRESSION).build();

        DisruptionEvidence disruptionEvidence = new DisruptionEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(amp, inactivation, deletion, underexpression));

        LinxHomozygousDisruption matchAmp = create(geneAmp);
        LinxHomozygousDisruption matchInact = create(geneInact);
        LinxHomozygousDisruption nonMatch = create("other gene");

        List<ProtectEvidence> evidences = disruptionEvidence.evidence(Sets.newHashSet(matchAmp, matchInact, nonMatch));

        assertEquals(1, evidences.size());
        ProtectEvidence evidence = evidences.get(0);
        assertTrue(evidence.reported());
        assertEquals(geneInact, evidence.gene());
        assertEquals(DisruptionEvidence.HOMOZYGOUS_DISRUPTION_EVENT, evidence.event());

        assertEquals(evidence.sources().size(), 1);
        assertEquals(EvidenceType.INACTIVATION, evidence.sources().iterator().next().evidenceType());
    }

    @NotNull
    private static LinxHomozygousDisruption create(@NotNull String gene) {
        return TestLinxFactory.homozygousDisruptionBuilder().gene(gene).build();
    }
}