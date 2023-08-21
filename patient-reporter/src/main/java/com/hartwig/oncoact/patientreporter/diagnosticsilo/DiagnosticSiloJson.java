package com.hartwig.oncoact.patientreporter.diagnosticsilo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.hartwig.silo.diagnostic.DiagnosticSiloClient;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.jetbrains.annotations.Nullable;

public final class DiagnosticSiloJson {

    private DiagnosticSiloJson() {
    }

    @Nullable
    public static PatientInformationResponse read(@Nullable String diagnosticJsonPathName) throws IOException {
        if (diagnosticJsonPathName == null) {
            return null;
        } else {
            BufferedReader diagnosticFileReader = new BufferedReader(new FileReader(diagnosticJsonPathName));
            return DiagnosticSiloClient.getJsonMapper().readValue(diagnosticFileReader, PatientInformationResponse.class);
        }
    }
}
