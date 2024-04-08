package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.model.Hospital;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

class HospitalCreator {
    private static final Logger LOGGER = LogManager.getLogger(HospitalCreator.class);

    static Hospital createHospital(
            @NotNull PatientReporterData lamaPatientData
    ) {
        return Hospital.builder()
                .name(lamaPatientData.getOfficialHospitalName())
                .requester(determineRequester(lamaPatientData))
                .reportAddress(determineReportAddress(lamaPatientData))
                .build();
    }

    @NotNull
    private static String determineRequester(@NotNull PatientReporterData lamaPatientData) {
        String requester = Strings.EMPTY;
        if (lamaPatientData.getIsStudy()) {
            if (lamaPatientData.getStudyPI() != null) {
                requester = lamaPatientData.getStudyPI();
            } else {
                LOGGER.warn("Missing study PI");
            }
        } else {
            if (lamaPatientData.getRequesterName() != null) {
                requester = lamaPatientData.getRequesterName();
            } else if (lamaPatientData.getStudyPI() != null) {
                requester = lamaPatientData.getStudyPI();
            } else {
                LOGGER.warn("Missing requester name");
            }
        }
        return requester;
    }

    public static String determineReportAddress(@NotNull PatientReporterData lamaPatientData) {
        String studyPI = lamaPatientData.getStudyPI();
        String requester = lamaPatientData.getRequesterName();
        String hospital = lamaPatientData.getOfficialHospitalName();
        String postalCode = lamaPatientData.getHospitalPostalCode();
        String city = lamaPatientData.getHospitalCity();
        String address = lamaPatientData.getHospitalAddress();

        String requesterNameReport = Strings.EMPTY;

        if (lamaPatientData.getIsStudy()) {
            if (studyPI != null) {
                requesterNameReport = studyPI;
            } else {
                throw new IllegalArgumentException("None study PI name of report is known. Solve before reporting!");
            }
        } else {
            if (requester != null) {
                requesterNameReport = requester;
            } else if (studyPI != null) {
                requesterNameReport = studyPI;
            } else {
                throw new IllegalArgumentException("None requester name of report is known. Solve before reporting!");
            }
        }

        if (hospital.equals(Strings.EMPTY)) {
            throw new IllegalArgumentException("Unknown hospital name is known");
        }

        if (postalCode.equals(Strings.EMPTY)) {
            throw new IllegalArgumentException("Unknown postal code of hospital known");
        }

        if (city.equals(Strings.EMPTY)) {
            throw new IllegalArgumentException("Unknown city of hospital known");
        }

        String hospitalAddress = hospital + ", " + postalCode + " " + city;
        if (address != null) {
            hospitalAddress = hospital + ", " + address + ", " + postalCode + " " + city;
        }

        return requesterNameReport + ", " + hospitalAddress;
    }
}