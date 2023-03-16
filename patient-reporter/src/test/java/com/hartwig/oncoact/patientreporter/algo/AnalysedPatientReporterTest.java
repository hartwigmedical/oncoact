package com.hartwig.oncoact.patientreporter.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.time.LocalDate;

import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.PatientReporterTestFactory;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.reporting.ImmutableSampleMetadata;
import com.hartwig.oncoact.reporting.SampleMetadata;
import com.hartwig.oncoact.util.Formats;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class AnalysedPatientReporterTest {

    private static final String REF_SAMPLE_ID = "reference";
    private static final String TUMOR_SAMPLE_ID = "sample";

    private static final String REPORT_DATE = Formats.formatDate(LocalDate.now());

    @Test
    public void canRunOnRunDirectory() throws IOException {
        AnalysedPatientReporter reporter =
                new AnalysedPatientReporter(PatientReporterTestFactory.loadTestAnalysedReportData(), REPORT_DATE);
        PatientReporterConfig config = PatientReporterTestFactory.createTestReporterConfig();

        SampleMetadata sampleMetadata = ImmutableSampleMetadata.builder()
                .refSampleId(REF_SAMPLE_ID)
                .refSampleBarcode(Strings.EMPTY)
                .tumorSampleId(TUMOR_SAMPLE_ID)
                .tumorSampleBarcode(Strings.EMPTY)
                .sampleNameForReport(TUMOR_SAMPLE_ID)
                .build();

        assertNotNull(reporter.run(sampleMetadata, config));
    }

    @Test
    public void canDetermineForNumber() {
        double purityCorrect = 0.40;
        boolean hasReliablePurityCorrect = true;

        assertEquals(QsFormNumber.FOR_080.display(), AnalysedPatientReporter.determineForNumber(hasReliablePurityCorrect, purityCorrect));

        double purityNotCorrect = 0.10;
        boolean hasReliablePurityNotCorrect = false;

        assertEquals(QsFormNumber.FOR_209.display(),
                AnalysedPatientReporter.determineForNumber(hasReliablePurityNotCorrect, purityNotCorrect));
    }
}
