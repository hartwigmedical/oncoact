package com.hartwig.oncoact.patientreporter;

import java.io.File;
import java.nio.file.Files;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReason;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.util.Strings;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface PatientReporterConfig {

    Logger LOGGER = LogManager.getLogger(PatientReporterConfig.class);

    // General params needed for every report
    String OUTPUT_DIRECTORY_REPORT = "output_dir_report";
    String OUTPUT_DIRECTORY_DATA = "output_dir_data";

    String RVA_LOGO = "rva_logo";
    String COMPANY_LOGO = "company_logo";
    String SIGNATURE = "signature";

    String UDI_DI = "udi_di";

    // Params specific for QC Fail reports
    String QC_FAIL = "qc_fail";
    String QC_FAIL_REASON = "qc_fail_reason";
    String SAMPLE_FAIL_REASON_COMMENT = "sample_fail_reason_comment";

    // Params specific for actual patient reports
    String ORANGE_JSON = "orange_json";
    String LAMA_JSON = "lama_json";
    String DIAGNOSTIC_SILO_JSON = "diagnostic_silo_json";
    String CUPPA_PLOT = "cuppa_plot";
    String PURPLE_CIRCOS_PLOT = "purple_circos_plot";
    String PROTECT_EVIDENCE_TSV = "protect_evidence_tsv";
    String ROSE_TSV = "rose_tsv";

    // Resources used for generating an analysed patient report
    String GERMLINE_REPORTING_TSV = "germline_reporting_tsv";
    String CLINICAL_TRANSCRIPTS_TSV = "clinical_transcripts_tsv";
    String HAS_CORRECTIONS = "has_corrections";
    String CORRECTION_JSON = "correction_json";

    // Some additional optional params and flags
    String LOG_DEBUG = "log_debug";
    String ONLY_CREATE_PDF = "only_create_pdf";
    String IS_DIAGNOSTIC = "is_diagnostic";

    // parameters for pipeline version
    String REQUIRE_PIPELINE_VERSION_FILE = "require_pipeline_version_file";
    String PIPELINE_VERSION_FILE = "pipeline_version_file";
    String EXPECTED_PIPELINE_VERSION = "expected_pipeline_version";
    String OVERRIDE_PIPELINE_VERSION = "override_pipeline_version";

    @NotNull
    static Options createOptions() {
        Options options = new Options();

        options.addOption(OUTPUT_DIRECTORY_REPORT, true, "Path to where the PDF report will be written to.");
        options.addOption(OUTPUT_DIRECTORY_DATA, true, "Path to where the data of the report will be written to.");

        options.addOption(RVA_LOGO, true, "Path towards an image file containing the RVA logo.");
        options.addOption(COMPANY_LOGO, true, "Path towards an image file containing the company logo.");
        options.addOption(SIGNATURE, true, "Path towards an image file containing the signature to be appended at the end of the report.");

        options.addOption(UDI_DI, true, "Code of the UDI DI code");

        options.addOption(QC_FAIL, false, "If set, generates a qc-fail report.");
        options.addOption(QC_FAIL_REASON, true, "One of: " + Strings.join(Lists.newArrayList(QCFailReason.validIdentifiers()), ','));
        options.addOption(SAMPLE_FAIL_REASON_COMMENT, true, "If set, add an extra comment of the failure of the sample.");

        options.addOption(ORANGE_JSON, true, "The path towards the ORANGE json");
        options.addOption(LAMA_JSON, true, "The path towards the LAMA json of the sample");
        options.addOption(CUPPA_PLOT, true, "Path towards the molecular tissue origin plot.");
        options.addOption(PURPLE_CIRCOS_PLOT, true, "Path towards the purple circos plot.");
        options.addOption(PROTECT_EVIDENCE_TSV, true, "Path towards the protect evidence TSV.");
        options.addOption(ROSE_TSV, true, "Path towards the ROSE TSV file.");

        options.addOption(GERMLINE_REPORTING_TSV, true, "Path towards a TSV containing germline reporting config.");
        options.addOption(CLINICAL_TRANSCRIPTS_TSV, true, "Path towards a TSV containing the clinical transcripts of that gene.");

        options.addOption(HAS_CORRECTIONS, false, "If provided, expect a correction json.");
        options.addOption(CORRECTION_JSON, true, "If provided, the path towards a correction json.");

        options.addOption(IS_DIAGNOSTIC, false, "If provided, use diagnostic patient data ");
        options.addOption(DIAGNOSTIC_SILO_JSON, true, "If provided, the path towards the diagnostic silo json of the patient information");

        options.addOption(LOG_DEBUG, false, "If provided, set the log level to debug rather than default.");
        options.addOption(ONLY_CREATE_PDF, false, "If provided, just the PDF will be generated and no additional data will be updated.");

        options.addOption(REQUIRE_PIPELINE_VERSION_FILE, false, "Boolean for determine pipeline version file is required");
        options.addOption(PIPELINE_VERSION_FILE, true, "Path towards the pipeline version (optional)");
        options.addOption(EXPECTED_PIPELINE_VERSION, true, "String of the expected pipeline version");
        options.addOption(OVERRIDE_PIPELINE_VERSION, false, "if set, the check for pipeline version is overridden");

        return options;
    }

    @NotNull
    String outputDirReport();

    @NotNull
    String outputDirData();

    @NotNull
    String rvaLogo();

    @NotNull
    String companyLogo();

    @NotNull
    String signature();

    @NotNull
    String udiDi();

    boolean qcFail();

    @Nullable
    QCFailReason qcFailReason();

    @Nullable
    String sampleFailReasonComment();

    @NotNull
    String orangeJson();

    @NotNull
    String lamaJson();

    @Nullable
    String diagnosticSiloJson();

    @NotNull
    String cuppaPlot();

    @NotNull
    String purpleCircosPlot();

    @NotNull
    String protectEvidenceTsv();

    @Nullable
    String roseTsv();

    @NotNull
    String germlineReportingTsv();

    @NotNull
    String clinicalTranscriptsTsv();

    @Nullable
    String correctionJson();

    boolean onlyCreatePDF();

    boolean requirePipelineVersionFile();

    @Nullable
    String pipelineVersionFile();

    @NotNull
    String expectedPipelineVersion();

    boolean overridePipelineVersion();

    @NotNull
    static PatientReporterConfig createConfig(@NotNull CommandLine cmd) throws ParseException {
        if (cmd.hasOption(LOG_DEBUG)) {
            Configurator.setRootLevel(Level.DEBUG);
            LOGGER.debug("Switched root level logging to DEBUG");
        }

        boolean isQCFail = cmd.hasOption(QC_FAIL);
        boolean requirePipelineVersion = cmd.hasOption(REQUIRE_PIPELINE_VERSION_FILE);
        QCFailReason qcFailReason = null;
        if (isQCFail) {
            String qcFailReasonString = nonOptionalValue(cmd, QC_FAIL_REASON);
            qcFailReason = QCFailReason.fromIdentifier(qcFailReasonString);
            if (qcFailReason == null) {
                throw new ParseException("Did not recognize QC Fail reason: " + qcFailReasonString);
            }
        }

        String pipelineVersion = null;
        String orangeJson = Strings.EMPTY;
        String cuppaPlot = Strings.EMPTY;
        String purpleCircosPlot = Strings.EMPTY;
        String protectEvidenceTsv = Strings.EMPTY;
        boolean addRose = false;
        String roseTsv = null;

        String germlineReportingTsv = Strings.EMPTY;
        String clinicalTranscriptsTsv = Strings.EMPTY;

        if (isQCFail && qcFailReason.isDeepWGSDataAvailable()) {
            if (requirePipelineVersion) {
                pipelineVersion = nonOptionalFile(cmd, PIPELINE_VERSION_FILE);
            }
            orangeJson = nonOptionalFile(cmd, ORANGE_JSON);
        } else if (!isQCFail) {
            if (requirePipelineVersion) {
                pipelineVersion = nonOptionalFile(cmd, PIPELINE_VERSION_FILE);
            }

            orangeJson = nonOptionalFile(cmd, ORANGE_JSON);
            cuppaPlot = nonOptionalFile(cmd, CUPPA_PLOT);
            purpleCircosPlot = nonOptionalFile(cmd, PURPLE_CIRCOS_PLOT);
            protectEvidenceTsv = nonOptionalFile(cmd, PROTECT_EVIDENCE_TSV);
            roseTsv = nonOptionalFile(cmd, ROSE_TSV);

            germlineReportingTsv = nonOptionalFile(cmd, GERMLINE_REPORTING_TSV);
            clinicalTranscriptsTsv = nonOptionalFile(cmd, CLINICAL_TRANSCRIPTS_TSV);
        }

        String correctionJson = null;
        if (cmd.hasOption(HAS_CORRECTIONS)) {
            correctionJson = nonOptionalFile(cmd, CORRECTION_JSON);
        }

        return ImmutablePatientReporterConfig.builder()
                .outputDirReport(nonOptionalDir(cmd, OUTPUT_DIRECTORY_REPORT))
                .outputDirData(nonOptionalDir(cmd, OUTPUT_DIRECTORY_DATA))
                .rvaLogo(nonOptionalFile(cmd, RVA_LOGO))
                .companyLogo(nonOptionalFile(cmd, COMPANY_LOGO))
                .signature(nonOptionalFile(cmd, SIGNATURE))
                .udiDi(nonOptionalValue(cmd, UDI_DI))
                .qcFail(isQCFail)
                .qcFailReason(qcFailReason)
                .sampleFailReasonComment(cmd.getOptionValue(SAMPLE_FAIL_REASON_COMMENT))
                .orangeJson(orangeJson)
                .lamaJson(nonOptionalFile(cmd, LAMA_JSON))
                .diagnosticSiloJson(cmd.hasOption(IS_DIAGNOSTIC) ? nonOptionalFile(cmd, DIAGNOSTIC_SILO_JSON) : null)
                .cuppaPlot(cuppaPlot)
                .purpleCircosPlot(purpleCircosPlot)
                .protectEvidenceTsv(protectEvidenceTsv)
                .roseTsv(roseTsv)
                .germlineReportingTsv(germlineReportingTsv)
                .clinicalTranscriptsTsv(clinicalTranscriptsTsv)
                .correctionJson(correctionJson)
                .onlyCreatePDF(cmd.hasOption(ONLY_CREATE_PDF))
                .requirePipelineVersionFile(requirePipelineVersion)
                .pipelineVersionFile(pipelineVersion)
                .expectedPipelineVersion(cmd.getOptionValue(EXPECTED_PIPELINE_VERSION))
                .overridePipelineVersion(cmd.hasOption(OVERRIDE_PIPELINE_VERSION))
                .build();
    }

    @NotNull
    static String nonOptionalValue(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = cmd.getOptionValue(param);
        if (value == null) {
            throw new ParseException("Parameter must be provided: " + param);
        }

        return value;
    }

    @NotNull
    static String nonOptionalDir(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = nonOptionalValue(cmd, param);

        if (!pathExists(value) || !pathIsDirectory(value)) {
            throw new ParseException("Parameter '" + param + "' must be an existing directory: " + value);
        }

        return value;
    }

    @NotNull
    static String nonOptionalFile(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = nonOptionalValue(cmd, param);

        if (!pathExists(value)) {
            throw new ParseException("Parameter '" + param + "' must be an existing file: " + value);
        }

        return value;
    }

    static boolean pathExists(@NotNull String path) {
        return Files.exists(new File(path).toPath());
    }

    static boolean pathIsDirectory(@NotNull String path) {
        return Files.isDirectory(new File(path).toPath());
    }
}