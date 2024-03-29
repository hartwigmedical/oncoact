package com.hartwig.oncoact.patientreporter.reportingdb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.google.gson.GsonBuilder;
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
        String reportType = "panel_result_report";

        if (report.isCorrectedReport()) {
            if (report.isCorrectedReportExtern()) {
                reportType = reportType + "_corrected_external";
            } else {
                reportType = reportType + "_corrected_internal";
            }
        }
        writeApiUpdateJson(outputDirectory, reportType, NA_STRING, null, null, report.reportDate());
    }

    public void appendPanelFailReport(@NotNull PanelFailReport report, @NotNull String outputDirectory) throws IOException {
        String reportType = report.panelFailReason().identifier();

        if (report.isCorrectedReport()) {
            if (report.isCorrectedReportExtern()) {
                reportType = reportType + "_corrected_external";
            } else {
                reportType = reportType + "_corrected_internal";
            }
        }
        writeApiUpdateJson(outputDirectory, reportType, NA_STRING, null, null, report.reportDate());
    }

    public void appendAnalysedReport(@NotNull AnalysedPatientReport report, @NotNull String outputDirectory) throws IOException {
        GenomicAnalysis analysis = report.genomicAnalysis();

        String purity = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(analysis.impliedPurity());
        boolean hasReliableQuality = analysis.hasReliableQuality();
        boolean hasReliablePurity = analysis.hasReliablePurity();

        String reportType;
        String clinicalSummary = report.clinicalSummary();
        if (clinicalSummary == null || clinicalSummary.isEmpty()) {
            LOGGER.warn("Skipping addition to reporting db, missing summary for sample '{}'!", report.lamaPatientData().getReportingId());
            reportType = "report_without_conclusion";
        } else {
            reportType = hasReliablePurity && analysis.impliedPurity() > ReportResources.PURITY_CUTOFF
                    ? "wgs_analysis"
                    : "wgs_analysis_low_purity";

            if (report.isCorrectedReport()) {
                reportType = report.isCorrectedReportExtern() ? reportType + "_corrected_external" : reportType + "_corrected_internal";
            }
        }
        writeApiUpdateJson(outputDirectory, reportType, purity, hasReliableQuality, hasReliablePurity, report.reportDate());
    }

    private void writeApiUpdateJson(String outputDirectory, String reportType, String purity, Boolean hasReliableQuality,
            Boolean hasReliablePurity, String reportDate) throws IOException {
        File outputFile = new File(outputDirectory, "api-update.json");
        LOGGER.info(outputFile);

        ReportCreated payload = ReportCreated.builder()
                .reportType(reportType)
                .purity(purity.equals(NA_STRING) ? null : Float.parseFloat(purity))
                .hasReliablePurity(hasReliablePurity)
                .hasReliableQuality(hasReliableQuality)
                .reportDate(reportDate)
                .build();

        appendToFile(outputFile.getAbsolutePath(),
                new GsonBuilder().serializeNulls()
                        .serializeSpecialFloatingPointValues()
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create()
                        .toJson(payload));
    }

    public void appendQCFailReport(@NotNull QCFailReport report, @NotNull String outputDirectory) throws IOException {
        String reportType = report.reason().identifier();

        if (report.isCorrectedReport()) {
            if (report.isCorrectedReportExtern()) {
                reportType = reportType + "_corrected_external";
            } else {
                reportType = reportType + "_corrected_internal";
            }
        }
        writeApiUpdateJson(outputDirectory, reportType, NA_STRING, null, null, report.reportDate());
    }

    private static void appendToFile(@NotNull String reportingDbTsv, @NotNull String stringToAppend) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(reportingDbTsv, true));
        writer.write(stringToAppend);
        writer.close();
    }
}
