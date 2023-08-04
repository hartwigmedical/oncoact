package com.hartwig.oncoact.clinicaltransript;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ClinicalTranscriptsModel {

    @NotNull
    private final Map<String, String> clinicalTranscriptMap;

    ClinicalTranscriptsModel(@NotNull final Map<String, String> clinicalTranscriptmMap) {
        this.clinicalTranscriptMap = clinicalTranscriptmMap;
    }

    @NotNull
    public String findCanonicalTranscriptForGene(@NotNull String gene) {
        boolean geneHasSpecialRemark = clinicalTranscriptMap.containsKey(gene);

        return geneHasSpecialRemark ? clinicalTranscriptMap.get(gene) : Strings.EMPTY;
    }
}
