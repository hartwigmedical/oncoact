package com.hartwig.oncoact.patientreporter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.algo.ExperimentType;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class OutputFileUtil {

    private OutputFileUtil() {
    }

    @NotNull
    public static String generateOutputFileName(@NotNull PatientReport report) {
        PatientReporterData lamaPatientData = report.lamaPatientData();
        String filePrefix = getFilePrefix(lamaPatientData.getHospitalName(), lamaPatientData.getReportingId());
        String fileSuffix = report.isCorrectedReport() ? "_corrected" : Strings.EMPTY;
        if (report.experimentType().equals(ExperimentType.WHOLE_GENOME)) {
            return filePrefix + "_oncoact_wgs_report" + fileSuffix;
        } else {
            return filePrefix + "_oncoact_panel_result_report" + fileSuffix;
        }
    }

    private static String getFilePrefix(String hospitalName, String reportId) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dtf.format(dateTime);
        return date + "_" + hospitalName + "_" + reportId;
    }
}