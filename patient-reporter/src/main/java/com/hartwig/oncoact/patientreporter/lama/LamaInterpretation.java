package com.hartwig.oncoact.patientreporter.lama;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.util.Formats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

public class LamaInterpretation {

    private static final Logger LOGGER = LogManager.getLogger(LamaInterpretation.class);

    private LamaInterpretation() {
    }

    public static String extractEarliestArrivalDate(@Nullable LocalDate refDate, @Nullable LocalDate sampleDate) {
        if (sampleDate == null) {
            return null;
        } else if (refDate == null || sampleDate.isBefore(refDate)) {
            return Formats.formatDate(sampleDate);
        } else {
            return Formats.formatDate(refDate);
        }
    }

    public static String hospitalContactReport(@Nullable PatientReporterData lamaPatientData) {
        String studyPI = lamaPatientData.getStudyPI();
        String requester = lamaPatientData.getRequesterName();
        String hospital = lamaPatientData.getHospitalName();
        String postalCode = lamaPatientData.getHospitalPostalCode();
        String city = lamaPatientData.getHospitalCity();
        String address = lamaPatientData.getHospitalAddress();

        String requesterNameReport = Strings.EMPTY;
        if (studyPI != null) {
            requesterNameReport = studyPI;
        } else if (requester != null) {
            requesterNameReport = requester;
        } else {
            LOGGER.warn("None requester name of report is known. Solve before reporting!");
        }

        if (hospital.equals(Strings.EMPTY)) {
            LOGGER.warn("Unknown hospital name is known");
        }

        if (postalCode.equals(Strings.EMPTY)) {
            LOGGER.warn("Unknown postal code of hospital known");
        }

        if (city.equals(Strings.EMPTY)) {
            LOGGER.warn("Unknown city of hospital known");
        }

        String hospitalAddress = hospital + ", " + postalCode + " " + city;
        if (address != null) {
            hospitalAddress = hospital + ", " + address + ", " + postalCode + " " + city;
        }

        return requesterNameReport + ", " + hospitalAddress;
    }
}