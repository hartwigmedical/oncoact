package com.hartwig.oncoact.patientreporter.cfreport;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReportResourcesTest {

    @Test
    public void canLoadFonts() {
        assertNotNull(ReportResources.fontRegular());
        assertNotNull(ReportResources.fontBold());
        assertNotNull(ReportResources.iconFont());
    }
}