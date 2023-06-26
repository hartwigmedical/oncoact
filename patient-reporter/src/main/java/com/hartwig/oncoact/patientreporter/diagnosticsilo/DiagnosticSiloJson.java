package com.hartwig.oncoact.patientreporter.diagnosticsilo;

import com.hartwig.silo.SiloClient;
import com.hartwig.silo.client.model.PatientInformationResponse;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DiagnosticSiloJson {

    private DiagnosticSiloJson() {
    }

    @NotNull
    public static PatientInformationResponse read(@NotNull String diagnosticJsonPathName) throws IOException {
        BufferedReader diagnosticFileReader = new BufferedReader(new FileReader(diagnosticJsonPathName));
        return SiloClient.getJsonMapper().readValue(diagnosticFileReader, PatientInformationResponse.class);
    }
}
