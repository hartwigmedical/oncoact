package com.hartwig.oncoact.clinical;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class PatientPrimaryTumorFileTest {

    private static final String TEST_TSV = Resources.getResource("clinical/patient_primary_tumor.tsv").getPath();

    @Test
    public void canConvertStringLists() {
        String string1 = "str1";
        String string2 = "str2";

        List<String> convertedStringList = PatientPrimaryTumorFile.toStringList(string1 + ";" + string2);

        assertEquals(2, convertedStringList.size());
        assertEquals(string1, convertedStringList.get(0));
        assertEquals(string2, convertedStringList.get(1));
    }

    @Test
    public void canReadFile() throws IOException {
        List<PatientPrimaryTumor> patientPrimaryTumors = PatientPrimaryTumorFile.read(TEST_TSV);

        assertEquals(3, patientPrimaryTumors.size());
        // TODO Expand on test
    }
}