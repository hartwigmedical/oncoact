package com.hartwig.oncoact.patientreporter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import com.hartwig.oncoact.parser.CliAndPropertyParser;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReporter;
import com.hartwig.oncoact.patientreporter.algo.AnalysedReportData;
import com.hartwig.oncoact.patientreporter.algo.ExperimentType;
import com.hartwig.oncoact.patientreporter.cfreport.CFReportWriter;
import com.hartwig.oncoact.util.Formats;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class PanelReporterApplication {

    private static final Logger LOGGER = LogManager.getLogger(PanelReporterApplication.class);

    public static final String VERSION = PanelReporterApplication.class.getPackage().getImplementationVersion();

    // Uncomment this line when generating an example report using CFReportWriterTest
    //                public static final String VERSION = "8.0";

    public static void main(@NotNull String[] args) throws IOException {
        LOGGER.info("Running panel reporter v{}", VERSION);

        Options options = PanelReporterConfig.createOptions();

        PanelReporterConfig config;
        try {
            config = PanelReporterConfig.createConfig(new CliAndPropertyParser().parse(options, args));
        } catch (ParseException exception) {
            LOGGER.warn(exception);
            new HelpFormatter().printHelp("PanelReporter", options);
            throw new IllegalArgumentException("Unexpected error, check inputs");
        }

        LOGGER.info("Panel reporter config is: {}", config);
        new PanelReporterApplication(config, Formats.formatDate(LocalDate.now())).run();
    }

    @NotNull
    private final PanelReporterConfig config;
    @NotNull
    private final String reportDate;

    public PanelReporterApplication(@NotNull final PanelReporterConfig config, @NotNull final String reportDate) {
        this.config = config;
        this.reportDate = reportDate;
    }

    public void run() throws IOException {

        if (config.panelQcFail()) {
            LOGGER.info("Generating qc-fail panel report");
            generatePanelQCFail();
        } else {
            LOGGER.info("Generating panel report");
            generatePanelAnalysedReport();
        }
    }

    private void generatePanelAnalysedReport() throws IOException {
        AnalysedReportData reportData = AnalysedReportData.buildFromConfigPanel(config);

        AnalysedPatientReporter reporter = new AnalysedPatientReporter(reportData, reportDate);

        AnalysedPatientReport report = reporter.run(config.roseTsv(),
                config.pipelineVersion(),
                config.orangeJson(),
                config.protectEvidenceTsv(),
                null,
                null,
                ExperimentType.TARGETED);

        ReportWriter reportWriter = CFReportWriter.createProductionReportWriter();
        String outputFilePath = generateOutputFilePathForPanelResultReport(config.outputDirReport(), report);

        reportWriter.writeAnalysedPatientReport(report, outputFilePath);

        //        if (!config.onlyCreatePDF()) {
        //            LOGGER.debug("Updating reporting db and writing report data");
        //            reportWriter.writeJsonPanelFile(report, config.outputDirData());
        //            new ReportingDb().appendPanelReport(report, config.outputDirData());
        //        }
    }

    private void generatePanelQCFail() throws IOException {
        //        QCFailReporter reporter = new QCFailReporter(QCFailReportData.buildFromConfigPanel(config), reportDate);
        //        QCFailReport report = reporter.run(config);
        //
        //        ReportWriter reportWriter = CFReportWriter.createProductionReportWriter();
        //        String outputFilePath = generateOutputFilePathForPanelResultReport(config.outputDirReport(), report);
        //
        //        reportWriter.writeQCFailReport(report, outputFilePath);
        //
        //        if (!config.onlyCreatePDF()) {
        //            LOGGER.debug("Updating reporting db and writing report data");
        //            reportWriter.writeJsonPanelFailedFile(report, config.outputDirData());
        //            new ReportingDb().appendPanelFailReport(report, config.outputDirData());
        //        }
    }

    @NotNull
    private static String generateOutputFilePathForPanelResultReport(@NotNull String outputDirReport,
            @NotNull AnalysedPatientReport panelReport) {
        return outputDirReport + File.separator + OutputFileUtil.generateOutputFileName(panelReport) + ".pdf";
    }

}