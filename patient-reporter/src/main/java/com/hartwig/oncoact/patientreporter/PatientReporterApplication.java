package com.hartwig.oncoact.patientreporter;

import com.hartwig.oncoact.parser.CliAndPropertyParser;
import com.hartwig.oncoact.patientreporter.algo.AnalysedReportData;
import com.hartwig.oncoact.patientreporter.algo.wgs.WgsReportCreator;
import com.hartwig.oncoact.patientreporter.cfreport.CFReportWriter;
import com.hartwig.oncoact.patientreporter.correction.Correction;
import com.hartwig.oncoact.patientreporter.model.ImmutableSummary;
import com.hartwig.oncoact.patientreporter.model.ImmutableWgsReport;
import com.hartwig.oncoact.patientreporter.model.WgsPatientReport;
import com.hartwig.oncoact.patientreporter.model.WgsReport;
import com.hartwig.oncoact.patientreporter.reportingdb.ReportingDb;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class PatientReporterApplication {

    private static final Logger LOGGER = LogManager.getLogger(PatientReporterApplication.class);

    public static final String VERSION = PatientReporterApplication.class.getPackage().getImplementationVersion();

    // Uncomment this line when generating an example report using CFReportWriterTest
    //  public static final String VERSION = "8.0.2";

    public static void main(@NotNull String[] args) throws IOException {
        LOGGER.info("Running patient reporter v{}", VERSION);

        Options options = PatientReporterConfig.createOptions();

        PatientReporterConfig config;
        try {
            config = PatientReporterConfig.createConfig(new CliAndPropertyParser().parse(options, args));
        } catch (ParseException exception) {
            LOGGER.warn(exception);
            new HelpFormatter().printHelp("PatientReporter", options);
            throw new IllegalArgumentException("Unexpected error, check inputs");
        }

        LOGGER.info("Patient reporter config is: {}", config);
        new PatientReporterApplication(config).run();
    }

    @NotNull
    private final PatientReporterConfig config;

    public PatientReporterApplication(@NotNull final PatientReporterConfig config) {
        this.config = config;
    }

    public void run() throws IOException {

        if (config.qcFail()) {
            LOGGER.info("Generating qc-fail report");
            //  generateQCFail();
        } else {
            LOGGER.info("Generating patient report");
            generateAnalysedReport();
        }
    }

    private void generateAnalysedReport() throws IOException {
        AnalysedReportData reportData = AnalysedReportData.buildFromConfig(config);

        WgsReportCreator wgsReportCreator = new WgsReportCreator(reportData);
        WgsReport report = wgsReportCreator.run(config);

        ReportWriter reportWriter = CFReportWriter.createProductionReportWriter();

        boolean isCorrection = Optional.ofNullable(reportData.correction()).map(Correction::isCorrectedReport).orElse(false);
        boolean isCorrectionExtern = Optional.ofNullable(reportData.correction())
                .map(Correction::isCorrectedReportExtern)
                .orElse(false);

        String outputFilePath = generateOutputFilePathForPatientReport(config.outputDirReport(), report, isCorrection);
        reportWriter.writeAnalysedPatientReport(report, outputFilePath, reportData.logoCompanyPath(),
                config.purpleCircosPlot(), config.rvaLogo(), config.signature(), config.cuppaPlot());

        if (!config.onlyCreatePDF()) {
            LOGGER.debug("Updating reporting db and writing report data");

            String technique = "Technique: WGS";
            String platform = "Platform: NovaSeq 6000 (Illumina) WGS analysis, processed using Hartwig MedicalOncoAct® software and "
                    + "reporting (https://www.oncoact.nl/specsheetOncoActWGS). All activities are performed under ISO17025 "
                    + "accreditation (RVA, L633).";
            report = ImmutableWgsReport.builder()
                    .from(report)
                    .summary(ImmutableSummary.builder()
                            .from(report.summary())
                            .mostRelevantFindings(technique + "\n" + platform + "\n\n" + report.summary().mostRelevantFindings()
                                    + "The underlying data of these WGS results"
                                    + " can be requested at Hartwig Medical Foundation"
                                    + " (diagnosticsupport@hartwigmedicalfoundation.nl).")
                            .build())
                    .build();
            reportWriter.writeJsonAnalysedFile(report, config.outputDirData());
            reportWriter.writeXMLAnalysedFile(report, config.outputDirData(), isCorrection);
            new ReportingDb().appendAnalysedReport(report, config.outputDirData(), isCorrection, isCorrectionExtern);
        }
    }

    private void generateQCFail() throws IOException {
//        QCFailReporter reporter = new QCFailReporter(QCFailReportData.buildFromConfig(config));
//        QCFailReport report = reporter.run(config);
//
//        ReportWriter reportWriter = CFReportWriter.createProductionReportWriter();
//        String outputFilePath = generateOutputFilePathForPatientReport(config.outputDirReport(), report);
//
//        reportWriter.writeQCFailReport(report, outputFilePath);
//
//        if (!config.onlyCreatePDF()) {
//            LOGGER.debug("Updating reporting db and writing report data");
//
//            reportWriter.writeJsonFailedFile(report, config.outputDirData());
//
//            new ReportingDb().appendQCFailReport(report, config.outputDirReport());
//        }
    }

    @NotNull
    private static String generateOutputFilePathForPatientReport(@NotNull String outputDirReport,
                                                                 @NotNull WgsPatientReport wgsReport, boolean isCorrection) {
        return outputDirReport + File.separator + OutputFileUtil.generateOutputFileName(wgsReport, isCorrection) + ".pdf";
    }
}