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
}