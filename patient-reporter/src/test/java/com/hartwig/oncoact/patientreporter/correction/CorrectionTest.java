package com.hartwig.oncoact.patientreporter.correction;

import static org.junit.Assert.*;

import java.io.IOException;

import com.google.common.io.Resources;

import org.junit.Test;

public class CorrectionTest {
    private static final String CORRECTION_JSON = Resources.getResource("correction/correction.json").getPath();

    @Test
    public void correctionFromTsvWithNewLines() throws IOException {
        Correction correction = Correction.read(CORRECTION_JSON);

        assertNull(correction.comments());

        String specialRemark = correction.specialRemark();

        assertEquals(3, specialRemark.split("\n").length);
    }
}