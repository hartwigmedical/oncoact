package com.hartwig.oncoact.patientreporter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import com.hartwig.oncoact.parser.CliAndPropertyParser;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReporter;
import com.hartwig.oncoact.patientreporter.algo.AnalysedReportData;
import com.hartwig.oncoact.patientreporter.cfreport.CFReportWriter;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReportData;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReporter;
import com.hartwig.oncoact.patientreporter.reportingdb.ReportingDb;
import com.hartwig.oncoact.util.Formats;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class PatientReporterApplication {

    private static final Logger LOGGER = LogManager.getLogger(PatientReporterApplication.class);

    public static final String VERSION = PatientReporterApplication.class.getPackage().getImplementationVersion();

    // Uncomment this line when generating an example report using CFReportWriterTest
    //                public static final String VERSION = "8.0";

    public static void main(@NotNull String[] args) throws IOException {
        LOGGER.info("Running patient reporter v{}", VERSION);

        Options options = PatientReporterConfig.createOptions();

        PatientReporterConfig config = null;
        try {
            config = PatientReporterConfig.createConfig(new CliAndPropertyParser().parse(options, args));
        } catch (ParseException exception) {
            LOGGER.warn(exception);
            new HelpFormatter().printHelp("PatientReporter", options);
            System.exit(1);
        }

        LOGGER.info("Patient reporter config is: {}", config);
        new PatientReporterApplication(config, Formats.formatDate(LocalDate.now())).run();
    }

    @NotNull
    private final PatientReporterConfig config;
    @NotNull
    private final String reportDate;

    public PatientReporterApplication(@NotNull final PatientReporterConfig config, @NotNull final String reportDate) {
        this.config = config;
        this.reportDate = reportDate;
    }

    public void run() throws IOException {

        if (config.qcFail()) {
            LOGGER.info("Generating qc-fail report");
            generateQCFail();
        } else {
            LOGGER.info("Generating patient report");
            generateAnalysedReport();
        }
    }

    private void generateAnalysedReport() throws IOException {
        AnalysedReportData reportData = AnalysedReportData.buildFromConfig(config);
        AnalysedPatientReporter reporter = new AnalysedPatientReporter(reportData, reportDate);

        AnalysedPatientReport report = reporter.run(config);

        ReportWriter reportWriter = CFReportWriter.createProductionReportWriter();

        String outputFilePath = generateOutputFilePathForPatientReport(config.outputDirReport(), report);
        reportWriter.writeAnalysedPatientReport(report, outputFilePath);

        if (!config.onlyCreatePDF()) {
            LOGGER.debug("Updating reporting db and writing report data");

            reportWriter.writeJsonAnalysedFile(report, config.outputDirData());

            reportWriter.writeXMLAnalysedFile(report, config.outputDirData());

            new ReportingDb().appendAnalysedReport(report, config.outputDirData());
        }
    }

    private void generateQCFail() throws IOException {
        QCFailReporter reporter = new QCFailReporter(QCFailReportData.buildFromConfig(config), reportDate);
        QCFailReport report = reporter.run(config);

        ReportWriter reportWriter = CFReportWriter.createProductionReportWriter();
        String outputFilePath = generateOutputFilePathForPatientReport(config.outputDirReport(), report);

        reportWriter.writeQCFailReport(report, outputFilePath);

        if (!config.onlyCreatePDF()) {
            LOGGER.debug("Updating reporting db and writing report data");

            reportWriter.writeJsonFailedFile(report, config.outputDirData());

            new ReportingDb().appendQCFailReport(report, config.outputDirReport());
        }
    }

    @NotNull
    private static String generateOutputFilePathForPatientReport(@NotNull String outputDirReport, @NotNull PatientReport patientReport) {
        return outputDirReport + File.separator + OutputFileUtil.generateOutputFileNameForPdfReport(patientReport);
    }
}