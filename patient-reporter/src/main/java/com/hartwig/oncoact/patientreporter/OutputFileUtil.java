package com.hartwig.oncoact.patientreporter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.hartwig.lama.client.model.PatientReporterData;

import org.jetbrains.annotations.NotNull;

public final class OutputFileUtil {

    private OutputFileUtil() {
    }

    @NotNull
    public static String generateOutputFileNameForPdfPanelResultReport(@NotNull PanelReport report) {
        String filePrefix = getFilePrefix(report);
        String fileSuffix = report.isCorrectedReport() ? "_corrected.pdf" : ".pdf";
        return filePrefix + "oncoact_tumor_ngs_result_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForJsonPanel(@NotNull PanelReport report) {
        String filePrefix = getFilePrefix(report);
        String fileSuffix = report.isCorrectedReport() ? "_corrected.json" : ".json";
        return filePrefix + "oncoact_tumor_ngs_result_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForPdfReport(@NotNull PatientReport report) {
        String filePrefix = getFilePrefix(report);
        String fileSuffix = report.isCorrectedReport() ? "_corrected.pdf" : ".pdf";
        return filePrefix + "_oncoact_tumor_wgs_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForJson(@NotNull PatientReport report) {
        String filePrefix = getFilePrefix(report);
        String fileSuffix = report.isCorrectedReport() ? "_corrected.json" : ".json";
        return filePrefix + "_oncoact_tumor_wgs_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForXML(@NotNull PatientReport report) {
        String filePrefix = getFilePrefix(report);
        String fileSuffix = report.isCorrectedReport() ? "_corrected.xml" : ".xml";
        return filePrefix + "_oncoact_tumor_wgs_report" + fileSuffix;
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