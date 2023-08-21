package com.hartwig.oncoact.rose;

import java.io.File;
import java.io.IOException;

import com.hartwig.oncoact.parser.CliAndPropertyParser;
import com.hartwig.oncoact.rose.conclusion.ConclusionAlgo;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class RoseApplication {

    private static final Logger LOGGER = LogManager.getLogger(RoseApplication.class);
    private static final String VERSION = RoseApplication.class.getPackage().getImplementationVersion();

    public static void main(@NotNull String[] args) throws IOException {
        LOGGER.info("Running ROSE v{}", VERSION);

        Options options = RoseConfig.createOptions();

        RoseConfig config = null;
        try {
            config = RoseConfig.createConfig(new CliAndPropertyParser().parse(options, args));
        } catch (ParseException exception) {
            LOGGER.warn(exception);
            new HelpFormatter().printHelp("ROSE", options);
            System.exit(1);
        }

        LOGGER.info("Rose config is: {}", config);
        new RoseApplication(config).run();

        LOGGER.info("Complete");
    }

    @NotNull
    private final RoseConfig config;

    public RoseApplication(@NotNull final RoseConfig config) {
        this.config = config;
    }

    public void run() throws IOException {
        RoseAlgo algo = RoseAlgo.build(config.actionabilityDatabaseTsv(), config.driverGeneTsv(), config.clinicalTranscriptsTsv());
        RoseData rose = algo.run(config);

        ActionabilityConclusion actionabilityConclusion = ConclusionAlgo.generateConclusion(rose);

        String filename = config.outputDir() + File.separator + "rose.txt";
        LOGGER.info("Writing actionability conclusion to file: {}", filename);
        RoseConclusionFile.write(filename, actionabilityConclusion);
    }
}