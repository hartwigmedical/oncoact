package com.hartwig.oncoact.patientreporter;

import static com.hartwig.oncoact.patientreporter.ReporterApplication.PANEL;

import java.io.File;
import java.nio.file.Files;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReason;

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
public interface PanelReporterConfig {

    Logger LOGGER = LogManager.getLogger(PanelReporterConfig.class);

    // General params needed for every report
    String OUTPUT_DIRECTORY_REPORT = "output_dir_report";
    String OUTPUT_DIRECTORY_DATA = "output_dir_data";
    String COMPANY_LOGO = "company_logo";
    String SIGNATURE = "signature";

    // Params specific for actual patient reports
    String ORANGE_JSON = "orange_json";
    String LAMA_JSON = "lama_json";
    String DIAGNOSTIC_SILO_JSON = "diagnostic_silo_json";
    String PROTECT_EVIDENCE_TSV = "protect_evidence_tsv";
    String ROSE_TSV = "rose_tsv";

    // Params specific for QC fail reports
    String PANEL_QC_FAIL = "panel_qc_fail";
    String PANEL_QC_FAIL_REASON = "panel_qc_fail_reason";
    String SAMPLE_FAIL_REASON_COMMENT = "sample_fail_reason_comment";

    // Resources used for generating an analysed patient report
    String HAS_CORRECTIONS = "has_corrections";
    String CORRECTION_JSON = "correction_json";

    // Some additional optional params and flags
    String IS_DIAGNOSTIC = "is_diagnostic";
    String LOG_DEBUG = "log_debug";
    String ONLY_CREATE_PDF = "only_create_pdf";
    String PIPELINE_VERSION = "pipeline_version";
    String CLINICAL_TRANSCRIPTS_TSV = "clinical_transcripts_tsv";

    @NotNull
    static Options createOptions() {
        Options options = new Options();

        options.addOption(PANEL, false, "Flag to go into panel mode");

        options.addOption(OUTPUT_DIRECTORY_REPORT, true, "Path to where the PDF report will be written to.");
        options.addOption(OUTPUT_DIRECTORY_DATA, true, "Path to where the data of the report will be written to.");
        options.addOption(COMPANY_LOGO, true, "Path towards an image file containing the company logo.");
        options.addOption(SIGNATURE, true, "Path towards an image file containing the signature to be appended at the end of the report.");

        options.addOption(ORANGE_JSON, true, "The path towards the ORANGE json");
        options.addOption(LAMA_JSON, true, "The path towards the LAMA json of the sample");
        options.addOption(DIAGNOSTIC_SILO_JSON, true, "If provided, the path towards the diagnostic silo json of the patient information");
        options.addOption(PROTECT_EVIDENCE_TSV, true, "Path towards the protect evidence TSV.");
        options.addOption(ROSE_TSV, true, "Path towards the ROSE TSV file.");

        options.addOption(PANEL_QC_FAIL, false, "If set, generates a qc-fail report.");
        options.addOption(SAMPLE_FAIL_REASON_COMMENT, true, "If set, add an extra comment of the failure of the sample.");
        options.addOption(PANEL_QC_FAIL_REASON,
                true,
                "One of: " + Strings.join(Lists.newArrayList(PanelFailReason.validIdentifiers()), ','));

        options.addOption(HAS_CORRECTIONS, false, "If provided, expect a correction json.");
        options.addOption(CORRECTION_JSON, true, "If provided, the path towards a correction json.");

        options.addOption(IS_DIAGNOSTIC, false, "If provided, use diagnostic patient data ");
        options.addOption(LOG_DEBUG, false, "If provided, set the log level to debug rather than default.");
        options.addOption(ONLY_CREATE_PDF, false, "If provided, just the PDF will be generated and no additional data will be updated.");
        options.addOption(PIPELINE_VERSION, true, "String of the pipeline version");
        options.addOption(CLINICAL_TRANSCRIPTS_TSV, true, "Path towards a TSV containing the clinical transcripts of that gene.");

        return options;
    }

    @NotNull
    String outputDirReport();

    @NotNull
    String outputDirData();

    @NotNull
    String companyLogo();

    @NotNull
    String signature();

    @NotNull
    String orangeJson();

    @NotNull
    String lamaJson();

    @Nullable
    String diagnosticSiloJson();

    @NotNull
    String protectEvidenceTsv();

    @NotNull
    String roseTsv();

    boolean panelQcFail();

    @Nullable
    PanelFailReason panelQcFailReason();

    @Nullable
    String sampleFailReasonComment();

    @Nullable
    String correctionJson();

    boolean onlyCreatePDF();

    @NotNull
    String pipelineVersion();

    @NotNull
    String clinicalTranscriptsTsv();

    @NotNull
    static PanelReporterConfig createConfig(@NotNull CommandLine cmd) throws ParseException {
        if (cmd.hasOption(LOG_DEBUG)) {
            Configurator.setRootLevel(Level.DEBUG);
            LOGGER.debug("Switched root level logging to DEBUG");
        }

        String orangeJson = Strings.EMPTY;
        String protectEvidenceTsv = Strings.EMPTY;
        String roseTsv = Strings.EMPTY;

        String correctionJson = null;
        if (cmd.hasOption(HAS_CORRECTIONS)) {
            correctionJson = nonOptionalFile(cmd, CORRECTION_JSON);
        }

        String diagnosticSiloJson = null;
        if (cmd.hasOption(IS_DIAGNOSTIC)) {
            diagnosticSiloJson = nonOptionalFile(cmd, DIAGNOSTIC_SILO_JSON);
        }

        boolean isPanelQCFail = cmd.hasOption(PANEL_QC_FAIL);
        PanelFailReason panelQcFailReason = null;
        String clinicalTranscriptsTsv = Strings.EMPTY;

        if (isPanelQCFail) {
            clinicalTranscriptsTsv = nonOptionalFile(cmd, CLINICAL_TRANSCRIPTS_TSV);
            String qcFailReasonString = nonOptionalValue(cmd, PANEL_QC_FAIL_REASON);
            panelQcFailReason = PanelFailReason.fromIdentifier(qcFailReasonString);
            if (panelQcFailReason == null) {
                throw new ParseException("Did not recognize QC Fail reason: " + qcFailReasonString);
            }
        }

        return ImmutablePanelReporterConfig.builder()
                .outputDirReport(nonOptionalDir(cmd, OUTPUT_DIRECTORY_REPORT))
                .outputDirData(nonOptionalDir(cmd, OUTPUT_DIRECTORY_DATA))
                .companyLogo(nonOptionalFile(cmd, COMPANY_LOGO))
                .signature(nonOptionalFile(cmd, SIGNATURE))
                .orangeJson(orangeJson)
                .lamaJson(nonOptionalFile(cmd, LAMA_JSON))
                .diagnosticSiloJson(diagnosticSiloJson)
                .protectEvidenceTsv(protectEvidenceTsv)
                .roseTsv(roseTsv)
                .panelQcFail(isPanelQCFail)
                .panelQcFailReason(panelQcFailReason)
                .sampleFailReasonComment(cmd.getOptionValue(SAMPLE_FAIL_REASON_COMMENT))
                .correctionJson(correctionJson)
                .onlyCreatePDF(cmd.hasOption(ONLY_CREATE_PDF))
                .pipelineVersion(nonOptionalValue(cmd, PIPELINE_VERSION))
                .clinicalTranscriptsTsv(clinicalTranscriptsTsv)
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
