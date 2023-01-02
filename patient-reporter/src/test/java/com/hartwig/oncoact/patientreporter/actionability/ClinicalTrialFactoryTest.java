package com.hartwig.oncoact.patientreporter.actionability;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestProtectFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.junit.Test;

public class ClinicalTrialFactoryTest {

    @Test
    public void canExtractClinicalTrials() {
        ProtectEvidence evidence = TestProtectFactory.builder()
                .event("event")
                .germline(false)
                .reported(true)
                .treatment(ImmutableTreatment.builder().name("acronym").build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Sets.newHashSet(TestProtectFactory.sourceBuilder()
                        .name(Knowledgebase.ICLUSION)
                        .evidenceType(EvidenceType.AMPLIFICATION)
                        .build()))
                .build();

        List<ProtectEvidence> trial = ClinicalTrialFactory.extractOnLabelTrials(Lists.newArrayList(evidence));

        assertEquals(1, trial.size());
        assertEquals("event", trial.get(0).event());
        assertEquals("acronym", trial.get(0).treatment().name());
    }
}