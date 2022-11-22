package com.hartwig.oncoact.patientreporter.germline;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.common.genotype.GenotypeStatus;
import com.hartwig.oncoact.common.lims.LimsGermlineReportingLevel;
import com.hartwig.oncoact.common.variant.ImmutableReportableVariant;
import com.hartwig.oncoact.common.variant.ReportableVariant;
import com.hartwig.oncoact.common.variant.ReportableVariantSource;
import com.hartwig.oncoact.common.variant.ReportableVariantTestFactory;
import com.hartwig.oncoact.common.variant.VariantType;
import com.hartwig.oncoact.patientreporter.PatientReporterTestFactory;
import com.hartwig.oncoact.patientreporter.algo.AnalysedReportData;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class GermlineReportingModelTest {

    @Test
    public void canDetermineNotifyForGermlineVariants() {
        LimsGermlineReportingLevel germlineReportingLevel = LimsGermlineReportingLevel.REPORT_WITH_NOTIFICATION;
        AnalysedReportData testReportData = PatientReporterTestFactory.loadTestAnalysedReportData();

        ReportableVariant reportableVariant1 = testReportableVariant("MUTYH", GenotypeStatus.HOM_ALT);
        Set<String> germlineGenesWithIndependentHits1 = Sets.newHashSet();

        assertTrue(testReportData.germlineReportingModel()
                .notifyGermlineVariant(reportableVariant1, germlineReportingLevel, germlineGenesWithIndependentHits1));

        ReportableVariant reportableVariant = testReportableVariant("MUTYH", GenotypeStatus.UNKNOWN);
        Set<String> germlineGenesWithIndependentHits = Sets.newHashSet();

        assertFalse(testReportData.germlineReportingModel()
                .notifyGermlineVariant(reportableVariant, germlineReportingLevel, germlineGenesWithIndependentHits));

        assertFalse(testReportData.germlineReportingModel()
                .notifyGermlineVariant(reportableVariant,
                        LimsGermlineReportingLevel.REPORT_WITHOUT_NOTIFICATION,
                        germlineGenesWithIndependentHits));
    }

    @NotNull
    private static ReportableVariant testReportableVariant(@NotNull String gene, @NotNull GenotypeStatus genotypeStatus) {
        return ImmutableReportableVariant.builder()
                .from(ReportableVariantTestFactory.create())
                .type(VariantType.SNP)
                .source(ReportableVariantSource.GERMLINE)
                .gene(gene)
                .genotypeStatus(genotypeStatus)
                .build();
    }
}