package com.hartwig.oncoact.patientreporter.diagnosticsilo;

import com.google.common.io.Resources;
import com.hartwig.silo.client.model.PatientInformationResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

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
        assertEquals(diagnosticSiloPatientData.getGender(), "gender");
        assertEquals(diagnosticSiloPatientData.getSurname(), "surname");
        assertEquals(diagnosticSiloPatientData.getBirthdate(), "birthdate");
        assertEquals(diagnosticSiloPatientData.getBirthSurname(), "birthSurname");
        assertEquals(diagnosticSiloPatientData.getPostalCode(), "postalCode");
    }
}