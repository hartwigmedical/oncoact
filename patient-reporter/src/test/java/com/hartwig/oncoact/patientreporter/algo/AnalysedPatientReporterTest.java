package com.hartwig.oncoact.patientreporter.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.PatientReporterTestFactory;
import com.hartwig.oncoact.patientreporter.QsFormNumber;

import org.junit.Test;

public class AnalysedPatientReporterTest {

    @Test
    public void canRunOnRunDirectory() throws IOException {
        AnalysedPatientReporter reporter = new AnalysedPatientReporter(PatientReporterTestFactory.loadTestAnalysedReportData());
        PatientReporterConfig config = PatientReporterTestFactory.createTestReporterConfig();

        assertNotNull(reporter.run(config));
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
