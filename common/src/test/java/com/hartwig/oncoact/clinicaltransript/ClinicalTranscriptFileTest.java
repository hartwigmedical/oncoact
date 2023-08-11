package com.hartwig.oncoact.clinicaltransript;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.google.common.io.Resources;

import org.junit.Test;

public class ClinicalTranscriptFileTest {

    private static final String CLINICAL_TRANSCRIPT_TSV = Resources.getResource("clinicaltranscript/clinical_transcipts.tsv").getPath();

    @Test
    public void canReadClinicalTranscriptsTsv() throws IOException {
        ClinicalTranscriptsModel clinicalTranscriptsModel = ClinicalTranscriptFile.buildFromTsv(CLINICAL_TRANSCRIPT_TSV);

        String clinicalTranscriptBRCA1 = clinicalTranscriptsModel.findCanonicalTranscriptForGene("BRCA1");
        String clinicalTranscriptBRCA2 = clinicalTranscriptsModel.findCanonicalTranscriptForGene("BRCA2");

        assertEquals("NM_789", clinicalTranscriptBRCA1);
        assertEquals("NM_123", clinicalTranscriptBRCA2);
    }

}