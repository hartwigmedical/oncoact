package com.hartwig.oncoact.patientreporter.clinicaltransript;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

public class TestClinicalTranscriptModelFactory {

    private TestClinicalTranscriptModelFactory() {
    }

    @NotNull
    public static ClinicalTranscriptsModel createEmpty() {
        return new ClinicalTranscriptsModel(Maps.newHashMap());
    }
}
