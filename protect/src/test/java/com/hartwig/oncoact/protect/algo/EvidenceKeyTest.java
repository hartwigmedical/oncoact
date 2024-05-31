package com.hartwig.oncoact.protect.algo;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestProtectFactory;
import com.hartwig.serve.datamodel.ImmutableTreatment;

import org.junit.Test;

public class EvidenceKeyTest {

    @Test
    public void canCreateKeySet() {
        List<ProtectEvidence> evidences = Lists.newArrayList();

        ProtectEvidence evidence1 = TestProtectFactory.builder()
                .gene("gene 1")
                .event("event 1")
                .treatment(ImmutableTreatment.builder()
                        .name("treatment 1")
                        .treatmentApproachesDrugClass(Sets.newHashSet("AA"))
                        .treatmentApproachesTherapy(Sets.newHashSet("A"))
                        .build())
                .build();
        evidences.add(evidence1);
        evidences.add(evidence1);
        evidences.add(evidence1);

        Set<EvidenceKey> keys = EvidenceKey.buildKeySet(evidences);
        assertEquals(1, keys.size());
        EvidenceKey key = keys.iterator().next();
        assertEquals("gene 1", key.gene());
        assertEquals("event 1", key.event());
        assertEquals("treatment 1", key.treatment());

        ProtectEvidence evidence2 = TestProtectFactory.builder()
                .gene(null)
                .event("event 2")
                .treatment(ImmutableTreatment.builder()
                        .name("treatment 2")
                        .treatmentApproachesDrugClass(Sets.newHashSet("AA"))
                        .treatmentApproachesTherapy(Sets.newHashSet("A"))
                        .build())
                .build();
        evidences.add(evidence2);
        evidences.add(evidence2);
        evidences.add(evidence2);

        assertEquals(2, EvidenceKey.buildKeySet(evidences).size());
    }
}