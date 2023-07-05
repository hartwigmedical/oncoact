package com.hartwig.oncoact.patientreporter.qcfail;

import java.io.IOException;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.ReportData;
import com.hartwig.oncoact.patientreporter.correction.Correction;
import com.hartwig.oncoact.patientreporter.diagnosticsilo.DiagnosticSiloJson;
import com.hartwig.oncoact.patientreporter.lama.LamaJson;
import com.hartwig.silo.client.model.PatientInformationResponse;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public interface QCFailReportData extends ReportData {
    static ImmutableQCFailReportData.Builder builder() {
        return ImmutableQCFailReportData.builder();
    }

    @NotNull
    static QCFailReportData buildFromConfig(@NotNull PatientReporterConfig config) throws IOException {
        PatientReporterData lamaPatientData = LamaJson.read(config.lamaJson());
        PatientInformationResponse diagnosticPatientData = DiagnosticSiloJson.read(config.diagnosticSiloJson());
        String correctionJson = config.correctionJson();
        Correction correction = correctionJson != null ? Correction.read(correctionJson) : null;

        return builder()
                .diagnosticSiloPatientData(diagnosticPatientData)
                .lamaPatientData(lamaPatientData)
                .correction(correction)
                .signaturePath(config.signature())
                .logoRVAPath(config.rvaLogo())
                .logoCompanyPath(config.companyLogo())
                .udiDi(config.udiDi())
                .build();
    }
}
