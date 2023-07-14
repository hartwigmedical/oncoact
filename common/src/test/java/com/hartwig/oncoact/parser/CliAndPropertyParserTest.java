package com.hartwig.oncoact.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CliAndPropertyParserTest {
    private File properties;

    @Before
    public void setup() throws IOException {
        properties = File.createTempFile("test", ".properties");
        try (var stream = new FileOutputStream(properties)) {
            stream.write("opt=value".getBytes(StandardCharsets.UTF_8));
        }
    }

    @After
    public void tearDown() {
        properties.delete();
    }

    @Test
    public void testNormalParsing() throws ParseException {
        var options = new Options();
        options.addOption("opt", true, "My option");
        String[] args = new String[] { "command", "-opt", "value" };
        var cli = new CliAndPropertyParser().parse(options, args);
        assertTrue(cli.hasOption("opt"));
        assertEquals("value", cli.getOptionValue("opt"));
    }

    @Test
    public void testPropertyFileParsing() throws ParseException {
        var options = new Options();
        options.addOption("opt", true, "My option");
        String[] args = new String[] { "command", "-properties_file", properties.getPath() };
        var cli = new CliAndPropertyParser().parse(options, args);
        assertTrue(cli.hasOption("opt"));
        assertEquals("value", cli.getOptionValue("opt"));
    }

    @Test
    public void testCliArgTakesPrecedent() throws ParseException {
        var options = new Options();
        options.addOption("opt", true, "My option");
        String[] args = new String[] { "command", "-opt", "cli-value", "-properties_file", properties.getPath() };
        var cli = new CliAndPropertyParser().parse(options, args);
        assertTrue(cli.hasOption("opt"));
        assertEquals("cli-value", cli.getOptionValue("opt"));
    }

    @Test
    public void testPropertiesFileDoesNotExist() {
        var options = new Options();
        options.addOption("opt", true, "My option");
        String[] args = new String[] { "command", "-properties_file", "does-not-exist.properties" };
        assertThrows(RuntimeException.class, () -> new CliAndPropertyParser().parse(options, args));
    }
}