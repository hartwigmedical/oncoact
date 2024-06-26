package com.hartwig.oncoact.patientreporter.diagnosticsilo;

import static org.junit.Assert.assertEquals;

import com.hartwig.oncoact.diagnosticsilo.DiagnosticSiloJsonInterpretation;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class DiagnosticSiloJsonInterpretationTest {

    @Test
    public void extractPatientNameAllDataPresent() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");
        patientInformationData.setInitials("A.B");
        patientInformationData.setGender("Female");
        patientInformationData.setBirthdate("2023-11-02");
        patientInformationData.setSurname("Jong");
        patientInformationData.setBirthSurname("Jong-Oud");

        String result = patientInformationData.getInitials() + " " + patientInformationData.getSurname() + " ("
                + DiagnosticSiloJsonInterpretation.determineGenderDisplay(patientInformationData) + ")";
        assertEquals(result, DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
    }

    @Test
    public void extractPatientNameWithoutGender() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");
        patientInformationData.setInitials("A.B");
        patientInformationData.setBirthdate("2023-11-02");
        patientInformationData.setSurname("Jong");
        patientInformationData.setBirthSurname("Jong-Oud");

        String result = patientInformationData.getInitials() + " " + patientInformationData.getSurname() + " ";
        assertEquals(result, DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
    }

    @Test
    public void extractPatientNameNoBirthSurname() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");
        patientInformationData.setInitials("A.B");
        patientInformationData.setGender("Male");
        patientInformationData.setBirthdate("2023-11-02");
        patientInformationData.setSurname("Jong");

        String result = patientInformationData.getInitials() + " " + patientInformationData.getSurname() + " ("
                + DiagnosticSiloJsonInterpretation.determineGenderDisplay(patientInformationData) + ")";
        assertEquals(result, DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
    }

    @Test
    public void extractPatientNameNoSurname() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");
        patientInformationData.setInitials("A.B");
        patientInformationData.setGender("Female");
        patientInformationData.setBirthdate("2023-11-02");
        patientInformationData.setBirthSurname("Jong-Oud");

        String result = patientInformationData.getInitials() + " " + patientInformationData.getBirthSurname() + " ("
                + DiagnosticSiloJsonInterpretation.determineGenderDisplay(patientInformationData) + ")";
        assertEquals(result, DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
    }

    @Test
    public void extractPatientNameNoSurnameAndBirthSurname() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");
        patientInformationData.setInitials("A.B");
        patientInformationData.setGender("Female");
        patientInformationData.setBirthdate("2023-11-02");

        String result = patientInformationData.getInitials() + " (" + DiagnosticSiloJsonInterpretation.determineGenderDisplay(
                patientInformationData) + ")";
        assertEquals(result, DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
    }

    @Test
    public void extractPatientNameNoInitials() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");
        patientInformationData.setGender("Unknown");
        patientInformationData.setBirthdate("2023-11-02");
        patientInformationData.setSurname("Jong");

        String result = patientInformationData.getSurname();
        assertEquals(result + " ", DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
    }

    @Test
    public void extractNoPatientData() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");

        String result = Strings.EMPTY;
        assertEquals(result, DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
    }
}