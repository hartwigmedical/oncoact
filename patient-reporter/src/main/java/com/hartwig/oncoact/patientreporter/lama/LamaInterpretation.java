package com.hartwig.oncoact.patientreporter.lama;

import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

public class LamaInterpretation {

    private LamaInterpretation(){
    }

    public static String extractEarliestArrivalDate(@Nullable LocalDate refDate, @Nullable LocalDate sampleDate){
            if (sampleDate == null) {
            return null;
        } else if (refDate == null || sampleDate.isBefore(refDate)) {
            return Formats.formatDate(sampleDate);
        } else {
            return Formats.formatDate(refDate);
        }
    }
}
