package com.hartwig.oncoact.protect;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.Knowledgebase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class KnowledgebaseSourceComparatorTest {

    @Test
    public void canSortKnowledgebaseSources() {
        KnowledgebaseSource source1 = create(Knowledgebase.CKB_EVIDENCE, "event 1", EvidenceType.ANY_MUTATION);
        KnowledgebaseSource source2 = create(Knowledgebase.CKB_EVIDENCE, "event 1", EvidenceType.WILD_TYPE);
        KnowledgebaseSource source3 = create(Knowledgebase.CKB_EVIDENCE, "event 2", EvidenceType.ANY_MUTATION);
        KnowledgebaseSource source4 = create(Knowledgebase.VICC_CGI, "event 1", EvidenceType.ANY_MUTATION);

        List<KnowledgebaseSource> sources = Lists.newArrayList(source1, source3, source2, source4);
        sources.sort(new KnowledgebaseSourceComparator());

        assertEquals(4, sources.size());
        assertEquals(source1, sources.get(0));
        assertEquals(source2, sources.get(1));
        assertEquals(source3, sources.get(2));
        assertEquals(source4, sources.get(3));
    }

    @NotNull
    private static KnowledgebaseSource create(@NotNull Knowledgebase name, @NotNull String sourceEvent, @NotNull EvidenceType evidentType) {
        return TestProtectFactory.sourceBuilder().name(name).sourceEvent(sourceEvent).evidenceType(evidentType).rangeRank(null).build();
    }
}