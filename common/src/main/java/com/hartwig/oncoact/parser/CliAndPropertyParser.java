package com.hartwig.oncoact.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Command Line Interface parser with an extra option to parse a properties file.
 * <p>
 * Options found in the properties file are treated the same as options provided through the command line.
 * <p>
 * If an option is provided through the command line and is found in the properties file, the command line
 * value takes precedent.
 */
public class CliAndPropertyParser extends DefaultParser {
    private static final String PROPERTIES_FILE = "properties_file";

    @Override
    public CommandLine parse(Options options, String[] args) throws ParseException {
        options.addOption(PROPERTIES_FILE, true, "Path to a properties file. Properties provide defaults for command line arguments.");
        CommandLine cmd = super.parse(options, args);

        if (!cmd.hasOption(PROPERTIES_FILE)) {
            return cmd;
        }
        var path = cmd.getOptionValue(PROPERTIES_FILE);

        // Load the properties file
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(path)) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Could not load properties file at " + path, e);
        }

        return new PropertyCommandLine(props, cmd);
    }

    private static class PropertyCommandLine extends CommandLine {
        private final Properties properties;
        private final CommandLine cmd;

        public PropertyCommandLine(final Properties properties, final CommandLine cmd) {
            this.properties = properties;
            this.cmd = cmd;
        }

        @Override
        public String getOptionValue(final char opt) {
            return getOptionValue(String.valueOf(opt));
        }

        @Override
        public String getOptionValue(final char opt, final String defaultValue) {
            return getOptionValue(String.valueOf(opt), defaultValue);
        }

        @Override
        public String getOptionValue(final String opt) {
            return cmd.getOptionValue(opt, properties.getProperty(opt));
        }

        @Override
        public String getOptionValue(final String opt, final String defaultValue) {
            var optionValue = cmd.getOptionValue(opt, properties.getProperty(opt));
            return optionValue != null ? optionValue : defaultValue;
        }

        @Override
        public boolean hasOption(final char opt) {
            return hasOption(String.valueOf(opt));
        }

        @Override
        public boolean hasOption(final String opt) {
            return cmd.hasOption(opt) || properties.containsKey(opt);
        }
    }
}

