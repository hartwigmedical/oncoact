package com.hartwig.oncoact.protect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.purple.ImmutablePurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.CopyNumberInterpretation;
import com.hartwig.hmftools.datamodel.purple.PurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;
import com.hartwig.hmftools.datamodel.purple.Variant;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class EventGeneratorTest {

    @Test
    public void canTestToVariantEvent() {
        assertEquals("p.Gly12Cys",
                EventGenerator.toVariantEvent("p.Gly12Cys", "c.123A>C", "missense_variant", PurpleCodingEffect.MISSENSE));
        assertEquals("c.123A>C splice", EventGenerator.toVariantEvent("p.?", "c.123A>C", "missense_variant", PurpleCodingEffect.SPLICE));
        assertEquals("c.123A>C", EventGenerator.toVariantEvent("", "c.123A>C", "missense_variant", PurpleCodingEffect.MISSENSE));
        assertEquals("splice", EventGenerator.toVariantEvent("", "", "splice", PurpleCodingEffect.SPLICE));
        assertEquals("missense_variant", EventGenerator.toVariantEvent("", "", "missense_variant", PurpleCodingEffect.MISSENSE));
    }

    @NotNull
    private static PurpleTranscriptImpact purpleTranscriptImpact(@NotNull String transcript, @NotNull String hgvsCodingImpact,
                                                                 @NotNull String hgvsProteinImpact) {
        return ImmutablePurpleTranscriptImpact.builder()
                .transcript(transcript)
                .hgvsCodingImpact(hgvsCodingImpact)
                .hgvsProteinImpact(hgvsProteinImpact)
                .spliceRegion(false)
                .codingEffect(PurpleCodingEffect.UNDEFINED)
                .build();
    }

    @NotNull
    private static ReportableVariant generateReportableVariant(@NotNull String transcript, @NotNull String hgvsCodingImpact,
                                                                 @NotNull String hgvsProteinImpact, boolean isCanonical) {
        return TestReportableVariantFactory.builder()
                .isCanonical(isCanonical)
                .canonicalHgvsCodingImpact("coding")
                .otherImpactClinical(purpleTranscriptImpact(transcript, hgvsCodingImpact, hgvsProteinImpact))
                .otherReportedEffects(createAltTranscriptInfo())
                .build();
    }

    @NotNull
    private static ReportableVariant generateReportableVariant(boolean isCanonical) {
        return TestReportableVariantFactory.builder()
                .isCanonical(isCanonical)
                .canonicalHgvsCodingImpact("coding")
                .otherImpactClinical(null)
                .otherReportedEffects(createAltTranscriptInfo())
                .build();
    }

    @Test
    public void canGenerateEventForReportableVariantWithClinicalTranscriptProtein() {
        ReportableVariant base = generateReportableVariant("transcript", "coding", "protein", false);
        assertEquals("coding (protein)", EventGenerator.variantEvent(base));
        assertNotNull(EventGenerator.variantEvent(base));
    }

    @Test
    public void canGenerateEventForReportableVariantWithClinicalTranscriptCoding() {
        ReportableVariant base = generateReportableVariant("transcript", "coding", Strings.EMPTY, false);
        assertEquals("coding (coding)", EventGenerator.variantEvent(base));
        assertNotNull(EventGenerator.variantEvent(base));
    }

    @Test
    public void canGenerateEventForReportableVariant() {
        ReportableVariant base = generateReportableVariant(true);
        assertEquals("coding", EventGenerator.variantEvent(base));
        assertNotNull(EventGenerator.variantEvent(base));
    }

    @NotNull
    private static String createAltTranscriptInfo() {
        return "trans|coding||effect|MISSENSE";
    }

    @Test
    public void canGenerateEventForVariant() {
        Variant base = TestPurpleFactory.variantBuilder()
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder().addEffects(PurpleVariantEffect.MISSENSE).build())
                .build();
        assertEquals("missense", EventGenerator.variantEvent(base));

        Variant coding = TestPurpleFactory.variantBuilder()
                .from(base)
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder().hgvsCodingImpact("coding impact").build())
                .build();
        assertEquals("coding impact", EventGenerator.variantEvent(coding));

        Variant protein = TestPurpleFactory.variantBuilder()
                .from(base)
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder().hgvsProteinImpact("protein impact").build())
                .build();
        assertEquals("protein impact", EventGenerator.variantEvent(protein));
    }

    @Test
    public void canGenerateEventForGainLoss() {
        PurpleGainLoss gainLoss = TestPurpleFactory.gainLossBuilder().interpretation(CopyNumberInterpretation.FULL_LOSS).build();
        assertEquals("full loss", EventGenerator.gainLossEvent(gainLoss));
    }

    @Test
    public void canGenerateEventForFusion() {
        LinxFusion fusion = TestLinxFactory.fusionBuilder().geneStart("start").geneEnd("end").build();
        assertEquals("start - end fusion", EventGenerator.fusionEvent(fusion));
    }
}