package com.hartwig.oncoact.patientreporter.algo;

import java.io.IOException;

import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.ReportData;
import com.hartwig.oncoact.patientreporter.germline.GermlineReportingFile;
import com.hartwig.oncoact.patientreporter.germline.GermlineReportingModel;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReportData;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface AnalysedReportData extends ReportData {
    static ImmutableAnalysedReportData.Builder builder() {
        return ImmutableAnalysedReportData.builder();
    }

    @NotNull
    static AnalysedReportData buildFromConfig(@NotNull PatientReporterConfig config) throws IOException {
        GermlineReportingModel germlineReportingModel = GermlineReportingFile.buildFromTsv(config.germlineReportingTsv());

        return builder().from(QCFailReportData.buildFromConfig(config)).germlineReportingModel(germlineReportingModel).build();
    }

    @NotNull
    GermlineReportingModel germlineReportingModel();
}