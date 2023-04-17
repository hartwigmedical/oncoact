package com.hartwig.oncoact.lama;

import com.hartwig.lama.client.LamaClient;
import com.hartwig.lama.client.model.PatientReporterData;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class LamaJson {

    private LamaJson() {
    }

    @NotNull
    public static PatientReporterData read(@NotNull String lamaJsonPathName) throws IOException {
        BufferedReader lamaFileReader = new BufferedReader(new FileReader(lamaJsonPathName));
        PatientReporterData patientReporterData = LamaClient.getJsonMapper().readValue(lamaFileReader, PatientReporterData.class);
        return patientReporterData;
    }
}