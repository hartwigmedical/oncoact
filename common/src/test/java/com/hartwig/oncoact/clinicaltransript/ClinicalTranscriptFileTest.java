package com.hartwig.oncoact.clinicaltransript;

import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ClinicalTranscriptFileTest {

    private static final String CLINICAL_TRANSCRIPT_TSV = Resources.getResource("clinicaltranscript/clinical_transcipts.tsv").getPath();

    @Test
    public void canReadClinicalTranscriptsTsv() throws IOException {
        ClinicalTranscriptsModel clinicalTranscriptsModel = ClinicalTranscriptFile.buildFromTsv(CLINICAL_TRANSCRIPT_TSV);
        assertEquals(2, clinicalTranscriptsModel.clinicaltranscriptCount());

        String clinicalTranscriptBRCA1 = clinicalTranscriptsModel.findCanonicalTranscriptForGene("BRCA1");
        String clinicalTranscriptBRCA2 = clinicalTranscriptsModel.findCanonicalTranscriptForGene("BRCA2");

        assertEquals("NM_789", clinicalTranscriptBRCA1);
        assertEquals("NM_123", clinicalTranscriptBRCA2);
    }

}