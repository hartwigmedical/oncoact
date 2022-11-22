package com.hartwig.oncoact.patientreporter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.hartwig.oncoact.common.clinical.PatientPrimaryTumor;
import com.hartwig.oncoact.common.clinical.PatientPrimaryTumorFile;
import com.hartwig.oncoact.common.lims.Lims;
import com.hartwig.oncoact.common.lims.LimsFactory;
import com.hartwig.oncoact.common.utils.DataUtil;
import com.hartwig.oncoact.patientreporter.cfreport.CFReportWriter;
import com.hartwig.oncoact.patientreporter.panel.ImmutableQCFailPanelReportData;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReporter;
import com.hartwig.oncoact.patientreporter.panel.PanelReporter;
import com.hartwig.oncoact.patientreporter.panel.QCFailPanelReportData;
import com.hartwig.oncoact.patientreporter.reportingdb.ReportingDb;
import com.hartwig.oncoact.patientreporter.panel.PanelReport;

import org.apache.commons.cli.DefaultParser;
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
    //                public static final String VERSION = "7.26";

    public static void main(@NotNull String[] args) throws IOException {
        LOGGER.info("Running patient reporter v{}", VERSION);

        Options options = PanelReporterConfig.createOptions();

        PanelReporterConfig config = null;
        try {
            config = PanelReporterConfig.createConfig(new DefaultParser().parse(options, args));
        } catch (ParseException exception) {
            LOGGER.warn(exception);
            new HelpFormatter().printHelp("PanelReporter", options);
            throw new IllegalArgumentException("Unexpected error, check inputs");
        }

        new PanelReporterApplication(config, DataUtil.formatDate(LocalDate.now())).run();
    }

    @NotNull
    private final PanelReporterConfig config;
    @NotNull
    private final String reportDate;

    private PanelReporterApplication(@NotNull final PanelReporterConfig config, @NotNull final String reportDate) {
        this.config = config;
        this.reportDate = reportDate;
    }

    private void run() throws IOException {
        SampleMetadata sampleMetadata = buildSampleMetadata(config);

        if (config.panelQcFail()) {
            LOGGER.info("Generating qc-fail panel report");
            generatePanelQCFail(sampleMetadata);
        } else {
            LOGGER.info("Generating panel report");
            generatePanelAnalysedReport(sampleMetadata);
        }
    }

    private void generatePanelAnalysedReport(@NotNull SampleMetadata sampleMetadata) throws IOException {
        PanelReporter reporter = new PanelReporter(buildBasePanelReportData(config), reportDate);
        PanelReport report = reporter.run(sampleMetadata,
                config.comments(),
                config.isCorrectedReport(),
                config.isCorrectedReportExtern(),
                config.expectedPipelineVersion(),
                config.overridePipelineVersion(),
                config.pipelineVersionFile(),
                config.requirePipelineVersionFile(),
                config.panelVCFname(),
                config.allowDefaultCohortConfig());

        ReportWriter reportWriter = CFReportWriter.createProductionReportWriter();
        String outputFilePath = generateOutputFilePathForPanelResultReport(config.outputDirReport(), report);

        reportWriter.writePanelAnalysedReport(report, outputFilePath);

        if (!config.onlyCreatePDF()) {
            LOGGER.debug("Updating reporting db and writing report data");
            reportWriter.writeJsonPanelFile(report, config.outputDirData());
            new ReportingDb().appendPanelReport(report, config.outputDirData());
        }
    }

    private void generatePanelQCFail(@NotNull SampleMetadata sampleMetadata) throws IOException {
        PanelFailReporter reporter = new PanelFailReporter(buildBasePanelReportData(config), reportDate);
        PanelFailReport report = reporter.run(sampleMetadata,
                config.comments(),
                config.isCorrectedReport(),
                config.isCorrectedReportExtern(),
                config.panelQcFailReason(),
                config.allowDefaultCohortConfig());

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
    private static SampleMetadata buildSampleMetadata(@NotNull PanelReporterConfig config) {
        String sampleNameForReport = config.sampleNameForReport();
        SampleMetadata sampleMetadata = ImmutableSampleMetadata.builder()
                .refSampleId(null)
                .refSampleBarcode(null)
                .tumorSampleId(config.tumorSampleId())
                .tumorSampleBarcode(config.tumorSampleBarcode())
                .sampleNameForReport(sampleNameForReport != null ? sampleNameForReport : config.tumorSampleId())
                .build();

        LOGGER.info("Printing sample meta data for {}", sampleMetadata.tumorSampleId());
        LOGGER.info(" Tumor sample barcode: {}", sampleMetadata.tumorSampleBarcode());
        LOGGER.info(" Ref sample: {}", sampleMetadata.refSampleId());
        LOGGER.info(" Ref sample barcode: {}", sampleMetadata.refSampleBarcode());
        LOGGER.info(" Sample name for report: {}", sampleMetadata.sampleNameForReport());

        return sampleMetadata;
    }

    @NotNull
    private static QCFailPanelReportData buildBasePanelReportData(@NotNull PanelReporterConfig config) throws IOException {
        String primaryTumorTsv = config.primaryTumorTsv();

        List<PatientPrimaryTumor> patientPrimaryTumors = PatientPrimaryTumorFile.read(primaryTumorTsv);
        LOGGER.info("Loaded primary tumors for {} patients from {}", patientPrimaryTumors.size(), primaryTumorTsv);

        String limsDirectory = config.limsDir();
        Lims lims = LimsFactory.fromLimsDirectory(limsDirectory);
        LOGGER.info("Loaded LIMS data for {} samples from {}", lims.sampleBarcodeCount(), limsDirectory);

        return ImmutableQCFailPanelReportData.builder()
                .patientPrimaryTumors(patientPrimaryTumors)
                .limsModel(lims)
                .signaturePath(config.signature())
                .logoCompanyPath(config.companyLogo())
                .build();
    }
}