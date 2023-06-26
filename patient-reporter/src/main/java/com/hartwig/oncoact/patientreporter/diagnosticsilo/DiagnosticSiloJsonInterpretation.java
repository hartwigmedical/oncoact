package com.hartwig.oncoact.patientreporter.diagnosticsilo;

import com.hartwig.silo.client.model.PatientInformationResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class DiagnosticSiloJsonInterpretation {
    private static final Logger LOGGER = LogManager.getLogger(DiagnosticSiloJsonInterpretation.class);

    private DiagnosticSiloJsonInterpretation() {

    }

    @NotNull
    public static String determineName(@NotNull PatientInformationResponse patientInformationData) {
        String gender = patientInformationData.getGender() != null ? patientInformationData.getGender() : Strings.EMPTY;
        String initials = patientInformationData.getInitials() != null ? patientInformationData.getInitials() + " " : Strings.EMPTY;
        if (initials.equals(Strings.EMPTY)) {
            LOGGER.warn("None initials of the patient is known");
        }

        String surname = patientInformationData.getSurname() != null ? patientInformationData.getSurname() + " " : Strings.EMPTY;
        String birthSurname = patientInformationData.getBirthSurname() != null ? patientInformationData.getBirthSurname() + " " : Strings.EMPTY;

        String interpretSurname = !birthSurname.equals(Strings.EMPTY) ? birthSurname : surname;
        if (interpretSurname.equals(Strings.EMPTY)) {
            LOGGER.warn("None surname of the patient is known");
        }

        String name;
        if (!gender.isEmpty()) {
            name = initials + interpretSurname + "(" + gender + ")";
        } else {
            name = initials + interpretSurname;
        }
        return name;
    }
}