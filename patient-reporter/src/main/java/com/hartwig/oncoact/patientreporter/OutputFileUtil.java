package com.hartwig.oncoact.patientreporter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class OutputFileUtil {

    private OutputFileUtil() {
    }

    @NotNull
    public static String generateOutputFileNameForPdfPanelResultReport(@NotNull PanelReport report) {
        String filePrefix = getFilePrefix(report);
        String failPrefix = report instanceof PanelFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix = report.isCorrectedReport() ? "_corrected.pdf" : ".pdf";
        return filePrefix + failPrefix + "oncoact_tumor_ngs_result_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForJsonPanel(@NotNull PanelReport report) {
        String filePrefix = getFilePrefix(report);
        String failPrefix = report instanceof PanelFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix = report.isCorrectedReport() ? "_corrected.json" : ".json";
        return filePrefix + failPrefix + "oncoact_tumor_ngs_result_report" + fileSuffix;
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