package com.hartwig.oncoact.patientreporter.algo;

import org.jetbrains.annotations.NotNull;

public enum ExperimentType {
    TARGETED,
    WHOLE_GENOME;

    @NotNull
    static ExperimentType toExperimentType(@NotNull String experimentTypeInput) {
        for (ExperimentType experimentType : ExperimentType.values()) {
            if (experimentTypeInput.equals(experimentType.toString())) {
                return experimentType;
            }
        }

        throw new IllegalStateException("Cannot resolve experimentType: " + experimentTypeInput);
    }
}
