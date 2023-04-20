package com.hartwig.oncoact.lims;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class LimsCheckerTest {

    @Test
    public void canConvertHospitalPathologySampleId() {
        String wideSampleId = "WIDE020000001T";
        String cpctSampleId = "CPCT020000001T";
        String coreSampleId = "CORE020000001T";

        String correctIdT = "T20-72346";
        String correctIdC = "C18-124";
        String wrongId = "BGr-121111";
        String correctIdT_part = "T20-72346 (I-6)";
        String correctIdC_part = "C18-124 I 1";
        String correctIdT_part_small = "T20-1 (I-6)";
        String correctIdC_part_small = "C18-2 I 1";
        assertEquals(correctIdT, LimsChecker.toHospitalPathologySampleIdForReport(correctIdT, wideSampleId));
        assertEquals(correctIdC, LimsChecker.toHospitalPathologySampleIdForReport(correctIdC, wideSampleId));
        assertEquals(correctIdT_part, LimsChecker.toHospitalPathologySampleIdForReport(correctIdT_part, wideSampleId));
        assertEquals(correctIdC_part, LimsChecker.toHospitalPathologySampleIdForReport(correctIdC_part, wideSampleId));
        assertEquals(correctIdT_part_small,
                LimsChecker.toHospitalPathologySampleIdForReport(correctIdT_part_small, wideSampleId));
        assertEquals(correctIdC_part_small,
                LimsChecker.toHospitalPathologySampleIdForReport(correctIdC_part_small, wideSampleId));

        assertEquals(wrongId, LimsChecker.toHospitalPathologySampleIdForReport(wrongId, wideSampleId));
        assertNull(LimsChecker.toHospitalPathologySampleIdForReport(Lims.NOT_AVAILABLE_STRING, wideSampleId));
        assertNull(LimsChecker.toHospitalPathologySampleIdForReport(Strings.EMPTY, wideSampleId));

        assertNull(LimsChecker.toHospitalPathologySampleIdForReport(Strings.EMPTY, coreSampleId));
        assertEquals(correctIdT, LimsChecker.toHospitalPathologySampleIdForReport(correctIdT, coreSampleId));
        assertEquals(wrongId, LimsChecker.toHospitalPathologySampleIdForReport(wrongId, coreSampleId));

        assertEquals(correctIdT, LimsChecker.toHospitalPathologySampleIdForReport(correctIdT, cpctSampleId));
        assertEquals(correctIdC, LimsChecker.toHospitalPathologySampleIdForReport(correctIdC, cpctSampleId));
    }
}