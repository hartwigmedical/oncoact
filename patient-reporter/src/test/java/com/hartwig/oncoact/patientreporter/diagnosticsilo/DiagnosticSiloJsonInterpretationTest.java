package com.hartwig.oncoact.patientreporter.diagnosticsilo;

import com.hartwig.silo.client.model.PatientInformationResponse;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiagnosticSiloJsonInterpretationTest {

    @Test
    public void extractPatientNameAllDataPresent() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");
        patientInformationData.setInitials("A.B");
        patientInformationData.setGender("V");
        patientInformationData.setBirthdate("2023-11-02");
        patientInformationData.setSurname("Jong");
        patientInformationData.setBirthSurname("Jong-Oud");

        String result = patientInformationData.getInitials() + " " + patientInformationData.getBirthSurname() + " (" + patientInformationData.getGender() + ")";
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

        String result = patientInformationData.getInitials() + " " + patientInformationData.getBirthSurname() + " ";
        assertEquals(result, DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
    }


    @Test
    public void extractPatientNameNoBirthSurname() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");
        patientInformationData.setInitials("A.B");
        patientInformationData.setGender("V");
        patientInformationData.setBirthdate("2023-11-02");
        patientInformationData.setSurname("Jong");

        String result = patientInformationData.getInitials() + " " + patientInformationData.getSurname() + " (" + patientInformationData.getGender() + ")";
        assertEquals(result, DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
    }

    @Test
    public void extractPatientNameNoSurname() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");
        patientInformationData.setInitials("A.B");
        patientInformationData.setGender("V");
        patientInformationData.setBirthdate("2023-11-02");
        patientInformationData.setBirthSurname("Jong-Oud");

        String result = patientInformationData.getInitials() + " " + patientInformationData.getBirthSurname() + " (" + patientInformationData.getGender() + ")";
        assertEquals(result, DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
    }

    @Test
    public void extractPatientNameNoSurnameAndBirthSurname() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");
        patientInformationData.setInitials("A.B");
        patientInformationData.setGender("V");
        patientInformationData.setBirthdate("2023-11-02");

        String result = patientInformationData.getInitials() + " (" + patientInformationData.getGender() + ")";
        assertEquals(result, DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
    }

    @Test
    public void extractPatientNameNoInitials() {
        PatientInformationResponse patientInformationData = new PatientInformationResponse();
        patientInformationData.setHospitalName("hospital");
        patientInformationData.setReportingId("123");
        patientInformationData.setGender("V");
        patientInformationData.setBirthdate("2023-11-02");
        patientInformationData.setSurname("Jong");

        String result = patientInformationData.getSurname() + " (" + patientInformationData.getGender() + ")";
        assertEquals(result, DiagnosticSiloJsonInterpretation.determineName(patientInformationData));
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