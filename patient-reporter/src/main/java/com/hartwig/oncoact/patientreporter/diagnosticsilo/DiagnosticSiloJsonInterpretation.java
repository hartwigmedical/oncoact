package com.hartwig.oncoact.patientreporter.diagnosticsilo;

import com.hartwig.silo.client.model.PatientInformationResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DiagnosticSiloJsonInterpretation {
    private static final Logger LOGGER = LogManager.getLogger(DiagnosticSiloJsonInterpretation.class);

    private DiagnosticSiloJsonInterpretation() {

    }

    @NotNull
    public static String determineName(@NotNull PatientInformationResponse patientInformationData) {
        String initials = nullToEmpty(patientInformationData.getInitials());
        if (initials.isEmpty()) {
            LOGGER.warn("Initials of the patient are unknown");
        }

        String surname = determineSurname(
                nullToEmpty(patientInformationData.getBirthSurname()), nullToEmpty(patientInformationData.getSurname()));
        if (surname.isEmpty()) {
            LOGGER.warn("Surname of the patient is unknown");
        }

        if (patientInformationData.getGender() != null) {
            return String.format("%s%s(%s)", initials, surname, patientInformationData.getGender());
        } else {
            return String.format("%s%s", initials, surname);
        }
    }

    @NotNull
    private static String nullToEmpty(@Nullable String str) {
        return str != null ? str + " " : Strings.EMPTY;
    }

    @NotNull
    private static String determineSurname(@NotNull String birthSurname, @NotNull String surname) {
        return !birthSurname.equals(Strings.EMPTY) ? birthSurname : surname;
    }
}