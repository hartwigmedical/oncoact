package com.hartwig.oncoact.patientreporter.diagnosticsilo;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.oncoact.diagnosticsilo.DiagnosticSiloJson;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class DiagnosticSiloJsonTest {

    private static final String DIAGNOSTIC_SILO_JSON = Resources.getResource("silo/sample.silo.json").getPath();

    @NotNull
    public static PatientInformationResponse canReadDiagnosticSiloJsonEmpty() throws IOException {
        return DiagnosticSiloJson.read(DIAGNOSTIC_SILO_JSON);
    }

    @Test
    public void canReadLamaJson() throws IOException {
        PatientInformationResponse diagnosticSiloPatientData = canReadDiagnosticSiloJsonEmpty();
        assertEquals(diagnosticSiloPatientData.getHospitalName(), "hospitalName");
        assertEquals(diagnosticSiloPatientData.getReportingId(), "reportingId");
        assertEquals(diagnosticSiloPatientData.getInitials(), "initials");
        assertEquals(diagnosticSiloPatientData.getGender(), "female");
        assertEquals(diagnosticSiloPatientData.getSurname(), "surname");
        assertEquals(diagnosticSiloPatientData.getBirthdate(), "2023-08-04");
        assertEquals(diagnosticSiloPatientData.getBirthSurname(), "birthSurname");
    }
}