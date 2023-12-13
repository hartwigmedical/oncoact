package com.hartwig.oncoact.patientreporter.algo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExperimentTypeTest {

    @Test
    public void canExtractExperimentType() {
        assertEquals(ExperimentType.WHOLE_GENOME, ExperimentType.toExperimentType("WHOLE_GENOME"));
        assertEquals(ExperimentType.TARGETED, ExperimentType.toExperimentType("TARGETED"));
    }

    @Test(expected = IllegalStateException.class)
    public void hasUnknownExperimentType() {
        ExperimentType.toExperimentType("test");
    }

}