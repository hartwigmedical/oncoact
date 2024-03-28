package com.hartwig.oncoact.patientreporter.model;

import org.apache.logging.log4j.util.Strings;

public enum ObservedGainsLossesType {
    FULL_GAIN("full gain"),
    PARTIAL_GAIN("partial gain"),
    FULL_LOSS("full loss"),
    PARTIAL_LOSS("partial loss"),
    UNKNOWN(Strings.EMPTY);

    public final String display;

    ObservedGainsLossesType(String display) {
        this.display = display;
    }
}