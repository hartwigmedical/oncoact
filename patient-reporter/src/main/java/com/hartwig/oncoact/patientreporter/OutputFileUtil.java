package com.hartwig.oncoact.patientreporter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class OutputFileUtil {

    private OutputFileUtil() {
    }

    @NotNull
    public static String generateOutputFileName(@NotNull PanelReport report) {
        PatientReporterData lamaPatientData = report.lamaPatientData();
        String filePrefix = getFilePrefix(lamaPatientData.getHospitalName(), lamaPatientData.getReportingId());
        String failPrefix = report instanceof PanelFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix = report.isCorrectedReport() ? "_corrected" : Strings.EMPTY;
        return filePrefix + failPrefix + "oncoact_tumor_ngs_result_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileName(@NotNull PatientReport report) {
        PatientReporterData lamaPatientData = report.lamaPatientData();
        String filePrefix = getFilePrefix(lamaPatientData.getHospitalName(), lamaPatientData.getReportingId());
        String failPrefix = report instanceof QCFailReport ? "_failed" : Strings.EMPTY;
        String fileSuffix = report.isCorrectedReport() ? "_corrected" : Strings.EMPTY;
        return filePrefix + failPrefix + "_oncoact_tumor_wgs_report" + fileSuffix;
    }

    private static String getFilePrefix(String hospitalName, String reportId) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dtf.format(dateTime);
        return date + "_" + hospitalName + "_" + reportId;
    }
}