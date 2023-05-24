package com.hartwig.oncoact.patientreporter;

import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class OutputFileUtil {

    private OutputFileUtil() {
    }

    @NotNull
    public static String generateOutputFileNameForPdfReport(@NotNull PatientReport report) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dtf.format(dateTime);

        String hospitalNameAbbreviation = "hospital";
        String reportId = report.patientReporterData().getReportingId();

        String filePrefix = date + "_" + hospitalNameAbbreviation + "_" + reportId;

        String fileSuffix = report.isCorrectedReport() ? "_corrected.pdf" : ".pdf";

        String failPrefix = report instanceof QCFailReport ? "_failed" : Strings.EMPTY;

        return filePrefix + failPrefix + "_dna_analysis_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForPdfPanelResultReport(@NotNull PanelReport report) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dtf.format(dateTime);

        String hospitalNameAbbreviation = "hospital";
        String reportId = report.patientReporterData().getReportingId();

        String filePrefix = date + "_" + hospitalNameAbbreviation + "_" + reportId;

        String fileSuffix = report.isCorrectedReport() ? "_corrected.pdf" : ".pdf";

        String failPrefix = report instanceof PanelFailReport ? "_failed" : Strings.EMPTY;

        return filePrefix + failPrefix + "_oncopanel_result_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForJson(@NotNull PatientReport report) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dtf.format(dateTime);

        String hospitalNameAbbreviation = "hospital";
        String reportId = report.patientReporterData().getReportingId();

        String filePrefix = date + "_" + hospitalNameAbbreviation + "_" + reportId + "_oncoact";

        String failPrefix = report instanceof QCFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix;
        if (report.isCorrectedReport()) {
            if (report.isCorrectedReportExtern()) {
                fileSuffix = "_corrected.json";
            } else {
                fileSuffix = "_corrected.json";
            }
        } else {
            fileSuffix = ".json";
        }
        return filePrefix + failPrefix + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForXML(@NotNull PatientReport report) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dtf.format(dateTime);

        String hospitalNameAbbreviation = "hospital";
        String reportId = report.patientReporterData().getReportingId();

        String filePrefix = date + "_" + hospitalNameAbbreviation + "_" + reportId + "_oncoact";

        String failPrefix = report instanceof QCFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix;
        if (report.isCorrectedReport()) {
            if (report.isCorrectedReportExtern()) {
                fileSuffix = "_corrected.xml";
            } else {
                fileSuffix = "_corrected.xml";
            }
        } else {
            fileSuffix = ".xml";
        }
        return filePrefix + failPrefix + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForJsonPanel(@NotNull com.hartwig.oncoact.patientreporter.PanelReport report) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dtf.format(dateTime);

        String hospitalNameAbbreviation = "hospital";
        String reportId = report.patientReporterData().getReportingId();

        String filePrefix = date + "_" + hospitalNameAbbreviation + "_" + reportId + "_oncopanel";
        String failPrefix = report instanceof PanelFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix;
        if (report.isCorrectedReport()) {
            if (report.isCorrectedReportExtern()) {
                fileSuffix = "_corrected_external.json";
            } else {
                fileSuffix = "_corrected_internal.json";
            }
        } else {
            fileSuffix = ".json";
        }
        return filePrefix + failPrefix + fileSuffix;
    }
}