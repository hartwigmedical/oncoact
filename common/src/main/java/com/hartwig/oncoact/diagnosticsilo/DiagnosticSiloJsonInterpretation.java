package com.hartwig.oncoact.diagnosticsilo;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiagnosticSiloJsonInterpretation {
    private static final Logger LOGGER = LogManager.getLogger(DiagnosticSiloJsonInterpretation.class);

    private DiagnosticSiloJsonInterpretation() {

    }

    @Nullable
    @VisibleForTesting
    public static String determineGenderDisplay(@NotNull PatientInformationResponse patientInformationData) {
        String genderInterpret = null;
        String gender = patientInformationData.getGender();
        if (gender != null) {
            if (gender.equalsIgnoreCase("female")) {
                genderInterpret = "F";
            } else if (gender.equalsIgnoreCase("male")) {
                genderInterpret = "M";
            }
        }
        return genderInterpret;
    }

    @NotNull
    public static String determineName(@NotNull PatientInformationResponse patientInformationData) {
        String initials = nullToEmpty(patientInformationData.getInitials());
        if (initials.isEmpty()) {
            LOGGER.warn("Initials of the patient are unknown");
        }

        String surname =
                determineSurname(nullToEmpty(patientInformationData.getBirthSurname()), nullToEmpty(patientInformationData.getSurname()));
        if (surname.isEmpty()) {
            LOGGER.warn("Surname of the patient is unknown");
        }

        if (patientInformationData.getGender() != null) {
            String gender = determineGenderDisplay(patientInformationData);

            if (gender != null) {
                return String.format("%s%s(%s)", initials, surname, gender);
            }
        }
        return String.format("%s%s", initials, surname);

    }

    @NotNull
    private static String nullToEmpty(@Nullable String str) {
        return str != null ? str + " " : Strings.EMPTY;
    }

    @NotNull
    private static String determineSurname(@NotNull String birthSurname, @NotNull String surname) {
        return surname.equals(Strings.EMPTY) ? birthSurname : surname;
    }
}