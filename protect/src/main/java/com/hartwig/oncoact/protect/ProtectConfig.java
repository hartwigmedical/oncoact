package com.hartwig.oncoact.protect;

import com.google.common.collect.Sets;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public interface ProtectConfig {

    String DOID_SEPARATOR = ";";

    // General params needed for every analysis
    String ORANGE_JSON = "orange_json";
    String PRIMARY_TUMOR_DOIDS = "primary_tumor_doids";
    String OUTPUT_DIRECTORY = "output_dir";

    // Input files used by the algorithm
    String SERVE_ACTIONABILITY_DIRECTORY = "serve_actionability_dir";
    String DOID_JSON = "doid_json";
    String DRIVER_GENE_TSV = "driver_gene_tsv";
    String CLINICAL_TRANSCRIPTS_TSV = "clinical_transcripts_tsv";


    // Some additional optional params and flags
    String LOG_DEBUG = "log_debug";

    @NotNull
    static Options createOptions() {
        Options options = new Options();

        options.addOption(ORANGE_JSON, true, "The path towards the ORANGE json");
        options.addOption(PRIMARY_TUMOR_DOIDS, true, "A semicolon-separated list of DOIDs representing the primary tumor of patient.");
        options.addOption(OUTPUT_DIRECTORY, true, "Path to where the PROTECT output data will be written to.");

        options.addOption(SERVE_ACTIONABILITY_DIRECTORY, true, "Path towards the SERVE actionability directory.");
        options.addOption(DOID_JSON, true, "Path to JSON file containing the full DOID tree.");
        options.addOption(DRIVER_GENE_TSV, true, "Path to driver gene TSV");
        options.addOption(CLINICAL_TRANSCRIPTS_TSV, true, "Path towards a TSV containing the clinical transcripts of that gene.");

        options.addOption(LOG_DEBUG, false, "If provided, set the log level to debug rather than default.");

        return options;
    }

    @NotNull
    String orangeJson();

    @NotNull
    Set<String> primaryTumorDoids();

    @NotNull
    String outputDir();

    @NotNull
    String serveActionabilityDir();

    @NotNull
    String doidJsonFile();

    @NotNull
    String driverGeneTsv();

    @NotNull
    String clinicalTranscriptsTsv();

    @NotNull
    static ProtectConfig createConfig(@NotNull CommandLine cmd) throws ParseException, IOException {
        if (cmd.hasOption(LOG_DEBUG)) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        return ImmutableProtectConfig.builder()
                .orangeJson(nonOptionalFile(cmd, ORANGE_JSON))
                .primaryTumorDoids(toStringSet(nonOptionalValue(cmd, PRIMARY_TUMOR_DOIDS), DOID_SEPARATOR))
                .outputDir(outputDir(cmd, OUTPUT_DIRECTORY))
                .serveActionabilityDir(nonOptionalDir(cmd, SERVE_ACTIONABILITY_DIRECTORY))
                .doidJsonFile(nonOptionalFile(cmd, DOID_JSON))
                .driverGeneTsv(nonOptionalFile(cmd, DRIVER_GENE_TSV))
                .clinicalTranscriptsTsv(nonOptionalFile(cmd, CLINICAL_TRANSCRIPTS_TSV))
                .build();
    }

    @NotNull
    static Iterable<String> toStringSet(@NotNull String paramValue, @NotNull String separator) {
        return !paramValue.isEmpty() ? Sets.newHashSet(paramValue.split(separator)) : Sets.newHashSet();
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
