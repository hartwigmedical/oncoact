package com.hartwig.oncoact.patientreporter;

import com.hartwig.oncoact.common.genotype.GenotypeStatus;
import com.hartwig.oncoact.common.variant.CodingEffect;
import com.hartwig.oncoact.common.variant.Hotspot;
import com.hartwig.oncoact.common.variant.ImmutableReportableVariant;
import com.hartwig.oncoact.common.variant.ReportableVariant;
import com.hartwig.oncoact.common.variant.ReportableVariantSource;
import com.hartwig.oncoact.common.variant.VariantType;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class PatientReporterApplicationTest {

//    @Test
//    public void canConvertNaNDataToJson() {
//        AnalysedPatientReport patientReport = ExampleAnalysisTestFactory.createTestReport();
//
//        PatientReport nanReport = ImmutableAnalysedPatientReport.builder()
//                .from(patientReport)
//                .genomicAnalysis(ImmutableGenomicAnalysis.builder()
//                        .from(patientReport.genomicAnalysis())
//                        .reportableVariants(Lists.newArrayList(nanVariant()))
//                        .build())
//                .build();
//
//        assertNotNull(GenerateJsonOutput.convertToJson(nanReport));
//    }

    @NotNull
    private static ReportableVariant nanVariant() {
        return ImmutableReportableVariant.builder()
                .source(ReportableVariantSource.SOMATIC)
                .gene(Strings.EMPTY)
                .genotypeStatus(GenotypeStatus.UNKNOWN)
                .chromosome(Strings.EMPTY)
                .position(1)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .type(VariantType.SNP)
                .canonicalTranscript(Strings.EMPTY)
                .canonicalEffect(Strings.EMPTY)
                .canonicalCodingEffect(CodingEffect.MISSENSE)
                .canonicalHgvsCodingImpact(Strings.EMPTY)
                .canonicalHgvsProteinImpact(Strings.EMPTY)
                .alleleReadCount(0)
                .totalReadCount(1)
                .alleleCopyNumber(Double.NaN)
                .minorAlleleCopyNumber(Double.NaN)
                .totalCopyNumber(Double.NaN)
                .hotspot(Hotspot.HOTSPOT)
                .driverLikelihood(0D)
                .clonalLikelihood(0D)
                .biallelic(null)
                .build();
    }
}