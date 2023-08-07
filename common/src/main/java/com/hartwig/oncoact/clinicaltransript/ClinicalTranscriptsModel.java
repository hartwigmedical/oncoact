package com.hartwig.oncoact.clinicaltransript;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ClinicalTranscriptsModel {

    @NotNull
    private final Map<String, String> clinicalTranscriptMap;

    ClinicalTranscriptsModel(@NotNull final Map<String, String> clinicalTranscriptmMap) {
        this.clinicalTranscriptMap = clinicalTranscriptmMap;
    }

    @Nullable
    public String findCanonicalTranscriptForGene(@NotNull String gene) {
        return clinicalTranscriptMap.get(gene);
    }
}
