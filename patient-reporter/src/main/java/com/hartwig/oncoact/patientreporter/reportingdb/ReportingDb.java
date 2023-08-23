package com.hartwig.oncoact.patientreporter.reportingdb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.hartwig.oncoact.patientreporter.OutputFileUtil;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.patientreporter.panel.PanelReport;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ReportingDb {

    private static final Logger LOGGER = LogManager.getLogger(ReportingDb.class);
    private static final String NA_STRING = "N/A";

    public ReportingDb() {
    }

    public void appendPanelReport(@NotNull PanelReport report, @NotNull String outputDirectory) throws IOException {
        String displayName = report.lamaPatientData().getCohort();
        String tumorBarcode = report.lamaPatientData().getTumorIsolationBarcode();

        String reportType = "oncopanel_result_report";

        if (report.isCorrectedReport()) {
            if (report.isCorrectedReportExtern()) {
                reportType = reportType + "_corrected_external";
            } else {
                reportType = reportType + "_corrected_internal";
            }
        }

        var outputFileName = OutputFileUtil.generateOutputFileName(report);
        writeApiUpdateJson(outputDirectory, tumorBarcode, displayName, reportType, NA_STRING, null, null, outputFileName);
    }

    public void appendPanelFailReport(@NotNull PanelFailReport report, @NotNull String outputDirectory) throws IOException {
        String cohort = report.lamaPatientData().getCohort();
        String tumorBarcode = report.lamaPatientData().getTumorIsolationBarcode();

        String reportType = report.panelFailReason().identifier();

        if (report.isCorrectedReport()) {
            if (report.isCorrectedReportExtern()) {
                reportType = reportType + "_corrected_external";
            } else {
                reportType = reportType + "_corrected_internal";
            }
        }
        var outputFileName = OutputFileUtil.generateOutputFileName(report);
        writeApiUpdateJson(outputDirectory, tumorBarcode, cohort, reportType, NA_STRING, null, null, outputFileName);
    }

    public void appendAnalysedReport(@NotNull AnalysedPatientReport report, @NotNull String outputDirectory) throws IOException {
        String cohort = report.lamaPatientData().getCohort();

        String tumorBarcode = report.lamaPatientData().getTumorIsolationBarcode();

        GenomicAnalysis analysis = report.genomicAnalysis();

        String purity = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(analysis.impliedPurity());
        boolean hasReliableQuality = analysis.hasReliableQuality();
        boolean hasReliablePurity = analysis.hasReliablePurity();

        String reportType;
        if (report.clinicalSummary().isEmpty()) {
            LOGGER.warn("Skipping addition to reporting db, missing summary for sample '{}'!", report.lamaPatientData().getReportingId());
            reportType = "report_without_conclusion";
        } else {
            if (hasReliablePurity && analysis.impliedPurity() > ReportResources.PURITY_CUTOFF) {
                reportType = "dna_analysis_report";
            } else {
                reportType = "dna_analysis_report_insufficient_tcp";
            }

            if (report.isCorrectedReport()) {
                if (report.isCorrectedReportExtern()) {
                    reportType = reportType + "_corrected_external";
                } else {
                    reportType = reportType + "_corrected_internal";
                }
            }
        }
        String outputFileName = OutputFileUtil.generateOutputFileName(report);
        writeApiUpdateJson(outputDirectory,
                tumorBarcode,
                cohort,
                reportType,
                purity,
                hasReliableQuality,
                hasReliablePurity,
                outputFileName);
    }

    private void writeApiUpdateJson(final String outputDirectory, final String tumorBarcode, final String displayName,
            final String reportType, final String purity, final Boolean hasReliableQuality, final Boolean hasReliablePurity,
            final String outputFilename) throws IOException {
        File outputFile = new File(outputDirectory, "api-update.json");
        LOGGER.info(outputFile);
        Map<String, Object> payload = new HashMap<>();
        payload.put("barcode", tumorBarcode);
        payload.put("report_type", reportType);
        payload.put("purity", purity.equals(NA_STRING) ? purity : Float.parseFloat(purity));
        payload.put("cohort", displayName);
        payload.put("has_reliable_quality", hasReliableQuality != null ? hasReliableQuality : NA_STRING);
        payload.put("has_reliable_purity", hasReliablePurity != null ? hasReliablePurity : NA_STRING);
        payload.put("output_file_name", outputFilename);

        appendToFile(outputFile.getAbsolutePath(),
                new GsonBuilder().serializeNulls()
                        .serializeSpecialFloatingPointValues()
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create()
                        .toJson(payload));
    }

    public void appendQCFailReport(@NotNull QCFailReport report, @NotNull String outputDirectory) throws IOException {
        String displayName = report.lamaPatientData().getCohort();
        String tumorBarcode = report.lamaPatientData().getTumorIsolationBarcode();

        String reportType = report.reason().identifier();

        if (report.isCorrectedReport()) {
            if (report.isCorrectedReportExtern()) {
                reportType = reportType + "_corrected_external";
            } else {
                reportType = reportType + "_corrected_internal";
            }
        }
        var outputFileName = OutputFileUtil.generateOutputFileName(report);
        writeApiUpdateJson(outputDirectory, tumorBarcode, displayName, reportType, NA_STRING, null, null, outputFileName);
    }

    private static void appendToFile(@NotNull String reportingDbTsv, @NotNull String stringToAppend) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(reportingDbTsv, true));
        writer.write(stringToAppend);
        writer.close();
    }
}
