package com.hartwig.oncoact.patientreporter.lama;

import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;
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

    public static String hospitalContactReport(@Nullable String studyPI, @Nullable String requester, @Nullable String hospital,
                                               @Nullable String postalCode, @Nullable String city, @Nullable String adres) {
        //TODO; implement hospital contact data correctly
        String mainRequester = requester;
        String hospitalAdres = hospital + ", " + postalCode + ", " + city;
        return mainRequester + ", " + hospitalAdres;
    }
}
