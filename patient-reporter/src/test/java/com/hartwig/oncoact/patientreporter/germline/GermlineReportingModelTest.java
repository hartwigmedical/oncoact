package com.hartwig.oncoact.patientreporter.germline;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.lims.LimsGermlineReportingLevel;
import com.hartwig.hmftools.datamodel.purple.PurpleGenotypeStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantType;
import com.hartwig.oncoact.patientreporter.PatientReporterTestFactory;
import com.hartwig.oncoact.patientreporter.algo.AnalysedReportData;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantSource;
import com.hartwig.oncoact.variant.TestReportableVariantFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class GermlineReportingModelTest {

    @Test
    public void canDetermineNotifyForGermlineVariants() {
        LimsGermlineReportingLevel germlineReportingLevel = LimsGermlineReportingLevel.REPORT_WITH_NOTIFICATION;
        AnalysedReportData testReportData = PatientReporterTestFactory.loadTestAnalysedReportData();

        ReportableVariant reportableVariant1 = createReportableVariant("MUTYH", PurpleGenotypeStatus.HOM_ALT);
        Set<String> germlineGenesWithIndependentHits1 = Sets.newHashSet();

        assertTrue(testReportData.germlineReportingModel()
                .notifyGermlineVariant(reportableVariant1, germlineReportingLevel, germlineGenesWithIndependentHits1));

        ReportableVariant reportableVariant = createReportableVariant("MUTYH", PurpleGenotypeStatus.UNKNOWN);
        Set<String> germlineGenesWithIndependentHits = Sets.newHashSet();

        assertFalse(testReportData.germlineReportingModel()
                .notifyGermlineVariant(reportableVariant, germlineReportingLevel, germlineGenesWithIndependentHits));

        assertFalse(testReportData.germlineReportingModel()
                .notifyGermlineVariant(reportableVariant,
                        LimsGermlineReportingLevel.REPORT_WITHOUT_NOTIFICATION,
                        germlineGenesWithIndependentHits));
    }

    @NotNull
    private static ReportableVariant createReportableVariant(@NotNull String gene, @NotNull PurpleGenotypeStatus genotypeStatus) {
        return TestReportableVariantFactory.builder()
                .type(PurpleVariantType.SNP)
                .source(ReportableVariantSource.GERMLINE)
                .gene(gene)
                .genotypeStatus(genotypeStatus)
                .build();
    }
}