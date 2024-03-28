package com.hartwig.oncoact.patientreporter.model;

import org.apache.logging.log4j.util.Strings;

public enum VariantDriverInterpretation {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low"),
    UNKNOWN(Strings.EMPTY);

    public final String display;

    VariantDriverInterpretation(String display) {
        this.display = display;
    }
}