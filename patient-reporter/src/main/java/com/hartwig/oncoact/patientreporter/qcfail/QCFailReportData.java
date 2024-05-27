package com.hartwig.oncoact.patientreporter.qcfail;

import com.hartwig.oncoact.diagnosticsilo.DiagnosticSiloJson;
import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.ReportData;
import com.hartwig.oncoact.patientreporter.correction.Correction;
import com.hartwig.oncoact.patientreporter.lama.LamaJson;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public interface QCFailReportData extends ReportData {
    static ImmutableQCFailReportData.Builder builder() {
        return ImmutableQCFailReportData.builder();
    }

    @NotNull
    static QCFailReportData buildFromConfig(@NotNull PatientReporterConfig config) throws IOException {
        var lamaPatientData = LamaJson.read(config.lamaJson());
        var diagnosticPatientData = DiagnosticSiloJson.read(config.diagnosticSiloJson());
        var correctionJson = config.correctionJson();
        var correction = correctionJson != null ? Correction.read(correctionJson) : null;

        return builder().diagnosticSiloPatientData(diagnosticPatientData)
                .lamaPatientData(lamaPatientData)
                .correction(correction)
                .signaturePath(config.signature())
                .logoRVAPath(config.rvaLogo())
                .logoCompanyPath(config.companyLogo())
                .udiDi(config.udiDi())
                .reportTime(config.reportTime())
                .build();
    }
}
