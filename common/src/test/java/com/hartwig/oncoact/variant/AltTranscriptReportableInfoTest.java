package com.hartwig.oncoact.variant;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.hartwig.oncoact.orange.purple.PurpleCodingEffect;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class AltTranscriptReportableInfoTest {

    @Test
    public void canExtractFromOtherEffects() {
        String example = "ENST00000579755|c.246_247delCG|p.Gly83fs|frameshift_variant|NONSENSE_OR_FRAMESHIFT";

        assertEquals("ENST00000579755", AltTranscriptReportableInfo.firstOtherTranscript(example));
        assertEquals("c.246_247delCG", AltTranscriptReportableInfo.firstOtherHgvsCodingImpact(example));
        assertEquals("p.Gly83fs", AltTranscriptReportableInfo.firstOtherHgvsProteinImpact(example));
        assertEquals("frameshift_variant", AltTranscriptReportableInfo.firstOtherEffects(example));
        assertEquals(PurpleCodingEffect.NONSENSE_OR_FRAMESHIFT, AltTranscriptReportableInfo.firstOtherCodingEffect(example));
    }

    @Test
    public void canExtractFromMultipleOtherEffects() {
        AltTranscriptReportableInfo altInfo1 = new AltTranscriptReportableInfo("ENST00000579755",
                "c.194-3_194-2delCA",
                "p.?",
                "splice_acceptor_variant&intron_variant",
                PurpleCodingEffect.SPLICE);

        AltTranscriptReportableInfo altInfo2 = new AltTranscriptReportableInfo("ENST00000123456",
                "c.194-3_194-2delCA",
                "p.?",
                "splice_acceptor_variant&intron_variant",
                PurpleCodingEffect.NONSENSE_OR_FRAMESHIFT);

        String example = altInfo1.serialise() + AltTranscriptReportableInfo.VAR_IMPACT_OTHER_REPORT_DELIM + altInfo2.serialise();

        List<AltTranscriptReportableInfo> altTransInfos = AltTranscriptReportableInfo.parseAltTranscriptInfo(example);
        assertEquals(2, altTransInfos.size());
        assertEquals(altTransInfos.get(0).TransName, altInfo1.TransName);
        assertEquals(altTransInfos.get(0).Effect, altInfo1.Effect);

        assertEquals(altTransInfos.get(1).TransName, altInfo2.TransName);
        assertEquals(altTransInfos.get(1).Effect, altInfo2.Effect);
    }

    @Test
    public void worksOnEmptyString() {
        assertEquals(Strings.EMPTY, AltTranscriptReportableInfo.firstOtherTranscript(Strings.EMPTY));
        assertEquals(Strings.EMPTY, AltTranscriptReportableInfo.firstOtherHgvsCodingImpact(Strings.EMPTY));
        assertEquals(Strings.EMPTY, AltTranscriptReportableInfo.firstOtherHgvsProteinImpact(Strings.EMPTY));
        assertEquals(Strings.EMPTY, AltTranscriptReportableInfo.firstOtherEffects(Strings.EMPTY));
        assertEquals(PurpleCodingEffect.UNDEFINED, AltTranscriptReportableInfo.firstOtherCodingEffect(Strings.EMPTY));
    }
}