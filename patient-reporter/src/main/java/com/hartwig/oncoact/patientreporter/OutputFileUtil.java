package com.hartwig.oncoact.patientreporter;

import com.hartwig.oncoact.lims.cohort.LimsCohortConfig;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;

import com.hartwig.oncoact.reporting.PatientReport;
import com.hartwig.oncoact.reporting.SampleReport;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class OutputFileUtil {

    private OutputFileUtil() {
    }

    @NotNull
    public static String generateOutputFileNameForPdfReport(@NotNull PatientReport report) {
        SampleReport sampleReport = report.sampleReport();
        LimsCohortConfig cohort = report.sampleReport().cohort();

        String filePrefix = cohort.requireHospitalId()
                ? sampleReport.sampleNameForReport() + "_" + sampleReport.hospitalPatientId().replace(" ", "_")
                : sampleReport.sampleNameForReport();

        String fileSuffix = report.isCorrectedReport() ? "_corrected.pdf" : ".pdf";

        String failPrefix = report instanceof QCFailReport ? "_failed" : Strings.EMPTY;

        return filePrefix + failPrefix + "_dna_analysis_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForPdfPanelResultReport(@NotNull PanelReport report) {
        SampleReport sampleReport = report.sampleReport();
        LimsCohortConfig cohort = report.sampleReport().cohort();

        String filePrefix = cohort.requireHospitalId()
                ? sampleReport.sampleNameForReport() + "_" + sampleReport.hospitalPatientId().replace(" ", "_")
                : sampleReport.sampleNameForReport();

        String fileSuffix = report.isCorrectedReport() ? "_corrected.pdf" : ".pdf";

        String failPrefix = report instanceof PanelFailReport ? "_failed" : Strings.EMPTY;

        return filePrefix + failPrefix + "_oncopanel_result_report" + fileSuffix;
    }

    @NotNull
    public static String generateOutputFileNameForJson(@NotNull PatientReport report) {
        String filePrefix =
                report.sampleReport().sampleNameForReport() + "_" + report.sampleReport().tumorSampleBarcode() + "_oncoact";
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
        String filePrefix =
                report.sampleReport().sampleNameForReport() + "_" + report.sampleReport().tumorSampleBarcode() + "_oncoact";
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
        String filePrefix =
                report.sampleReport().sampleNameForReport() + "_" + report.sampleReport().tumorSampleBarcode() + "_oncopanel";
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