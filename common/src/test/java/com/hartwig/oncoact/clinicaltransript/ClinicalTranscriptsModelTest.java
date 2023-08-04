package com.hartwig.oncoact.clinicaltransript;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClinicalTranscriptsModelTest {

    @Test
    public void canExtractClinicalTranscript() {
        Map<String, String> clinicalTranscriptMap = Maps.newHashMap();
        clinicalTranscriptMap.put("BRCA2", "NM_345");
        ClinicalTranscriptsModel clinicalTranscriptsModel = new ClinicalTranscriptsModel(clinicalTranscriptMap);

        assertEquals("NM_345", clinicalTranscriptsModel.findCanonicalTranscriptForGene("BRCA2"));
        assertEquals(Strings.EMPTY, clinicalTranscriptsModel.findCanonicalTranscriptForGene("BRCA1"));
    }

}