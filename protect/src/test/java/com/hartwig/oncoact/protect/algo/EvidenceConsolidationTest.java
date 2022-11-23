package com.hartwig.oncoact.protect.algo;

import static com.hartwig.oncoact.common.protect.ProtectTestFactory.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.common.protect.KnowledgebaseSource;
import com.hartwig.oncoact.common.protect.ProtectEvidence;
import com.hartwig.oncoact.common.protect.ProtectTestFactory;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class EvidenceConsolidationTest {

    @Test
    public void canConsolidateSources() {
        String treatment1 = "treatment1";
        String treatment2 = "treatment2";
        Knowledgebase knowledgebase1 = Knowledgebase.VICC_CGI;
        Knowledgebase knowledgebase2 = Knowledgebase.VICC_CIVIC;

        ProtectEvidence evidence1 =
                builder().treatment(ImmutableTreatment.builder()
                        .name(treatment1)
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("AA"))
                        .relevantTreatmentApproaches(Sets.newHashSet("A"))
                        .build()).sources(Sets.newHashSet(ProtectTestFactory.createSource(knowledgebase1))).build();
        ProtectEvidence evidence2 =
                builder().treatment(ImmutableTreatment.builder()
                        .name(treatment1)
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("AA"))
                        .relevantTreatmentApproaches(Sets.newHashSet("A"))
                        .build()).sources(Sets.newHashSet(ProtectTestFactory.createSource(knowledgebase2))).build();
        ProtectEvidence evidence3 =
                builder().treatment(ImmutableTreatment.builder()
                        .name(treatment2)
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("AA"))
                        .relevantTreatmentApproaches(Sets.newHashSet("A"))
                        .build()).sources(Sets.newHashSet(ProtectTestFactory.createSource(knowledgebase2))).build();

        List<ProtectEvidence> consolidated = EvidenceConsolidation.consolidate(Lists.newArrayList(evidence1, evidence2, evidence3));

        assertEquals(2, consolidated.size());

        ProtectEvidence consolidatedEvidence1 = findByTreatment(consolidated, treatment1);
        assertEquals(2, consolidatedEvidence1.sources().size());
        assertNotNull(findByKnowledgebase(consolidatedEvidence1.sources(), knowledgebase1));
        assertNotNull(findByKnowledgebase(consolidatedEvidence1.sources(), knowledgebase2));

        ProtectEvidence consolidatedEvidence2 = findByTreatment(consolidated, treatment2);
        assertEquals(1, consolidatedEvidence2.sources().size());
        assertNotNull(findByKnowledgebase(consolidatedEvidence1.sources(), knowledgebase2));
    }

    @NotNull
    private static ProtectEvidence findByTreatment(@NotNull Iterable<ProtectEvidence> evidences, @NotNull String treatment) {
        for (ProtectEvidence evidence : evidences) {
            if (evidence.treatment().name().equals(treatment)) {
                return evidence;
            }
        }

        throw new IllegalStateException("Could not find evidence with treatment: " + treatment);
    }

    @NotNull
    private static KnowledgebaseSource findByKnowledgebase(@NotNull Set<KnowledgebaseSource> sources, @NotNull Knowledgebase knowledgebaseToFind) {
        for (KnowledgebaseSource source : sources) {
            if (source.name() == knowledgebaseToFind) {
                return source;
            }
        }

        throw new IllegalStateException("Could not find evidence from source: " + knowledgebaseToFind);
    }
}