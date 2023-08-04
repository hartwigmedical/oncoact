package com.hartwig.oncoact.clinicaltransript;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

public class ClinicalTranscriptModelTestFactory {

    private ClinicalTranscriptModelTestFactory() {
    }

    @NotNull
    public static ClinicalTranscriptsModel createEmpty() {
        return new ClinicalTranscriptsModel(Maps.newHashMap());
    }
}