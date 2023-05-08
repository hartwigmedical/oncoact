package com.hartwig.oncoact.patientreporter.lama;

import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class LamaInterpretationTest {

    @Test
    public void referenceIsEarliestDate() {
        LocalDate refDate = LocalDate.of(2023, 4, 6);
        LocalDate tumDate =LocalDate.of(2023, 4, 8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-LLL-yyyy");
        assertEquals(LamaInterpretation.extractEarliestArrivalDate(refDate, tumDate), refDate.format(formatter));
    }

    @Test
    public void tumorIsEarliestDate() {
        LocalDate refDate = LocalDate.of(2023, 4, 6);
        LocalDate tumDate =LocalDate.of(2023, 3, 8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-LLL-yyyy");
        assertEquals(LamaInterpretation.extractEarliestArrivalDate(refDate, tumDate), tumDate.format(formatter));
    }

    @Test
    public void extractHospitalDataStudy(){

        String studyPI = "studyPI";
        String requester = null;
        String hospital = "test center";
        String postalCode = "123";
        String city = "Amsterdam";
        String address = "test 1";
        String result = studyPI + ", " + hospital + ", " + address + ", " + postalCode + " " + city;

        assertEquals(result, LamaInterpretation.hospitalContactReport(studyPI, requester, hospital, postalCode, city, address));
    }

    @Test
    public void extractHospitalDataDiagnostic(){
        String studyPI = null;
        String requester = "requester";
        String hospital = "test center";
        String postalCode = "123";
        String city = "Amsterdam";
        String address = null;
        String result = requester + ", " + hospital + ", " + postalCode + " " + city;
        assertEquals(result, LamaInterpretation.hospitalContactReport(studyPI, requester, hospital, postalCode, city, address));
    }

    @Test
    public void extractHospitalDataMissingNameOnReport(){
        String studyPI = null;
        String requester = null;
        String hospital = "test center";
        String postalCode = "123";
        String city = "Amsterdam";
        String address = null;
        String result = ", " + hospital + ", " + postalCode + " " + city;
        assertEquals(result, LamaInterpretation.hospitalContactReport(studyPI, requester, hospital, postalCode, city, address));
    }

    @Test
    public void extractHospitalDataIncompleteHospitalDate(){
        String studyPI = null;
        String requester = "requester";
        String hospital = null;
        String postalCode = null;
        String city = null;
        String address = null;
        String result = requester + ", " + ",  ";
        assertEquals(result, LamaInterpretation.hospitalContactReport(studyPI, requester, hospital, postalCode, city, address));
    }

}