package com.hartwig.oncoact.patientreporter.model;

import org.apache.logging.log4j.util.Strings;

public enum Hotspot {
    HOTSPOT("Yes"),
    NEAR_HOTSPOT("Near"),
    UNKNOWN(Strings.EMPTY);

    public final String display;

    Hotspot(String display) {
        this.display = display;
    }
}