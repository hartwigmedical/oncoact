package com.hartwig.oncoact.patientreporter.diagnosticsilo;

import com.hartwig.silo.SiloClient;
import com.hartwig.silo.client.model.PatientInformationResponse;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class DiagnosticSiloJson {

    private DiagnosticSiloJson() {
    }

    @Nullable
    public static PatientInformationResponse read(@Nullable String diagnosticJsonPathName) throws IOException {
        if (diagnosticJsonPathName == null) {
            return null;
        } else {
            BufferedReader diagnosticFileReader = new BufferedReader(new FileReader(diagnosticJsonPathName));
            return SiloClient.getJsonMapper().readValue(diagnosticFileReader, PatientInformationResponse.class);
        }
    }
}
