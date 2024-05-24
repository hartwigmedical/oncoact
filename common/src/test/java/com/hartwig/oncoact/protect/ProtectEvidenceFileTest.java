package com.hartwig.oncoact.protect;

import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.Knowledgebase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProtectEvidenceFileTest {

    private static final String EVIDENCE_TSV = Resources.getResource("protect/sample.protect.tsv").getPath();

    @Test
    public void canReadProtectEvidenceFile() throws IOException {
        List<ProtectEvidence> evidences = ProtectEvidenceFile.read(EVIDENCE_TSV);
        assertEquals(5, evidences.size());

        // evidences 1
        ProtectEvidence evidence1 = findByTreatmentAndEvent(evidences, "Cobimetinib + Vemurafenib", "p.Val600Glu");
        assertEquals(1, evidence1.sources().size());

        KnowledgebaseSource evidence1Source = findBySource(evidence1.sources(), Knowledgebase.CKB_EVIDENCE);
        assertEquals("hotspot", evidence1Source.sourceEvent());
        assertEquals(Sets.newHashSet("https://www.google.com/#q=FDA"), evidence1Source.sourceUrls());
        assertEquals(EvidenceType.HOTSPOT_MUTATION, evidence1Source.evidenceType());
        assertTrue(evidence1Source.evidenceUrls().isEmpty());

        // evidences 2
        ProtectEvidence evidence2 = findByTreatmentAndEvent(evidences, "Dabrafenib", "p.Val600Glu");
        assertEquals(1, evidence2.sources().size());

        KnowledgebaseSource evidence2Source = findBySource(evidence2.sources(), Knowledgebase.CKB_EVIDENCE);
        assertEquals("hotspot", evidence2Source.sourceEvent());
        assertEquals(Sets.newHashSet("https://www.google.com/#q=FDA", "https://www.google.com/#q=NCCN"), evidence2Source.sourceUrls());
        assertEquals(EvidenceType.HOTSPOT_MUTATION, evidence2Source.evidenceType());
        assertTrue(evidence2Source.evidenceUrls().isEmpty());

        // evidences 3
        ProtectEvidence evidence3 = findByTreatmentAndEvent(evidences, "Dabrafenib + Trametinib", "p.Val600Glu");
        assertEquals(2, evidence3.sources().size());

        KnowledgebaseSource evidence3Source1 = findBySource(evidence3.sources(), Knowledgebase.CKB_EVIDENCE);
        assertEquals("hotspot", evidence3Source1.sourceEvent());
        assertTrue(evidence3Source1.sourceUrls().isEmpty());
        assertEquals(EvidenceType.HOTSPOT_MUTATION, evidence3Source1.evidenceType());
        assertEquals(Sets.newHashSet("http://www.ncbi.nlm.nih.gov/pubmed/25399551", "http://www.ncbi.nlm.nih.gov/pubmed/27283860"),
                evidence3Source1.evidenceUrls());

        KnowledgebaseSource evidence3Source2 = findBySource(evidence3.sources(), Knowledgebase.CKB_TRIAL);
        assertEquals("hotspot", evidence3Source2.sourceEvent());
        assertEquals(Sets.newHashSet("https://www.google.com/#q=FDA"), evidence3Source2.sourceUrls());
        assertEquals(EvidenceType.HOTSPOT_MUTATION, evidence3Source2.evidenceType());
        assertTrue(evidence3Source2.evidenceUrls().isEmpty());

        // evidences 4
        ProtectEvidence evidence4 = findByTreatmentAndEvent(evidences, "Vemurafenib", "p.Val600Glu");
        assertEquals(1, evidence4.sources().size());

        KnowledgebaseSource evidence4Source = findBySource(evidence4.sources(), Knowledgebase.CKB_EVIDENCE);
        assertEquals("hotspot", evidence4Source.sourceEvent());
        assertEquals(Sets.newHashSet("https://www.google.com/#q=FDA"), evidence4Source.sourceUrls());
        assertEquals(EvidenceType.HOTSPOT_MUTATION, evidence4Source.evidenceType());
        assertTrue(evidence4Source.evidenceUrls().isEmpty());

        // evidences 5
        ProtectEvidence evidence5 = findByTreatmentAndEvent(evidences, "Vemurafenib", "some mutation");
        assertEquals(1, evidence5.sources().size());

        KnowledgebaseSource evidence5Source1 = findBySource(evidence5.sources(), Knowledgebase.CKB_EVIDENCE);
        assertEquals("hotspot", evidence5Source1.sourceEvent());
        assertEquals(Sets.newHashSet("https://www.google.com/#q=FDA"), evidence5Source1.sourceUrls());
        assertEquals(EvidenceType.SIGNATURE, evidence5Source1.evidenceType());
        assertTrue(evidence5Source1.evidenceUrls().isEmpty());
    }

    @Test
    public void canConvertSourcesBackAndForth() {
        Set<KnowledgebaseSource> sources = Sets.newHashSet();

        sources.add(TestProtectFactory.sourceBuilder()
                .name(Knowledgebase.VICC_CGI)
                .sourceEvent("event 1")
                .sourceUrls(Sets.newHashSet("url1", "url2", "url3"))
                .evidenceType(EvidenceType.ANY_MUTATION)
                .evidenceUrls(Sets.newHashSet("url4", "url5", "url6"))
                .build());

        sources.add(TestProtectFactory.sourceBuilder()
                .name(Knowledgebase.VICC_CIVIC)
                .sourceEvent("event 2")
                .evidenceType(EvidenceType.HOTSPOT_MUTATION)
                .build());

        sources.add(TestProtectFactory.sourceBuilder()
                .name(Knowledgebase.VICC_JAX)
                .sourceEvent("event 3")
                .evidenceType(EvidenceType.HOTSPOT_MUTATION)
                .evidenceUrls(Sets.newHashSet("url1"))
                .build());

        assertEquals(sources, ProtectEvidenceFile.stringToSources(ProtectEvidenceFile.sourcesToString(sources)));
    }

    @NotNull
    private static ProtectEvidence findByTreatmentAndEvent(@NotNull Iterable<ProtectEvidence> evidences, @NotNull String treatment,
                                                           @NotNull String event) {
        for (ProtectEvidence evidence : evidences) {
            if (evidence.treatment().name().equals(treatment) && evidence.event().equals(event)) {
                return evidence;
            }
        }

        throw new IllegalStateException("Could not find evidence with treatment: " + treatment + " and event " + event);
    }

    @NotNull
    private static KnowledgebaseSource findBySource(@NotNull Iterable<KnowledgebaseSource> sources, @NotNull Knowledgebase sourceToFind) {
        for (KnowledgebaseSource source : sources) {
            if (source.name() == sourceToFind) {
                return source;
            }
        }

        throw new IllegalStateException("Could not find source: " + sourceToFind);
    }
}