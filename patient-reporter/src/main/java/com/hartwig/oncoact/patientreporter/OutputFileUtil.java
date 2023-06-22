package com.hartwig.oncoact.patientreporter;

import com.hartwig.lama.client.model.PatientReporterData;
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
    public static String generateOutputFileNameForPdfPanelResultReport(@NotNull PanelReport report) {
        String filePrefix = getFilePrefix(report);
        String failPrefix = report instanceof PanelFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix = report.isCorrectedReport() ? "_corrected.pdf" : ".pdf";
        return filePrefix + failPrefix + "_oncopanel_result_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForJsonPanel(@NotNull PanelReport report) {
        String filePrefix = getFilePrefix(report) + "_oncopanel";
        String failPrefix = report instanceof PanelFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix = report.isCorrectedReport() ? "_corrected.json" : ".json";
        return filePrefix + failPrefix + "_oncopanel_result_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForPdfReport(@NotNull PatientReport report) {
        String filePrefix = getFilePrefix(report);
        String failPrefix = report instanceof QCFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix = report.isCorrectedReport() ? "_corrected.pdf" : ".pdf";
        return filePrefix + failPrefix + "_dna_analysis_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForJson(@NotNull PatientReport report) {
        String filePrefix = getFilePrefix(report);
        String failPrefix = report instanceof QCFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix = report.isCorrectedReport() ? "_corrected.json" : ".json";
        return filePrefix + failPrefix + "_dna_analysis_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForXML(@NotNull PatientReport report) {
        String filePrefix = getFilePrefix(report) + "_oncoact";
        String failPrefix = report instanceof QCFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix = report.isCorrectedReport() ? "_corrected.xml" : ".xml";
        return filePrefix + failPrefix + "_dna_analysis_report" + fileSuffix;
    }

    @NotNull
    private static String getFilePrefix(final @NotNull PatientReport report) {
        PatientReporterData lamaPatientData = report.lamaPatientData();
        return getFilePrefix(lamaPatientData.getHospitalName(), lamaPatientData.getReportingId());
    }

    @NotNull
    private static String getFilePrefix(final @NotNull PanelReport report) {
        PatientReporterData lamaPatientData = report.lamaPatientData();
        return getFilePrefix(lamaPatientData.getHospitalName(), lamaPatientData.getReportingId());
    }

    private static String getFilePrefix(String hospitalName, String reportId) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dtf.format(dateTime);
        return date + "_" + hospitalName + "_" + reportId;
    }
}