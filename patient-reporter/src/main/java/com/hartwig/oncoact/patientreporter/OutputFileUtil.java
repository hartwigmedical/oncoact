package com.hartwig.oncoact.patientreporter;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.model.ReportingId;
import com.hartwig.oncoact.patientreporter.model.TumorSample;
import com.hartwig.oncoact.patientreporter.model.WgsPatientReport;
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
        String extendedReportId = "getExtendedReportId(lamaPatientData);";
        String filePrefix = getFilePrefix(lamaPatientData.getHospitalName(), extendedReportId, fileDate);
        String fileSuffix = report.isCorrectedReport() ? "_corrected" : Strings.EMPTY;
        return filePrefix + "_oncoact_panel_result_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileName(@NotNull WgsPatientReport wgsReport, boolean isCorrection) {
        TumorSample tumorSample = wgsReport.tumorSample();
        var fileDate = Formats.convertToFileDate(wgsReport.reportDate());
        String extendedReportId = getExtendedReportId(tumorSample);
        String filePrefix = getFilePrefix(tumorSample.hospital().name(), extendedReportId, fileDate);
        String fileSuffix = isCorrection ? "_corrected" : Strings.EMPTY;
        return filePrefix + "_oncoact_wgs_report" + fileSuffix;
    }

    private static String getExtendedReportId(TumorSample tumorSample) {
        ReportingId reportingIdData = tumorSample.reportingId();
        if (reportingIdData.label() == null) {
            return reportingIdData.value();
        }
        return reportingIdData.value() + "-" + reportingIdData.label();
    }

    private static String getFilePrefix(String hospitalName, String reportId, String date) {
        return date + "_" + hospitalName + "_" + reportId;
    }
}