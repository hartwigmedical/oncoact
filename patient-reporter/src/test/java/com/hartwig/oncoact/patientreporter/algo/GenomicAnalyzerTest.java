package com.hartwig.oncoact.patientreporter.algo;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.PurpleGenotypeStatus;
import com.hartwig.oncoact.clinicaltransript.ClinicalTranscriptModelTestFactory;
import com.hartwig.oncoact.clinicaltransript.ClinicalTranscriptsModel;
import com.hartwig.oncoact.orange.TestOrangeFactory;
import com.hartwig.oncoact.patientreporter.germline.GermlineReportingModel;
import com.hartwig.oncoact.patientreporter.germline.TestGermlineReportingModelFactory;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantSource;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GenomicAnalyzerTest {

    @Test
    public void canRunOnExampleData() {
        GermlineReportingModel testGermlineReportingModel = TestGermlineReportingModelFactory.createEmpty();
        ClinicalTranscriptsModel testClinicalTranscriptsModel = ClinicalTranscriptModelTestFactory.createEmpty();
        GenomicAnalyzer analyzer = new GenomicAnalyzer(testGermlineReportingModel, testClinicalTranscriptsModel);

        List<ProtectEvidence> noEvidences = Lists.newArrayList();

        assertNotNull(analyzer.run(TestOrangeFactory.createMinimalTestOrangeRecord(),
                noEvidences,
                true));

        assertNotNull(analyzer.run(TestOrangeFactory.createProperTestOrangeRecord(),
                noEvidences,
                true));
    }

    @Test
    public void canTestHasOtherGermlineVariantWithDifferentPhaseSet() {
        List<ReportableVariant> reportableVariants1 =
                createTestReportableVariants("MUTYH", PurpleGenotypeStatus.HET, null, "MUTYH", PurpleGenotypeStatus.HET, null);
        ReportableVariant reportableVariantToCompare1 = createTestReportableVariant("MUTYH", PurpleGenotypeStatus.HET, null);
        assertFalse(GenomicAnalyzer.hasOtherGermlineVariantWithDifferentPhaseSet(reportableVariants1, reportableVariantToCompare1));

        List<ReportableVariant> reportableVariants2 =
                createTestReportableVariants("MUTYH", PurpleGenotypeStatus.HET, null, "MUTYH", PurpleGenotypeStatus.HET, 123);
        ReportableVariant reportableVariantToCompare2 = createTestReportableVariant("MUTYH", PurpleGenotypeStatus.HET, 123);
        assertTrue(GenomicAnalyzer.hasOtherGermlineVariantWithDifferentPhaseSet(reportableVariants2, reportableVariantToCompare2));
    }

    @NotNull
    private static List<ReportableVariant> createTestReportableVariants(@NotNull String gene1,
                                                                        @NotNull PurpleGenotypeStatus genotypeStatus1, @Nullable Integer localPhaseSet1, @NotNull String gene2,
                                                                        @NotNull PurpleGenotypeStatus genotypeStatus2, @Nullable Integer localPhaseSet2) {
        ReportableVariant variant1 = TestReportableVariantFactory.builder()
                .source(ReportableVariantSource.GERMLINE)
                .gene(gene1)
                .genotypeStatus(genotypeStatus1)
                .localPhaseSet(localPhaseSet1)
                .build();

        ReportableVariant variant2 = TestReportableVariantFactory.builder()
                .source(ReportableVariantSource.GERMLINE)
                .gene(gene2)
                .genotypeStatus(genotypeStatus2)
                .localPhaseSet(localPhaseSet2)
                .build();

        return Lists.newArrayList(variant1, variant2);
    }

    @NotNull
    private static ReportableVariant createTestReportableVariant(@NotNull String gene, @NotNull PurpleGenotypeStatus genotypeStatus,
                                                                 @Nullable Integer localPhaseSet) {
        return TestReportableVariantFactory.builder()
                .source(ReportableVariantSource.GERMLINE)
                .gene(gene)
                .genotypeStatus(genotypeStatus)
                .localPhaseSet(localPhaseSet)
                .build();
    }
}
