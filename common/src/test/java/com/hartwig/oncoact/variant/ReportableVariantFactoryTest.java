package com.hartwig.oncoact.variant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleDriver;
import com.hartwig.hmftools.datamodel.purple.PurpleDriverType;
import com.hartwig.hmftools.datamodel.purple.PurpleVariant;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;
import com.hartwig.oncoact.clinicaltransript.ClinicalTranscriptModelTestFactory;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ReportableVariantFactoryTest {

    private static final double EPSILON = 1.0E-10;

    @Test
    public void canResolveReportableSomaticVariants() {
        PurpleVariant variant1 = TestPurpleFactory.variantBuilder()
                .reported(true)
                .gene("gene 1")
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder().transcript("transcript 1").build())
                .build();
        PurpleVariant variant2 = TestPurpleFactory.variantBuilder()
                .reported(false)
                .gene("gene 2")
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder().transcript("transcript 2").build())
                .build();
        Set<PurpleVariant> variants = Sets.newHashSet(variant1, variant2);

        double likelihood = 0.6;
        PurpleDriver driverGene1 = TestPurpleFactory.driverBuilder()
                .gene("gene 1")
                .transcript("transcript 1")
                .driverLikelihood(0.6)
                .type(PurpleDriverType.MUTATION)
                .build();

        Set<ReportableVariant> reportable = ReportableVariantFactory.toReportableSomaticVariants(variants,
                Sets.newHashSet(driverGene1),
                ClinicalTranscriptModelTestFactory.createEmpty());

        assertEquals(1, reportable.size());
        assertEquals(likelihood, reportable.iterator().next().driverLikelihood(), EPSILON);
    }

    @Test
    public void canResolveGermlineVariantsWithMultipleDrivers() {
        PurpleVariant variant1 = TestPurpleFactory.variantBuilder()
                .reported(true)
                .gene("gene")
                .variantCopyNumber(0.6)
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder().transcript("transcript 1").build())
                .build();

        PurpleDriver driver1 = TestPurpleFactory.driverBuilder()
                .gene("gene")
                .driverLikelihood(0.6)
                .transcript("transcript 1")
                .type(PurpleDriverType.GERMLINE_MUTATION)
                .build();
        PurpleDriver driver2 =
                TestPurpleFactory.driverBuilder().from(driver1).driverLikelihood(1D).type(PurpleDriverType.GERMLINE_DELETION).build();
        Set<PurpleDriver> drivers = Sets.newHashSet(driver1, driver2);

        Set<ReportableVariant> reportable = ReportableVariantFactory.toReportableGermlineVariants(Sets.newHashSet(variant1),
                drivers,
                ClinicalTranscriptModelTestFactory.createEmpty());

        assertEquals(0.6, reportable.iterator().next().driverLikelihood(), EPSILON);
    }

    @Test
    public void canResolveGermlineVariantsOnly() {
        PurpleVariant variant1 = TestPurpleFactory.variantBuilder()
                .reported(true)
                .gene("gene")
                .variantCopyNumber(0.4)
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder().transcript("transcript 1").build())
                .build();

        PurpleDriver driver1 = TestPurpleFactory.driverBuilder()
                .gene("gene")
                .driverLikelihood(0.6)
                .transcript("transcript 1")
                .type(PurpleDriverType.GERMLINE_MUTATION)
                .build();
        PurpleDriver driver2 =
                TestPurpleFactory.driverBuilder().from(driver1).driverLikelihood(1D).type(PurpleDriverType.GERMLINE_DELETION).build();
        Set<PurpleDriver> drivers = Sets.newHashSet(driver1, driver2);

        Set<ReportableVariant> reportable = ReportableVariantFactory.toReportableGermlineVariants(Sets.newHashSet(variant1),
                drivers,
                ClinicalTranscriptModelTestFactory.createEmpty());

        assertNull(reportable.iterator().next().driverLikelihood());
    }

    @Test
    public void canResolveReportableFromNonCanonicalDrivers() {
        PurpleVariant variant = TestPurpleFactory.variantBuilder()
                .reported(true)
                .gene("gene")
                .canonicalImpact(TestPurpleFactory.transcriptImpactBuilder().transcript("transcript 1").build())
                .addOtherImpacts(TestPurpleFactory.transcriptImpactBuilder()
                        .transcript("transcript 2")
                        .hgvsCodingImpact("c.246_247delCG")
                        .hgvsProteinImpact("p.Gly83fs")
                        .addEffects(PurpleVariantEffect.FRAMESHIFT)
                        .codingEffect(PurpleCodingEffect.NONSENSE_OR_FRAMESHIFT)
                        .build())
                .build();

        PurpleDriver driverNonCanonical =
                TestPurpleFactory.driverBuilder().gene("gene").transcript("transcript 2").driverLikelihood(0.6).isCanonical(false).build();

        Set<ReportableVariant> reportable = ReportableVariantFactory.toReportableSomaticVariants(Sets.newHashSet(variant),
                Sets.newHashSet(driverNonCanonical),
                ClinicalTranscriptModelTestFactory.createEmpty());

        assertEquals(1, reportable.size());
        assertEquals(0.6, reportable.iterator().next().driverLikelihood(), EPSILON);

        PurpleDriver driverCanonical =
                TestPurpleFactory.driverBuilder().gene("gene").transcript("transcript 1").driverLikelihood(0.7).build();
        Set<ReportableVariant> reportables = ReportableVariantFactory.toReportableSomaticVariants(Sets.newHashSet(variant),
                Sets.newHashSet(driverNonCanonical, driverCanonical),
                ClinicalTranscriptModelTestFactory.createEmpty());

        assertEquals(2, reportables.size());
        ReportableVariant reportable1 = findByTranscript(reportables, "transcript 1");
        assertEquals(0.7, reportable1.driverLikelihood(), EPSILON);
        ReportableVariant reportable2 = findByTranscript(reportables, "transcript 2");
        assertEquals(0.6, reportable2.driverLikelihood(), EPSILON);
    }

    @NotNull
    private static ReportableVariant findByTranscript(@NotNull Iterable<ReportableVariant> reportables, @NotNull String transcriptToFind) {
        for (ReportableVariant reportable : reportables) {
            if (reportable.transcript().equals(transcriptToFind)) {
                return reportable;
            }
        }

        throw new IllegalStateException("Could not find reportable variant with transcript: " + transcriptToFind);
    }
}