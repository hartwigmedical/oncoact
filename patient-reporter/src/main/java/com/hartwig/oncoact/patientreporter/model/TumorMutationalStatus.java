package com.hartwig.oncoact.patientreporter.model;

public enum TumorMutationalStatus {
    HIGH("High"),
    LOW("Low"),
    UNKNOWN("Unknown");

    public final String label;

    TumorMutationalStatus(String label) {
        this.label = label;
    }
}
