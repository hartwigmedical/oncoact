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

    // Params specific for Panel reports
    String PANEL_QC_FAIL = "panel_qc_fail";
    String PANEL_QC_FAIL_REASON = "panel_qc_fail_reason";
    String SAMPLE_FAIL_REASON_COMMENT = "sample_fail_reason_comment";
    String PANEL_VCF_NAME = "panel_vcf_name";
    String LAMA_JSON = "lama_json";
    String DIAGNOSTIC_SILO_JSON = "diagnostic_silo_json";
    String IS_DIAGNOSTIC = "is_diagnostic";

    // Some additional optional params and flags
    String HAS_CORRECTIONS = "has_corrections";
    String CORRECTION_JSON = "correction_json";
    String LOG_DEBUG = "log_debug";
    String ONLY_CREATE_PDF = "only_create_pdf";
    String PIPELINE_VERSION = "pipeline_version";

    @NotNull
    static Options createOptions() {
        Options options = new Options();

        options.addOption(PANEL, false, "Flag to go into panel mode");
        options.addOption(OUTPUT_DIRECTORY_REPORT, true, "Path to where the PDF report will be written to.");
        options.addOption(OUTPUT_DIRECTORY_DATA, true, "Path to where the data of the report will be written to.");

        options.addOption(COMPANY_LOGO, true, "Path towards an image file containing the company logo.");
        options.addOption(SIGNATURE, true, "Path towards an image file containing the signature to be appended at the end of the report.");

        options.addOption(PANEL_QC_FAIL, false, "If set, generates a qc-fail report.");
        options.addOption(SAMPLE_FAIL_REASON_COMMENT, true, "If set, add an extra comment of the failure of the sample.");
        options.addOption(PANEL_QC_FAIL_REASON,
                true,
                "One of: " + Strings.join(Lists.newArrayList(PanelFailReason.validIdentifiers()), ','));

        options.addOption(PANEL_VCF_NAME, true, "The name of the VCF file of the panel results.");
        options.addOption(LAMA_JSON, true, "The path towards the LAMA json of the sample");

        options.addOption(IS_DIAGNOSTIC, false, "If provided, use diagnostic patient data ");
        options.addOption(DIAGNOSTIC_SILO_JSON, true, "If provided, the path towards the diagnostic silo json of the patient information");

        options.addOption(HAS_CORRECTIONS, false, "If provided, expect a correction json.");
        options.addOption(CORRECTION_JSON, true, "If provided, the path towards a correction json.");

        options.addOption(LOG_DEBUG, false, "If provided, set the log level to debug rather than default.");
        options.addOption(ONLY_CREATE_PDF, false, "If provided, just the PDF will be generated and no additional data will be updated.");
        options.addOption(PIPELINE_VERSION, true, "String of the pipeline version");

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

    boolean panelQcFail();

    @Nullable
    String sampleFailReasonComment();

    @NotNull
    String panelVCFname();

    @NotNull
    String lamaJson();

    @Nullable
    String diagnosticSiloJson();

    @Nullable
    PanelFailReason panelQcFailReason();

    @Nullable
    String correctionJson();

    boolean onlyCreatePDF();

    @NotNull
    String pipelineVersion();

    @NotNull
    static PanelReporterConfig createConfig(@NotNull CommandLine cmd) throws ParseException {
        if (cmd.hasOption(LOG_DEBUG)) {
            Configurator.setRootLevel(Level.DEBUG);
            LOGGER.debug("Switched root level logging to DEBUG");
        }

        String panelVCFFile = Strings.EMPTY;
        String pipelineVersion = null;

        boolean isPanelQCFail = cmd.hasOption(PANEL_QC_FAIL);
        PanelFailReason panelQcFailReason = null;

        if (isPanelQCFail) {
            String qcFailReasonString = nonOptionalValue(cmd, PANEL_QC_FAIL_REASON);
            panelQcFailReason = PanelFailReason.fromIdentifier(qcFailReasonString);
            if (panelQcFailReason == null) {
                throw new ParseException("Did not recognize QC Fail reason: " + qcFailReasonString);
            }
        } else {
            panelVCFFile = nonOptionalValue(cmd, PANEL_VCF_NAME);
        }

        String correctionJson = null;
        if (cmd.hasOption(HAS_CORRECTIONS)) {
            correctionJson = nonOptionalFile(cmd, CORRECTION_JSON);
        }

        String diagnosticSiloJson = null;
        if (cmd.hasOption(IS_DIAGNOSTIC)) {
            diagnosticSiloJson = nonOptionalFile(cmd, DIAGNOSTIC_SILO_JSON);
        }

        return ImmutablePanelReporterConfig.builder()
                .outputDirReport(nonOptionalDir(cmd, OUTPUT_DIRECTORY_REPORT))
                .outputDirData(nonOptionalDir(cmd, OUTPUT_DIRECTORY_DATA))
                .companyLogo(nonOptionalFile(cmd, COMPANY_LOGO))
                .signature(nonOptionalFile(cmd, SIGNATURE))
                .panelQcFail(isPanelQCFail)
                .sampleFailReasonComment(cmd.getOptionValue(SAMPLE_FAIL_REASON_COMMENT))
                .panelQcFailReason(panelQcFailReason)
                .panelVCFname(panelVCFFile)
                .lamaJson(nonOptionalFile(cmd, LAMA_JSON))
                .diagnosticSiloJson(diagnosticSiloJson)
                .correctionJson(correctionJson)
                .onlyCreatePDF(cmd.hasOption(ONLY_CREATE_PDF))
                .pipelineVersion(nonOptionalValue(cmd, PIPELINE_VERSION))
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
