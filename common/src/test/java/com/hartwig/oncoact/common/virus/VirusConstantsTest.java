package com.hartwig.oncoact.common.virus;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VirusConstantsTest {

    @Test
    public void canExtractVirusConstants() {
        VirusConstants VirusConstantsHPV = VirusConstants.fromVirusName("HPV");
        assertEquals(VirusConstants.HPV, VirusConstantsHPV);

        VirusConstants VirusConstantsMCV = VirusConstants.fromVirusName("MCV");
        assertEquals(VirusConstants.MCV, VirusConstantsMCV);

        VirusConstants VirusConstantsEBV = VirusConstants.fromVirusName("EBV");
        assertEquals(VirusConstants.EBV, VirusConstantsEBV);

        VirusConstants VirusConstantsHBV = VirusConstants.fromVirusName("HBV");
        assertEquals(VirusConstants.HBV, VirusConstantsHBV);

        VirusConstants VirusConstantsHHV8 = VirusConstants.fromVirusName("HHV-8");
        assertEquals(VirusConstants.HHV8, VirusConstantsHHV8);
    }

    @Test(expected = IllegalStateException.class)
    public void hasUnknownVirusConstants() {
        //noinspection ResultOfMethodCallIgnored
        VirusConstants.fromVirusName("ABC");
    }

}