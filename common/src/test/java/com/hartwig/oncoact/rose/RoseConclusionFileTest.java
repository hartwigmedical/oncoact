package com.hartwig.oncoact.rose;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.google.common.io.Resources;

import org.junit.Test;

public class RoseConclusionFileTest  {

    private static final String ROSE_TSV = Resources.getResource("rose/sample.rose.tsv").getPath();

    @Test
    public void canReadRoseSummaryFile() throws IOException {
        String roseSummary = RoseConclusionFile.read(ROSE_TSV);
        assertEquals("Melanoma sample \n - A \n - B \n C \n D \n ", roseSummary);
    }
}