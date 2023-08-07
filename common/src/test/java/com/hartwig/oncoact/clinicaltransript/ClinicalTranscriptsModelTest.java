package com.hartwig.oncoact.clinicaltransript;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ClinicalTranscriptsModelTest {

    @Test
    public void canExtractClinicalTranscript() {
        Map<String, String> clinicalTranscriptMap = Maps.newHashMap();
        clinicalTranscriptMap.put("BRCA2", "NM_345");
        ClinicalTranscriptsModel clinicalTranscriptsModel = new ClinicalTranscriptsModel(clinicalTranscriptMap);

        assertEquals("NM_345", clinicalTranscriptsModel.findCanonicalTranscriptForGene("BRCA2"));
        assertNull(clinicalTranscriptsModel.findCanonicalTranscriptForGene("BRCA1"));
    }

}