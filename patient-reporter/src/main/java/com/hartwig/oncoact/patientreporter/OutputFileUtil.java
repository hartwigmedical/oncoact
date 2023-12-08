package com.hartwig.oncoact.patientreporter;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.util.Formats;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class OutputFileUtil {

    private OutputFileUtil() {
    }

    @NotNull
    public static String generateOutputFileName(@NotNull PanelReport report) {
        PatientReporterData lamaPatientData = report.lamaPatientData();
        var fileDate = Formats.convertToFileDate(report.reportDate());
        String filePrefix = getFilePrefix(lamaPatientData.getHospitalName(), lamaPatientData.getReportingId(), fileDate);
        String fileSuffix = report.isCorrectedReport() ? "_corrected" : Strings.EMPTY;
        return filePrefix + "_oncoact_panel_result_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileName(@NotNull PatientReport report) {
        PatientReporterData lamaPatientData = report.lamaPatientData();
        var fileDate = Formats.convertToFileDate(report.reportDate());
        String filePrefix = getFilePrefix(lamaPatientData.getHospitalName(), lamaPatientData.getReportingId(), fileDate);
        String fileSuffix = report.isCorrectedReport() ? "_corrected" : Strings.EMPTY;
        return filePrefix + "_oncoact_wgs_report" + fileSuffix;
    }

    private static String getFilePrefix(String hospitalName, String reportId, String date) {
        return date + "_" + hospitalName + "_" + reportId;
    }
}