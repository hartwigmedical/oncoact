package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.oncoact.patientreporter.model.Gender;
import com.hartwig.oncoact.patientreporter.model.Patient;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class PatientCreater {

    private static final Logger LOGGER = LogManager.getLogger(PatientCreater.class);

    static Patient createPatient(
            @NotNull PatientInformationResponse diagnosticSiloPatientData
    ) {

        return Patient.builder()
                .name(determineName(diagnosticSiloPatientData))
                .gender(diagnosticSiloPatientData.getGender() == null ? null : getGender(diagnosticSiloPatientData.getGender()))
                .birthDate(determineBirthDate(diagnosticSiloPatientData.getBirthdate()))
                .build();
    }

    static String determineBirthDate(@Nullable String birthDate) {
        String outputDateString = null;
        if (birthDate != null) {
            LocalDate date = LocalDate.parse(birthDate);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
            outputDateString = date.format(outputFormatter);
        }
        return outputDateString;
    }

    static Gender getGender(String gender) {
        switch (gender) {
            case "female":
                return Gender.FEMALE;
            case "male":
                return Gender.MALE;
            default:
                throw new IllegalStateException("Unknown gender: " + gender);
        }
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
            String gender = getGender(patientInformationData.getGender()).gender;

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