package com.hartwig.oncoact.patientreporter.reportingdb;

import static java.lang.String.format;

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
        String sampleName = report.lamaPatientData().getReportingId();
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

        writeApiUpdateJson(outputDirectory, tumorBarcode, sampleName, displayName, reportType, report.reportDate(), NA_STRING, null, null);
    }

    public void appendPanelFailReport(@NotNull PanelFailReport report, @NotNull String outputDirectory) throws IOException {
        String sampleName = report.lamaPatientData().getReportingId();
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

        writeApiUpdateJson(outputDirectory, tumorBarcode, sampleName, cohort, reportType, report.reportDate(), NA_STRING, null, null);
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
        writeApiUpdateJson(outputDirectory,
                tumorBarcode,
                report.lamaPatientData().getReportingId(),
                cohort,
                reportType,
                report.reportDate(),
                purity,
                hasReliableQuality,
                hasReliablePurity);
    }

    private void writeApiUpdateJson(final String outputDirectory, final String tumorBarcode, final String sampleName,
            final String displayName, final String reportType, final String reportDate, final String purity,
            final Boolean hasReliableQuality, final Boolean hasReliablePurity) throws IOException {
        File outputFile = new File(outputDirectory, format("%s_%s_%s_api-update.json", sampleName, tumorBarcode, reportType));
        LOGGER.info(outputFile);
        Map<String, Object> payload = new HashMap<>();
        payload.put("barcode", tumorBarcode);
        payload.put("report_type", reportType);
        payload.put("report_date", reportDate);
        payload.put("purity", purity.equals(NA_STRING) ? purity : Float.parseFloat(purity));
        payload.put("cohort", displayName);
        payload.put("has_reliable_quality", hasReliableQuality != null ? hasReliableQuality : NA_STRING);
        payload.put("has_reliable_purity", hasReliablePurity != null ? hasReliablePurity : NA_STRING);

        appendToFile(outputFile.getAbsolutePath(),
                new GsonBuilder().serializeNulls()
                        .serializeSpecialFloatingPointValues()
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create()
                        .toJson(payload));
    }

    public void appendQCFailReport(@NotNull QCFailReport report, @NotNull String outputDirectory) throws IOException {
        String sampleName = report.lamaPatientData().getReportingId();
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

        writeApiUpdateJson(outputDirectory, tumorBarcode, sampleName, displayName, reportType, report.reportDate(), NA_STRING, null, null);
    }

    private static void appendToFile(@NotNull String reportingDbTsv, @NotNull String stringToAppend) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(reportingDbTsv, true));
        writer.write(stringToAppend);
        writer.close();
    }
}
