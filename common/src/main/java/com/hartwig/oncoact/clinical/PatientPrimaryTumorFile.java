package com.hartwig.oncoact.clinical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

public final class PatientPrimaryTumorFile {

    private static final String TAB_DELIMITER = "\t";
    private static final String STRING_DELIMITER = ";";

    private PatientPrimaryTumorFile() {
    }

    @NotNull
    public static List<PatientPrimaryTumor> read(@NotNull String filePath) throws IOException {
        return fromLines(Files.readAllLines(new File(filePath).toPath()));
    }

    @NotNull
    @VisibleForTesting
    static List<PatientPrimaryTumor> fromLines(@NotNull List<String> lines) {
        List<PatientPrimaryTumor> patientPrimaryTumors = Lists.newArrayList();
        // Skip header
        for (String line : lines.subList(1, lines.size())) {
            String[] parts = line.split(TAB_DELIMITER);
            patientPrimaryTumors.add(ImmutablePatientPrimaryTumor.builder()
                    .patientIdentifier(parts[0])
                    .location(parts[1])
                    .subLocation(parts[2])
                    .type(parts[3])
                    .subType(parts[4])
                    .extraDetails(parts[5])
                    .doids(toStringList(parts[6]))
                    .snomedConceptIds(toStringList(parts[7]))
                    .isOverridden(Boolean.parseBoolean(parts[8]))
                    .build());
        }

        return patientPrimaryTumors;
    }

    @NotNull
    @VisibleForTesting
    static List<String> toStringList(@NotNull String stringPart) {
        return Lists.newArrayList(stringPart.split(STRING_DELIMITER));
    }
}
