package com.hartwig.oncoact.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AminoAcidsTest {

    @Test
    public void canForceSingleLetterProteinAnnotation() {
        assertEquals("p.N334K", AminoAcids.forceSingleLetterProteinAnnotation("p.Asn334Lys"));
    }
}