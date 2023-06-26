package com.hartwig.oncoact.patientreporter.diagnosticsilo;

import com.hartwig.silo.SiloClient;
import com.hartwig.silo.client.model.PatientInformationResponse;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DiagnosticSiloJson {

    private DiagnosticSiloJson() {
    }

    @Nullable
    public static PatientInformationResponse read(@Nullable String diagnosticJsonPathName, boolean isStudy) throws IOException {
        if (isStudy) {
            return null;
        } else {
            assert diagnosticJsonPathName != null;
            BufferedReader diagnosticFileReader = new BufferedReader(new FileReader(diagnosticJsonPathName));
            return SiloClient.getJsonMapper().readValue(diagnosticFileReader, PatientInformationResponse.class);
        }
    }
}
