package com.hartwig.oncoact.patientreporter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.diagnosticsilo.DiagnosticSiloJson;
import com.hartwig.oncoact.patientreporter.lama.LamaJson;
import com.hartwig.oncoact.parser.CliAndPropertyParser;
import com.hartwig.oncoact.patientreporter.cfreport.CFReportWriter;
import com.hartwig.oncoact.patientreporter.panel.ImmutableQCFailPanelReportData;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReporter;
import com.hartwig.oncoact.patientreporter.panel.PanelReporter;
import com.hartwig.oncoact.patientreporter.panel.QCFailPanelReportData;
import com.hartwig.oncoact.patientreporter.reportingdb.ReportingDb;
import com.hartwig.oncoact.util.Formats;

import com.hartwig.silo.client.model.PatientInformationResponse;
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
        LOGGER.info("Running patient reporter v{}", VERSION);

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
        PanelReporter reporter = new PanelReporter(buildBasePanelReportData(config), reportDate);
        com.hartwig.oncoact.patientreporter.panel.PanelReport report = reporter.run(
                config.comments(),
                config.isCorrectedReport(),
                config.isCorrectedReportExtern(),
                config.expectedPipelineVersion(),
                config.overridePipelineVersion(),
                config.pipelineVersionFile(),
                config.requirePipelineVersionFile(),
                config.panelVCFname());

        ReportWriter reportWriter = CFReportWriter.createProductionReportWriter();
        String outputFilePath = generateOutputFilePathForPanelResultReport(config.outputDirReport(), report);

        reportWriter.writePanelAnalysedReport(report, outputFilePath);

        if (!config.onlyCreatePDF()) {
            LOGGER.debug("Updating reporting db and writing report data");
            reportWriter.writeJsonPanelFile(report, config.outputDirData());
            new ReportingDb().appendPanelReport(report, config.outputDirData());
        }
    }

    private void generatePanelQCFail() throws IOException {
        PanelFailReporter reporter = new PanelFailReporter(buildBasePanelReportData(config), reportDate);
        PanelFailReport report = reporter.run(
                config.comments(),
                config.isCorrectedReport(),
                config.isCorrectedReportExtern(),
                config.panelQcFailReason());

        ReportWriter reportWriter = CFReportWriter.createProductionReportWriter();
        String outputFilePath = generateOutputFilePathForPanelResultReport(config.outputDirReport(), report);

        reportWriter.writePanelQCFailReport(report, outputFilePath);

        if (!config.onlyCreatePDF()) {
            LOGGER.debug("Updating reporting db and writing report data");
            reportWriter.writeJsonPanelFailedFile(report, config.outputDirData());
            new ReportingDb().appendPanelFailReport(report, config.outputDirData());
        }
    }

    @NotNull
    private static String generateOutputFilePathForPanelResultReport(@NotNull String outputDirReport,
                                                                     @NotNull com.hartwig.oncoact.patientreporter.PanelReport panelReport) {
        return outputDirReport + File.separator + OutputFileUtil.generateOutputFileNameForPdfPanelResultReport(panelReport);
    }

    @NotNull
    private static QCFailPanelReportData buildBasePanelReportData(@NotNull PanelReporterConfig config) throws IOException {

        PatientReporterData lamaPatientData = LamaJson.read(config.lamaJson());
        PatientInformationResponse diagnosticPatientData = DiagnosticSiloJson.read(config.diagnosticSiloJson());

        return ImmutableQCFailPanelReportData.builder()
                .lamaPatientData(lamaPatientData)
                .diagnosticSiloPatientData(diagnosticPatientData)
                .signaturePath(config.signature())
                .logoCompanyPath(config.companyLogo())
                .build();
    }
}