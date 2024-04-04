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
        String extendedReportId = getExtendedReportId(lamaPatientData);
        String filePrefix = getFilePrefix(lamaPatientData.getHospitalName(), extendedReportId, fileDate);
        String fileSuffix = report.isCorrectedReport() ? "_corrected" : Strings.EMPTY;
        return filePrefix + "_oncoact_panel_result_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileName() {
//        PatientReporterData lamaPatientData = report.lamaPatientData();
//        var fileDate = Formats.convertToFileDate(report.reportDate());
//        String extendedReportId = getExtendedReportId(lamaPatientData);
        String filePrefix = getFilePrefix("hospital", "reporting", "date");
        String fileSuffix = false ? "_corrected" : Strings.EMPTY;
        return filePrefix + "_oncoact_wgs_report" + fileSuffix;
    }

    private static String getExtendedReportId(PatientReporterData lamaPatientData) {
        if (lamaPatientData.getHospitalSampleLabel() == null) {
            return lamaPatientData.getReportingId();
        }
        return lamaPatientData.getReportingId() + "-" + lamaPatientData.getHospitalSampleLabel();
    }

    private static String getFilePrefix(String hospitalName, String reportId, String date) {
        return date + "_" + hospitalName + "_" + reportId;
    }
}