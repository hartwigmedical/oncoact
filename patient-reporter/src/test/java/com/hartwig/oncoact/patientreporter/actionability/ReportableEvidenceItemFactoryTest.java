package com.hartwig.oncoact.patientreporter.actionability;

import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestProtectFactory;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.junit.Test;

public class ReportableEvidenceItemFactoryTest {

    @Test
    public void reportableFactoryWorksForTrivialCase() {
        ProtectEvidence item1 = TestProtectFactory.builder()
                .event("A")
                .treatment(ImmutableTreatment.builder().name("A").build())
                .onLabel(true)
                .level(EvidenceLevel.A)
                .sources(Sets.newHashSet(TestProtectFactory.sourceBuilder()
                        .name(Knowledgebase.VICC_CIVIC)
                        .evidenceType(EvidenceType.AMPLIFICATION)
                        .build()))
                .build();
        ProtectEvidence item2 = TestProtectFactory.builder()
                .event("A")
                .treatment(ImmutableTreatment.builder().name("A").build())
                .onLabel(true)
                .level(EvidenceLevel.A)
                .sources(Sets.newHashSet(TestProtectFactory.sourceBuilder()
                        .name(Knowledgebase.VICC_CGI)
                        .evidenceType(EvidenceType.AMPLIFICATION)
                        .build()))
                .build();
        ProtectEvidence item3 = TestProtectFactory.builder()
                .event("B")
                .treatment(ImmutableTreatment.builder().name("B").build())
                .onLabel(true)
                .level(EvidenceLevel.A)
                .sources(Sets.newHashSet(TestProtectFactory.sourceBuilder()
                        .name(Knowledgebase.ICLUSION)
                        .evidenceType(EvidenceType.AMPLIFICATION)
                        .build()))
                .build();
        ProtectEvidence item4 = TestProtectFactory.builder()
                .event("C")
                .treatment(ImmutableTreatment.builder().name("C").build())
                .onLabel(true)
                .level(EvidenceLevel.C)
                .sources(Sets.newHashSet(TestProtectFactory.sourceBuilder()
                        .name(Knowledgebase.VICC_CGI)
                        .evidenceType(EvidenceType.AMPLIFICATION)
                        .build()))
                .build();

        List<ProtectEvidence> nonTrials =
                ReportableEvidenceItemFactory.extractNonTrialsOnLabel(Lists.newArrayList(item1, item2, item3, item4));
        assertTrue(nonTrials.contains(item1));
        assertTrue(nonTrials.contains(item2));
        assertTrue(nonTrials.contains(item4));
    }
}