package com.hartwig.oncoact.patientreporter.algo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.lims.LimsGermlineReportingLevel;
import com.hartwig.oncoact.orange.OrangeJson;
import com.hartwig.oncoact.orange.OrangeRecord;
import com.hartwig.oncoact.orange.purple.PurpleGenotypeStatus;
import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.PatientReporterTestFactory;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.ProtectEvidenceFile;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantSource;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class GenomicAnalyzerTest {

    @Test
    public void canRunOnTestRun() throws IOException {
        AnalysedReportData testReportData = PatientReporterTestFactory.loadTestAnalysedReportData();

        GenomicAnalyzer analyzer = new GenomicAnalyzer(testReportData.germlineReportingModel(), testReportData.knownFusionCache());

        PatientReporterConfig config = PatientReporterTestFactory.createTestReporterConfig();
        OrangeRecord orange = OrangeJson.read(config.orangeJson());
        List<ProtectEvidence> evidences = ProtectEvidenceFile.read(config.protectEvidenceTsv());

        assertNotNull(analyzer.run(orange, evidences, LimsGermlineReportingLevel.REPORT_WITH_NOTIFICATION));
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
    private static List<ReportableVariant> createTestReportableVariants(@NotNull String gene1, @NotNull PurpleGenotypeStatus genotypeStatus1,
            @Nullable Integer localPhaseSet1, @NotNull String gene2, @NotNull PurpleGenotypeStatus genotypeStatus2,
            @Nullable Integer localPhaseSet2) {
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
