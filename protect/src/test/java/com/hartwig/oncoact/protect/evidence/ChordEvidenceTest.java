package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.common.chord.ChordData;
import com.hartwig.oncoact.common.chord.ChordStatus;
import com.hartwig.oncoact.common.chord.ChordTestFactory;
import com.hartwig.oncoact.common.chord.ImmutableChordData;
import com.hartwig.oncoact.common.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.ServeTestFactory;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicAnnotation;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicsComparator;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ChordEvidenceTest {

    @Test
    public void canDetermineEvidenceForChord() {
        ActionableCharacteristic signature1 = ImmutableActionableCharacteristic.builder()
                .from(ServeTestFactory.createTestActionableCharacteristic())
                .name(TumorCharacteristicAnnotation.HOMOLOGOUS_RECOMBINATION_DEFICIENT)
                .build();

        ActionableCharacteristic signature2 = ImmutableActionableCharacteristic.builder()
                .from(ServeTestFactory.createTestActionableCharacteristic())
                .name(TumorCharacteristicAnnotation.HIGH_TUMOR_MUTATIONAL_LOAD)
                .build();

        ActionableCharacteristic signature3 = ImmutableActionableCharacteristic.builder()
                .from(ServeTestFactory.createTestActionableCharacteristic())
                .name(TumorCharacteristicAnnotation.HOMOLOGOUS_RECOMBINATION_DEFICIENT)
                .comparator(TumorCharacteristicsComparator.GREATER)
                .cutoff(0.8)
                .build();

        ChordEvidence chordEvidence =
                new ChordEvidence(EvidenceTestFactory.create(), Lists.newArrayList(signature1, signature2, signature3));

        ChordData hrDeficient = create(ChordStatus.HR_DEFICIENT, 0.5);
        List<ProtectEvidence> evidence = chordEvidence.evidence(hrDeficient);
        assertEquals(1, evidence.size());

        ProtectEvidence evidence1 = evidence.get(0);
        assertTrue(evidence1.reported());
        assertEquals(ChordEvidence.HR_DEFICIENCY_EVENT, evidence1.event());

        ChordData hrProficientWithHighScore = create(ChordStatus.HR_PROFICIENT, 0.85);
        assertEquals(1, chordEvidence.evidence(hrProficientWithHighScore).size());

        ChordData hrProficientWithLowScore = create(ChordStatus.HR_PROFICIENT, 0.2);
        assertEquals(0, chordEvidence.evidence(hrProficientWithLowScore).size());
    }

    @NotNull
    private static ChordData create(@NotNull ChordStatus status, double hrdValue) {
        return ImmutableChordData.builder()
                .from(ChordTestFactory.createMinimalTestChordAnalysis())
                .hrStatus(status)
                .hrdValue(hrdValue)
                .build();
    }
}