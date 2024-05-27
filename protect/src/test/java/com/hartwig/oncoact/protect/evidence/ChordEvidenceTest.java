package com.hartwig.oncoact.protect.evidence;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.chord.ChordRecord;
import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.oncoact.orange.chord.TestChordFactory;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicCutoffType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChordEvidenceTest {

    @Test
    public void canDetermineEvidenceForChord() {
        ActionableCharacteristic signature1 =
                TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.HOMOLOGOUS_RECOMBINATION_DEFICIENT).build();

        ActionableCharacteristic signature2 =
                TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_LOAD).build();

        ActionableCharacteristic signature3 = TestServeFactory.characteristicBuilder()
                .type(TumorCharacteristicType.HOMOLOGOUS_RECOMBINATION_DEFICIENT)
                .cutoffType(TumorCharacteristicCutoffType.GREATER)
                .cutoff(0.8)
                .build();

        ChordEvidence chordEvidence =
                new ChordEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(signature1, signature2, signature3));

        ChordRecord hrDeficient = create(ChordStatus.HR_DEFICIENT, 0.5);
        List<ProtectEvidence> evidence = chordEvidence.evidence(hrDeficient, null);
        assertEquals(1, evidence.size());

        ProtectEvidence evidence1 = evidence.get(0);
        assertTrue(evidence1.reported());
        assertEquals(ChordEvidence.HR_DEFICIENCY_EVENT, evidence1.event());

        ChordRecord hrProficientWithHighScore = create(ChordStatus.HR_PROFICIENT, 0.85);
        assertEquals(0, chordEvidence.evidence(hrProficientWithHighScore, null).size());

        ChordRecord hrProficientWithLowScore = create(ChordStatus.HR_PROFICIENT, 0.2);
        assertEquals(0, chordEvidence.evidence(hrProficientWithLowScore, null).size());

        ChordRecord hrCannotBeDeterminedWithLowScore = create(ChordStatus.CANNOT_BE_DETERMINED, 0.2);
        assertEquals(0, chordEvidence.evidence(hrCannotBeDeterminedWithLowScore, null).size());

        ChordRecord hrCannotBeDeterminedWithHighScore = create(ChordStatus.CANNOT_BE_DETERMINED, 0.9);
        assertEquals(0, chordEvidence.evidence(hrCannotBeDeterminedWithHighScore, null).size());
    }

    @NotNull
    private static ChordRecord create(@NotNull ChordStatus status, double hrdValue) {
        return TestChordFactory.builder().hrStatus(status).hrdValue(hrdValue).build();
    }
}