package com.hartwig.oncoact.patientreporter;

import static org.junit.Assert.*;

import org.junit.Test;

public class SampleReportFactoryTest {

    @Test
    public void canTestInterpretReferenceBarcode(){
        assertNull(SampleReportFactory.interpretRefBarcode(null));
        assertEquals("FR123", SampleReportFactory.interpretRefBarcode("FR123"));
        assertEquals("FR123", SampleReportFactory.interpretRefBarcode("FR123"));
        assertEquals("FR123", SampleReportFactory.interpretRefBarcode("FR123-c2f220514"));
        assertEquals("FR123", SampleReportFactory.interpretRefBarcode("FR123_c2f220514"));
    }
}