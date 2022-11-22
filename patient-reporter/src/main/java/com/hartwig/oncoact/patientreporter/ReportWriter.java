package com.hartwig.oncoact.patientreporter;

import java.io.IOException;

import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.patientreporter.panel.PanelReport;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;

import org.jetbrains.annotations.NotNull;

public interface ReportWriter {

    void writeAnalysedPatientReport(@NotNull AnalysedPatientReport report, @NotNull String outputFilePath) throws IOException;

    void writeQCFailReport(@NotNull QCFailReport report, @NotNull String outputFilePath) throws IOException;

    void writeJsonFailedFile(@NotNull QCFailReport report, @NotNull String outputFilePath) throws IOException;

    void writeJsonAnalysedFile(@NotNull AnalysedPatientReport report, @NotNull String outputFilePath) throws IOException;

    void writeXMLAnalysedFile(@NotNull AnalysedPatientReport report, @NotNull String outputFilePath) throws IOException;

    void writePanelAnalysedReport(@NotNull PanelReport report, @NotNull String outputFilePath) throws IOException;

    void writePanelQCFailReport(@NotNull PanelFailReport report, @NotNull String outputFilePath) throws IOException;

    void writeJsonPanelFile(@NotNull PanelReport report, @NotNull String outputFilePath) throws IOException;

    void writeJsonPanelFailedFile(@NotNull PanelFailReport report, @NotNull String outputFilePath) throws IOException;
}