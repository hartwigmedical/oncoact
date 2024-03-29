package com.hartwig.oncoact.patientreporter.model;

public enum VirusDriverInterpretation {
    HIGH("High"),
    LOW("Low"),
    UNKNOWN("Unknown");

    public final String display;

    VirusDriverInterpretation(String display) {
        this.display = display;
    }
}