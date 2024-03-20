package com.hartwig.oncoact.patientreporter.model;

public enum MicrosatelliteStatus {
    MSI("Unstable"),
    MSS("Stable"),
    UNKNOWN("Unknown");

    public final String label;

    MicrosatelliteStatus(String label) {
        this.label = label;
    }
}
