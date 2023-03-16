package com.hartwig.oncoact.reporting;

import com.google.common.io.Resources;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class ReportingJsonTest {

    private static final String REPORTING_JSON = Resources.getResource("reporting/reporting.json").getPath();

    @Test
    @Ignore
    public void canReadReportingJson() throws IOException {
        assertNotNull(ReportingJson.read(REPORTING_JSON));
    }
}