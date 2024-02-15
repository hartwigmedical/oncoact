package com.hartwig.oncoact.variant;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum DriverInterpretation {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low");

    @NotNull
    private final String display;

    DriverInterpretation(@NotNull final String display) {
        this.display = display;
    }

    @NotNull
    public String display() {
        return display;
    }

    @Nullable
    public static DriverInterpretation interpret(Double driverLikelihood) {
        if (driverLikelihood == null) {
            return null;
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

    public static String display(@Nullable DriverInterpretation interpretation) {
        return interpretation == null ? Strings.EMPTY : interpretation.display();
    }
}
