package com.hartwig.oncoact.clinicaltransript;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

public class ClinicalTranscriptModelFactoryTest {

    private ClinicalTranscriptModelFactoryTest() {
    }

    @NotNull
    public static ClinicalTranscriptsModel createEmpty() {
        return new ClinicalTranscriptsModel(Maps.newHashMap());
    }
}