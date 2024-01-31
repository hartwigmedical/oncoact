package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.CopyNumberInterpretation;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CopyNumberEvidenceTest {

    @Test
    public void canDetermineCopyNumberEvidence() {
        String geneAmp = "geneAmp";
        String geneInact = "geneInact";
        String geneDel = "geneDel";
        ActionableGene amp = TestServeFactory.geneBuilder().gene(geneAmp).event(GeneEvent.AMPLIFICATION).build();
        ActionableGene inactivation = TestServeFactory.geneBuilder().gene(geneInact).event(GeneEvent.INACTIVATION).build();
        ActionableGene deletion = TestServeFactory.geneBuilder().gene(geneDel).event(GeneEvent.DELETION).build();
        ActionableGene fusion = TestServeFactory.geneBuilder().gene(geneAmp).event(GeneEvent.FUSION).build();

        CopyNumberEvidence copyNumberEvidence =
                new CopyNumberEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(amp, inactivation, fusion, deletion));

        PurpleGainLoss reportableAmp =
                TestPurpleFactory.gainLossBuilder().gene(geneAmp).interpretation(CopyNumberInterpretation.FULL_GAIN).build();
        PurpleGainLoss reportableDel =
                TestPurpleFactory.gainLossBuilder().gene(geneInact).interpretation(CopyNumberInterpretation.FULL_LOSS).build();
        PurpleGainLoss ampOnOtherGene =
                TestPurpleFactory.gainLossBuilder().gene("other gene").interpretation(CopyNumberInterpretation.PARTIAL_GAIN).build();
        PurpleGainLoss reportableGermlineDel =
                TestPurpleFactory.gainLossBuilder().gene(geneDel).interpretation(CopyNumberInterpretation.FULL_LOSS).build();

        List<PurpleGainLoss> reportableSomaticGainLosses = Lists.newArrayList(reportableAmp, reportableDel, ampOnOtherGene);
        List<PurpleGainLoss> reportableGermlineGainLosses = Lists.newArrayList(reportableGermlineDel);
        List<PurpleGainLoss> unreportedGainLosses = Lists.newArrayList();
        List<PurpleGainLoss> unreportedGermlineGainLosses = Lists.newArrayList();
        List<ProtectEvidence> evidences = copyNumberEvidence.evidence(reportableSomaticGainLosses,
                unreportedGainLosses,
                reportableGermlineGainLosses,
                unreportedGermlineGainLosses,
                Lists.newArrayList(),
                Lists.newArrayList());

        assertEquals(3, evidences.size());

        ProtectEvidence ampEvidence = find(evidences, geneAmp);
        assertTrue(ampEvidence.reported());
        assertEquals(ampEvidence.sources().size(), 1);
        assertEquals(EvidenceType.AMPLIFICATION, ampEvidence.sources().iterator().next().evidenceType());

        ProtectEvidence inactEvidence = find(evidences, geneInact);
        assertTrue(inactEvidence.reported());
        assertEquals(inactEvidence.sources().size(), 1);
        assertEquals(EvidenceType.INACTIVATION, inactEvidence.sources().iterator().next().evidenceType());

        ProtectEvidence delEvidence = find(evidences, geneDel);
        assertTrue(delEvidence.reported());
        assertEquals(delEvidence.sources().size(), 1);
        assertEquals(EvidenceType.DELETION, delEvidence.sources().iterator().next().evidenceType());
    }

    @NotNull
    private static ProtectEvidence find(@NotNull List<ProtectEvidence> evidences, @NotNull String geneToFind) {

        return evidences.stream()
                .filter(x -> Objects.equals(x.gene(), geneToFind))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find evidence for gene: " + geneToFind));

    }
}