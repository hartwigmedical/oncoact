package com.hartwig.oncoact.protect.algo;

import static com.hartwig.oncoact.protect.TestProtectFactory.builder;
import static com.hartwig.oncoact.protect.algo.EvidenceReportingFunctions.highestReportableLevel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestProtectFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableClinicalTrial;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class EvidenceReportingFunctionsTest {

    private static final ProtectEvidence ON_LABEL_RESPONSIVE_A = createTestEvidence(true, EvidenceDirection.RESPONSIVE, EvidenceLevel.A);
    private static final ProtectEvidence ON_LABEL_RESISTANT_B = createTestEvidence(true, EvidenceDirection.RESISTANT, EvidenceLevel.B);
    private static final ProtectEvidence ON_LABEL_NO_BENEFIT_C = createTestEvidence(true, EvidenceDirection.NO_BENEFIT, EvidenceLevel.C);
    private static final ProtectEvidence ON_LABEL_RESPONSIVE_B = createTestEvidence(true, EvidenceDirection.RESPONSIVE, EvidenceLevel.B);
    private static final ProtectEvidence ON_LABEL_RESPONSIVE_C = createTestEvidence(true, EvidenceDirection.RESPONSIVE, EvidenceLevel.C);
    private static final ProtectEvidence OFF_LABEL_RESPONSIVE_A = createTestEvidence(false, EvidenceDirection.RESPONSIVE, EvidenceLevel.A);
    private static final ProtectEvidence OFF_LABEL_RESPONSIVE_B = createTestEvidence(false, EvidenceDirection.RESPONSIVE, EvidenceLevel.B);
    private static final ProtectEvidence OFF_LABEL_RESPONSIVE_C = createTestEvidence(false, EvidenceDirection.RESPONSIVE, EvidenceLevel.C);

    @Test
    public void canDetermineHighestReportableLevel() {
        List<ProtectEvidence> onAndOffLabelA = Lists.newArrayList(ON_LABEL_RESPONSIVE_A, ON_LABEL_RESPONSIVE_B, OFF_LABEL_RESPONSIVE_A);
        List<ProtectEvidence> onlyOffLabelA = Lists.newArrayList(ON_LABEL_RESPONSIVE_B, OFF_LABEL_RESPONSIVE_A);

        assertEquals(EvidenceLevel.A, highestReportableLevel(true, onAndOffLabelA));
        assertEquals(EvidenceLevel.B, highestReportableLevel(true, onlyOffLabelA));
        assertEquals(EvidenceLevel.A, highestReportableLevel(false, onAndOffLabelA));
        assertEquals(EvidenceLevel.A, highestReportableLevel(false, onlyOffLabelA));
    }

    @Test
    public void respectMaxReportingLevel() {
        List<ProtectEvidence> hartwigEvidences = createTestEvidencesForKnowledgebase(Knowledgebase.HARTWIG_GENE_CURATED);
        List<ProtectEvidence> hartwigFiltered = EvidenceReportingFunctions.applyReportingAlgo(hartwigEvidences);
        assertEquals(3, hartwigFiltered.size());
        assertEquals(0, reported(hartwigFiltered).size());

        List<ProtectEvidence> ckbEvidences = createTestEvidencesForKnowledgebase(Knowledgebase.CKB_EVIDENCE);
        List<ProtectEvidence> ckbFiltered = EvidenceReportingFunctions.applyReportingAlgo(ckbEvidences);
        assertEquals(3, ckbFiltered.size());
        assertEquals(1, reported(ckbFiltered).size());

        List<ProtectEvidence> ckbTrial = createTestEvidencesForKnowledgebase(Knowledgebase.CKB_TRIAL);
        List<ProtectEvidence> ckbTrialFiltered = EvidenceReportingFunctions.applyReportingAlgo(ckbTrial);
        assertEquals(3, ckbTrialFiltered.size());
        assertEquals(1, reported(ckbTrialFiltered).size());
    }

    @NotNull
    private static List<ProtectEvidence> createTestEvidencesForKnowledgebase(@NotNull Knowledgebase knowledgebase) {
        return Lists.newArrayList(TestProtectFactory.builder()
                        .from(ON_LABEL_RESPONSIVE_B)
                        .direction(EvidenceDirection.RESPONSIVE)
                        .sources(Sets.newHashSet(TestProtectFactory.createSource(knowledgebase)))
                        .build(),
                TestProtectFactory.builder()
                        .from(ON_LABEL_RESPONSIVE_C)
                        .direction(EvidenceDirection.RESPONSIVE)
                        .sources(Sets.newHashSet(TestProtectFactory.createSource(knowledgebase)))
                        .build(),
                TestProtectFactory.builder()
                        .from(OFF_LABEL_RESPONSIVE_C)
                        .direction(EvidenceDirection.PREDICTED_RESPONSIVE)
                        .sources(Sets.newHashSet(TestProtectFactory.createSource(knowledgebase)))
                        .build());
    }

    @Test
    public void reportEvidenceDirectionsIndependently() {
        List<ProtectEvidence> evidences = Lists.newArrayList(ON_LABEL_RESPONSIVE_A, ON_LABEL_RESISTANT_B, ON_LABEL_NO_BENEFIT_C);

        List<ProtectEvidence> filtered = EvidenceReportingFunctions.applyReportingAlgo(evidences);
        assertEquals(3, filtered.size());
        assertEquals(3, reported(filtered).size());
    }

    @Test
    public void doNoReportOffLabelAtSameLevelAsOnLabel() {
        List<ProtectEvidence> evidences = Lists.newArrayList(ON_LABEL_RESPONSIVE_A, OFF_LABEL_RESPONSIVE_A);

        List<ProtectEvidence> filtered = EvidenceReportingFunctions.applyReportingAlgo(evidences);
        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(ON_LABEL_RESPONSIVE_A));
        assertFalse(filtered.contains(OFF_LABEL_RESPONSIVE_A));
    }

    @Test
    public void reportHighestOffLabelIfHigherThanOnLabel() {
        List<ProtectEvidence> evidence = Lists.newArrayList(ON_LABEL_RESPONSIVE_C, OFF_LABEL_RESPONSIVE_A, OFF_LABEL_RESPONSIVE_B);

        List<ProtectEvidence> filtered = EvidenceReportingFunctions.applyReportingAlgo(evidence);
        assertEquals(3, filtered.size());
        assertTrue(filtered.contains(OFF_LABEL_RESPONSIVE_A));
        assertFalse(filtered.contains(OFF_LABEL_RESPONSIVE_B));
        assertTrue(filtered.contains(ON_LABEL_RESPONSIVE_C));
    }

    @Test
    public void neverSetReportToTrue() {
        ProtectEvidence reported = ON_LABEL_RESPONSIVE_A;
        ProtectEvidence reportedFiltered = EvidenceReportingFunctions.applyReportingAlgo(Lists.newArrayList(reported)).get(0);
        assertTrue(reportedFiltered.reported());

        ProtectEvidence notReported = TestProtectFactory.builder().from(reported).reported(false).build();
        ProtectEvidence notReportedFiltered = EvidenceReportingFunctions.applyReportingAlgo(Lists.newArrayList(notReported)).get(0);
        assertFalse(notReportedFiltered.reported());

        ProtectEvidence evidence1 = TestProtectFactory.builder()
                .from(createTestEvidence(true, EvidenceDirection.RESPONSIVE, EvidenceLevel.A))
                .reported(false)
                .build();
        ProtectEvidence evidence2 = createTestEvidence(false, EvidenceDirection.RESPONSIVE, EvidenceLevel.A);

        List<ProtectEvidence> filtered = EvidenceReportingFunctions.applyReportingAlgo(Lists.newArrayList(evidence1, evidence2));
        assertTrue(filtered.contains(evidence1));
        assertTrue(filtered.contains(evidence2));
    }

    @Test
    public void doNotReportOffLabelTrials() {
        String event1 = "event1";
        String event2 = "event2";
        String event3 = "event3";
        String event4 = "event4";

        ProtectEvidence evidence1 = builder().event(event1)
                .sources(Sets.newHashSet(TestProtectFactory.createSource(Knowledgebase.CKB_TRIAL)))
                .reported(true)
                .onLabel(true)
                .build();
        ProtectEvidence evidence2 = builder().event(event2)
                .sources(Sets.newHashSet(TestProtectFactory.createSource(Knowledgebase.CKB_TRIAL)))
                .reported(true)
                .onLabel(false)
                .build();
        ProtectEvidence evidence3 = builder().event(event3)
                .sources(Sets.newHashSet(TestProtectFactory.createSource(Knowledgebase.VICC_CGI)))
                .reported(true)
                .onLabel(false)
                .build();
        ProtectEvidence evidence4 = builder().event(event4)
                .sources(Sets.newHashSet(TestProtectFactory.createSource(Knowledgebase.CKB_TRIAL),
                        TestProtectFactory.createSource(Knowledgebase.VICC_CGI)))
                .reported(true)
                .onLabel(false)
                .build();

        List<ProtectEvidence> evidence =
                EvidenceReportingFunctions.reportOnLabelTrialsOnly(Lists.newArrayList(evidence1, evidence2, evidence3, evidence4));

        assertEquals(4, evidence.size());
        assertTrue(evidence.contains(evidence1));
        assertTrue(evidence.contains(evidence3));
        assertTrue(evidence.contains(evidence4));

        ProtectEvidence convertedEvidence2 = findByEvent(evidence, event2);
        assertFalse(convertedEvidence2.reported());
    }

    @NotNull
    private static List<ProtectEvidence> reported(@NotNull List<ProtectEvidence> evidences) {
        List<ProtectEvidence> filtered = Lists.newArrayList();
        for (ProtectEvidence evidence : evidences) {
            if (evidence.reported()) {
                filtered.add(evidence);
            }
        }
        return filtered;
    }

    @NotNull
    private static ProtectEvidence findByEvent(@NotNull Iterable<ProtectEvidence> evidences, @NotNull String event) {
        for (ProtectEvidence evidence : evidences) {
            if (evidence.event().equals(event)) {
                return evidence;
            }
        }

        throw new IllegalStateException("Could not find evidence with genomic event: " + event);
    }

    @NotNull
    private static ProtectEvidence createTestEvidence(boolean onLabel, @NotNull EvidenceDirection direction, @NotNull EvidenceLevel level) {
        return TestProtectFactory.builder()
                .onLabel(onLabel)
                .level(level)
                .direction(direction)
                .clinicalTrial(ImmutableClinicalTrial.builder()
                        .studyNctId("nct1")
                        .studyTitle("title")
                        .countriesOfStudy(Sets.newHashSet("netherlands"))
                        .build())
                .sources(Sets.newHashSet(TestProtectFactory.createSource(Knowledgebase.CKB_EVIDENCE)))
                .build();
    }
}