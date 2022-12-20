package com.hartwig.oncoact.protect.characteristic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicCutoffType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CharacteristicsFunctionsTest {

    @Test
    public void canDetermineWhetherCharacteristicHasCutoff() {
        ActionableCharacteristic withoutCutoff = TestServeFactory.characteristicBuilder().cutoffType(null).cutoff(null).build();

        assertFalse(CharacteristicsFunctions.hasExplicitCutoff(withoutCutoff));

        ActionableCharacteristic withCutoff =
                TestServeFactory.characteristicBuilder().cutoffType(TumorCharacteristicCutoffType.GREATER).cutoff(4D).build();

        assertTrue(CharacteristicsFunctions.hasExplicitCutoff(withCutoff));
    }

    @Test
    public void canEvaluateCutoffs() {
        ActionableCharacteristic greaterOrEqualOne = create(TumorCharacteristicCutoffType.EQUAL_OR_GREATER, 1D);
        ActionableCharacteristic greaterOne = create(TumorCharacteristicCutoffType.GREATER, 1D);
        ActionableCharacteristic lowerOrEqualOne = create(TumorCharacteristicCutoffType.EQUAL_OR_LOWER, 1D);
        ActionableCharacteristic lowerOne = create(TumorCharacteristicCutoffType.LOWER, 1D);

        assertTrue(CharacteristicsFunctions.evaluateVersusCutoff(greaterOrEqualOne, 2D));
        assertTrue(CharacteristicsFunctions.evaluateVersusCutoff(greaterOne, 2D));
        assertFalse(CharacteristicsFunctions.evaluateVersusCutoff(lowerOrEqualOne, 2D));
        assertFalse(CharacteristicsFunctions.evaluateVersusCutoff(lowerOne, 2D));

        assertTrue(CharacteristicsFunctions.evaluateVersusCutoff(greaterOrEqualOne, 1D));
        assertFalse(CharacteristicsFunctions.evaluateVersusCutoff(greaterOne, 1D));
        assertTrue(CharacteristicsFunctions.evaluateVersusCutoff(lowerOrEqualOne, 1D));
        assertFalse(CharacteristicsFunctions.evaluateVersusCutoff(lowerOne, 1D));

        assertFalse(CharacteristicsFunctions.evaluateVersusCutoff(greaterOrEqualOne, 0D));
        assertFalse(CharacteristicsFunctions.evaluateVersusCutoff(greaterOne, 0D));
        assertTrue(CharacteristicsFunctions.evaluateVersusCutoff(lowerOrEqualOne, 0D));
        assertTrue(CharacteristicsFunctions.evaluateVersusCutoff(lowerOne, 0D));
    }

    @NotNull
    private static ActionableCharacteristic create(@NotNull TumorCharacteristicCutoffType cutoffType, double cutoff) {
        return TestServeFactory.characteristicBuilder().cutoffType(cutoffType).cutoff(cutoff).build();
    }
}