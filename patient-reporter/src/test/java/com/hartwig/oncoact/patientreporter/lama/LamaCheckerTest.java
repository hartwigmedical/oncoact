package com.hartwig.oncoact.patientreporter.lama;

import org.junit.Test;

import java.time.LocalDate;

public class LamaCheckerTest {

    @Test
    public void checkArrivalDatesRefNull() {
        LocalDate refDate = null;
        LocalDate tumDate =LocalDate.of(2023, 4, 8);
        LamaChecker.lamaCheck(refDate, tumDate);
    }

    @Test
    public void checkArrivalDatesTumorNull() {
        LocalDate refDate = LocalDate.of(2023, 4, 8);
        LocalDate tumDate = null;
        LamaChecker.lamaCheck(refDate, tumDate);
    }

}