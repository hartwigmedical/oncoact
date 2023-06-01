package com.hartwig.oncoact.patientreporter.lama;

import com.hartwig.lama.client.model.TumorType;
import com.hartwig.oncoact.patientreporter.PanelReporterApplication;
import com.hartwig.oncoact.util.Formats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

public class LamaInterpretation {

    private static final Logger LOGGER = LogManager.getLogger(LamaInterpretation.class);

    private LamaInterpretation(){
    }

    public static InterpretTumorType interpretTumorType(@Nullable TumorType tumorType) {
        String location;
        String type;

        if (tumorType != null) {
            location = tumorType.getLocation();
            type = tumorType.getType();

            assert location != null;
            assert type != null;
        } else {
            location = Strings.EMPTY;
            type = Strings.EMPTY;
        }
        return ImmutableInterpretTumorType.builder().location(location).type(type).build();
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
                                               @Nullable String postalCode, @Nullable String city, @Nullable String address) {
        String requesterNameReport = Strings.EMPTY;
        if (studyPI!= null) {
            requesterNameReport = studyPI;
        } else if (requester != null) {
            requesterNameReport = requester;
        } else {
            LOGGER.warn("None requester name of report is known. Solve before reporting!");
        }

        String hospitalString= Strings.EMPTY;
        String postalCodeString= Strings.EMPTY;
        String cityString= Strings.EMPTY;

        if (hospital != null) {
            hospitalString = hospital;
        } else {
            LOGGER.warn("Unknown hospital name is known");
        }

        if (postalCode != null) {
            postalCodeString = postalCode;
        } else {
            LOGGER.warn("Unknown postal code of hospital known");
        }

        if (city != null) {
            cityString = city;
        } else {
            LOGGER.warn("Unknown city of hospital known");
        }

        String hospitalAddress = hospitalString + ", " + postalCodeString + " " + cityString;
        if (address != null) {
            hospitalAddress = hospitalString + ", " + address + ", " + postalCodeString +" " + cityString;
        }

        return requesterNameReport + ", " + hospitalAddress;
    }
}
