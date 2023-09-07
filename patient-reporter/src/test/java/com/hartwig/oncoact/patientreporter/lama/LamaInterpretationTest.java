package com.hartwig.oncoact.patientreporter.lama;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.hartwig.lama.client.model.PatientReporterData;

import org.junit.Test;

public class LamaInterpretationTest {

    @Test
    public void referenceIsEarliestDate() {
        LocalDate refDate = LocalDate.of(2023, 4, 6);
        LocalDate tumDate = LocalDate.of(2023, 4, 8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-LLL-yyyy");
        assertEquals(LamaInterpretation.extractEarliestArrivalDate(refDate, tumDate), refDate.format(formatter));
    }

    @Test
    public void tumorIsEarliestDate() {
        LocalDate refDate = LocalDate.of(2023, 4, 6);
        LocalDate tumDate = LocalDate.of(2023, 3, 8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-LLL-yyyy");
        assertEquals(LamaInterpretation.extractEarliestArrivalDate(refDate, tumDate), tumDate.format(formatter));
    }

    @Test
    public void extractHospitalDataStudy() {
        PatientReporterData patientReporterData = new PatientReporterData();
        patientReporterData.setIsStudy(true);
        patientReporterData.setStudyPI("studyPI");
        patientReporterData.setRequesterName(null);
        patientReporterData.setHospitalName("test center");
        patientReporterData.setOfficialHospitalName("test center");
        patientReporterData.setHospitalPostalCode("123");
        patientReporterData.setHospitalCity("Amsterdam");
        patientReporterData.setHospitalAddress("test 1");

        String result = patientReporterData.getStudyPI() + ", " + patientReporterData.getHospitalName() + ", "
                + patientReporterData.getHospitalAddress() + ", " + patientReporterData.getHospitalPostalCode() + " "
                + patientReporterData.getHospitalCity();
        assertEquals(result, LamaInterpretation.hospitalContactReport(patientReporterData));
    }

    @Test
    public void extractHospitalDataDiagnostic() {
        PatientReporterData patientReporterData = new PatientReporterData();
        patientReporterData.setIsStudy(false);
        patientReporterData.setStudyPI(null);
        patientReporterData.setRequesterName("requester");
        patientReporterData.setHospitalName("test center");
        patientReporterData.setOfficialHospitalName("test center");
        patientReporterData.setHospitalPostalCode("123");
        patientReporterData.setHospitalCity("Amsterdam");
        patientReporterData.setHospitalAddress(null);

        String result = patientReporterData.getRequesterName() + ", " + patientReporterData.getHospitalName() + ", "
                + patientReporterData.getHospitalPostalCode() + " " + patientReporterData.getHospitalCity();
        assertEquals(result, LamaInterpretation.hospitalContactReport(patientReporterData));
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractHospitalDataMissingNameOnReport() throws IllegalArgumentException {
        PatientReporterData patientReporterData = new PatientReporterData();
        patientReporterData.setIsStudy(false);
        patientReporterData.setStudyPI(null);
        patientReporterData.setRequesterName(null);
        patientReporterData.setHospitalName("test center");
        patientReporterData.setOfficialHospitalName("test center");
        patientReporterData.setHospitalPostalCode("123");
        patientReporterData.setHospitalCity("Amsterdam");
        patientReporterData.setHospitalAddress(null);
        
        LamaInterpretation.hospitalContactReport(patientReporterData);
    }
}