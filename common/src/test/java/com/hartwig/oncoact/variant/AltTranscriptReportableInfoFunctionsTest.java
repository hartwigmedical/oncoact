package com.hartwig.oncoact.variant;

import static org.junit.Assert.assertEquals;

import com.hartwig.oncoact.orange.purple.PurpleCodingEffect;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class AltTranscriptReportableInfoFunctionsTest {

    @Test
    public void canExtractFromOtherEffects() {
        String example = "ENST00000579755|c.246_247delCG|p.Gly83fs|frameshift_variant|NONSENSE_OR_FRAMESHIFT";

        assertEquals("ENST00000579755", AltTranscriptReportableInfoFunctions.firstOtherTranscript(example));
        assertEquals("c.246_247delCG", AltTranscriptReportableInfoFunctions.firstOtherHgvsCodingImpact(example));
        assertEquals("p.Gly83fs", AltTranscriptReportableInfoFunctions.firstOtherHgvsProteinImpact(example));
        assertEquals("frameshift_variant", AltTranscriptReportableInfoFunctions.firstOtherEffects(example));
        assertEquals(PurpleCodingEffect.NONSENSE_OR_FRAMESHIFT, AltTranscriptReportableInfoFunctions.firstOtherCodingEffect(example));
    }

    @Test
    public void worksOnEmptyString() {
        assertEquals(Strings.EMPTY, AltTranscriptReportableInfoFunctions.firstOtherTranscript(Strings.EMPTY));
        assertEquals(Strings.EMPTY, AltTranscriptReportableInfoFunctions.firstOtherHgvsCodingImpact(Strings.EMPTY));
        assertEquals(Strings.EMPTY, AltTranscriptReportableInfoFunctions.firstOtherHgvsProteinImpact(Strings.EMPTY));
        assertEquals(Strings.EMPTY, AltTranscriptReportableInfoFunctions.firstOtherEffects(Strings.EMPTY));
        assertEquals(PurpleCodingEffect.UNDEFINED, AltTranscriptReportableInfoFunctions.firstOtherCodingEffect(Strings.EMPTY));
    }
}