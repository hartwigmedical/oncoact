package com.hartwig.oncoact.patientreporter;

import com.hartwig.oncoact.patientreporter.model.WgsReport;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.patientreporter.panel.PanelReport;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface ReportWriter {

    void writeAnalysedPatientReport(@NotNull WgsReport report, @NotNull String outputFilePath, @NotNull String logoCompanyPath, @NotNull String purpleCircosPlot, @NotNull String logoRVAPath, @NotNull String signaturePath) throws IOException;

    void writeQCFailReport(@NotNull QCFailReport report, @NotNull String outputFilePath) throws IOException;

    void writeJsonFailedFile(@NotNull QCFailReport report, @NotNull String outputFilePath) throws IOException;

    void writeJsonAnalysedFile(@NotNull WgsReport report, @NotNull String outputFilePath) throws IOException;

    void writeXMLAnalysedFile(@NotNull WgsReport report, @NotNull String outputFilePath) throws IOException;

    void writePanelAnalysedReport(@NotNull PanelReport report, @NotNull String outputFilePath) throws IOException;

    void writePanelQCFailReport(@NotNull PanelFailReport report, @NotNull String outputFilePath) throws IOException;

    void writeJsonPanelFile(@NotNull PanelReport report, @NotNull String outputFilePath) throws IOException;

    void writeJsonPanelFailedFile(@NotNull PanelFailReport report, @NotNull String outputFilePath) throws IOException;
}