package com.hartwig.oncoact.variant;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public enum DriverInterpretation {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low"),
    EMPTY(Strings.EMPTY);

    @NotNull
    private final String display;

    DriverInterpretation(@NotNull final String display) {
        this.display = display;
    }

    @NotNull
    public String display() {
        return display;
    }

    @NotNull
    public static DriverInterpretation interpret(Double driverLikelihood) {
        if (driverLikelihood == null) {
            return EMPTY;
        } else {
            if (driverLikelihood > 0.8) {
                return HIGH;
            } else if (driverLikelihood > 0.2) {
                return MEDIUM;
            } else {
                return LOW;
            }
        }
    }
}
