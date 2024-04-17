package com.hartwig.oncoact.patientreporter.actionability;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestProtectFactory;
import com.hartwig.serve.datamodel.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ClinicalTrialFactoryTest {

    @Test
    public void canExtractClinicalTrials() {
        ProtectEvidence evidence1 = TestProtectFactory.builder()
                .event("event")
                .germline(false)
                .reported(true)
                .clinicalTrial(ImmutableClinicalTrial.builder()
                        .studyNctId("nct1")
                        .studyTitle("study title")
                        .countriesOfStudy(Sets.newHashSet("netherlands", "belgium"))
                        .build())
                .treatment(ImmutableTreatment.builder().name("therapy").build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Sets.newHashSet(TestProtectFactory.sourceBuilder()
                        .name(Knowledgebase.CKB_TRIAL)
                        .evidenceType(EvidenceType.AMPLIFICATION)
                        .build()))
                .build();

        ProtectEvidence evidence2 = TestProtectFactory.builder()
                .event("event")
                .germline(false)
                .reported(true)
                .clinicalTrial(ImmutableClinicalTrial.builder()
                        .studyNctId("nct1")
                        .studyTitle("study title")
                        .countriesOfStudy(Sets.newHashSet("belgium"))
                        .build())
                .treatment(ImmutableTreatment.builder().name("therapy").build())
                .onLabel(true)
                .level(EvidenceLevel.B)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Sets.newHashSet(TestProtectFactory.sourceBuilder()
                        .name(Knowledgebase.CKB_TRIAL)
                        .evidenceType(EvidenceType.AMPLIFICATION)
                        .build()))
                .build();

        List<ProtectEvidence> trial = ClinicalTrialFactory.extractOnLabelTrials(Lists.newArrayList(evidence1, evidence2));

        assertEquals(1, trial.size());
        assertEquals("event", trial.get(0).event());
        assertEquals("therapy", trial.get(0).treatment().name());
        assertEquals("nct1", trial.get(0).clinicalTrial().studyNctId());
        assertEquals("study title", trial.get(0).clinicalTrial().studyTitle());
        assertEquals(Sets.newHashSet("netherlands", "belgium"), trial.get(0).clinicalTrial().countriesOfStudy());
    }
}