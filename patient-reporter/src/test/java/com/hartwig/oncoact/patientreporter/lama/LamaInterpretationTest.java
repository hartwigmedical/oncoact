package com.hartwig.oncoact.patientreporter.lama;

import junit.framework.TestCase;
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
}