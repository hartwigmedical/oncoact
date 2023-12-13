package com.hartwig.oncoact.rose;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface RoseConfig {

    String ORANGE_JSON = "orange_json";
    String OUTPUT_DIRECTORY = "output_dir";

    String EXPERIMENT_TYPE = "experiment_type";
    String ACTIONABILITY_DATABASE_TSV = "actionability_database_tsv";
    String DRIVER_GENE_TSV = "driver_gene_tsv";
    String CLINICAL_TRANSCRIPTS_TSV = "clinical_transcripts_tsv";

    // Some additional optional params and flags
    String LOG_DEBUG = "log_debug";

    @NotNull
    static Options createOptions() {
        Options options = new Options();

        options.addOption(ORANGE_JSON, true, "The path towards the ORANGE json");
        options.addOption(OUTPUT_DIRECTORY, true, "Path to where the data of the report will be written to.");
        options.addOption(EXPERIMENT_TYPE, true, "String to which experiment is executed");

        options.addOption(ACTIONABILITY_DATABASE_TSV, true, "Path to where the data of the actionability database can be found.");
        options.addOption(DRIVER_GENE_TSV, true, "Path to driver gene TSV");
        options.addOption(CLINICAL_TRANSCRIPTS_TSV, true, "Path towards a TSV containing the clinical transcripts of that gene.");

        options.addOption(LOG_DEBUG, false, "If provided, set the log level to debug rather than default.");

        return options;
    }

    @NotNull
    String orangeJson();

    @NotNull
    String outputDir();

    @NotNull
    String experimentType();

    @NotNull
    String actionabilityDatabaseTsv();

    @NotNull
    String driverGeneTsv();

    @NotNull
    String clinicalTranscriptsTsv();

    @NotNull
    static RoseConfig createConfig(@NotNull CommandLine cmd) throws ParseException, IOException {
        if (cmd.hasOption(LOG_DEBUG)) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        return ImmutableRoseConfig.builder()
                .orangeJson(nonOptionalFile(cmd, ORANGE_JSON))
                .outputDir(outputDir(cmd, OUTPUT_DIRECTORY))
                .experimentType(nonOptionalValue(cmd, EXPERIMENT_TYPE))
                .actionabilityDatabaseTsv(nonOptionalFile(cmd, ACTIONABILITY_DATABASE_TSV))
                .driverGeneTsv(nonOptionalFile(cmd, DRIVER_GENE_TSV))
                .clinicalTranscriptsTsv(nonOptionalFile(cmd, CLINICAL_TRANSCRIPTS_TSV))
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
    static String outputDir(@NotNull CommandLine cmd, @NotNull String param) throws ParseException, IOException {
        String value = nonOptionalValue(cmd, param);
        File outputDir = new File(value);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Unable to write to directory " + value);
        }
        return value;
    }

    @NotNull
    static String nonOptionalFile(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = nonOptionalValue(cmd, param);

        if (pathDoesNotExist(value)) {
            throw new ParseException("Parameter '" + param + "' must be an existing file: " + value);
        }

        return value;
    }

    static boolean pathDoesNotExist(@NotNull String path) {
        return !Files.exists(Paths.get(path));
    }
}