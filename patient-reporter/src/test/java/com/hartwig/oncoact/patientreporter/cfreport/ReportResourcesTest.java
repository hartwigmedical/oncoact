package com.hartwig.oncoact.patientreporter.cfreport;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReportResourcesTest {

    @Test
    public void canLoadFonts() {
        ReportResources reportResources = ReportResources.create();
        assertNotNull(reportResources.fontRegular());
        assertNotNull(reportResources.fontBold());
        assertNotNull(reportResources.iconFont());
    }
}