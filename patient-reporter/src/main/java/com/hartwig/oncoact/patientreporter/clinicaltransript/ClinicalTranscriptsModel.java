package com.hartwig.oncoact.patientreporter.clinicaltransript;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ClinicalTranscriptsModel {

    @NotNull
    private final Map<String, String> clinicalTranscriptmMap;

    ClinicalTranscriptsModel(@NotNull final Map<String, String> clinicalTranscriptmMap) {
        this.clinicalTranscriptmMap = clinicalTranscriptmMap;
    }

    @NotNull
    public String findCanonicalTranscriptForGene(@NotNull String gene) {
        boolean geneHasSpecialRemark = genePresentInCanonicalTranscripts(gene);

        return geneHasSpecialRemark ? clinicalTranscriptmMap.get(gene) : Strings.EMPTY;
    }

    @VisibleForTesting
    int clinicaltranscriptCount() {
        return clinicalTranscriptmMap.keySet().size();
    }

    @VisibleForTesting
    boolean genePresentInCanonicalTranscripts(@NotNull String gene) {
        return clinicalTranscriptmMap.containsKey(gene);
    }
}
