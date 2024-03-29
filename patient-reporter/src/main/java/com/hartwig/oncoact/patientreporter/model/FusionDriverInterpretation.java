package com.hartwig.oncoact.patientreporter.model;

public enum FusionDriverInterpretation {
    HIGH("High"),
    LOW("Low"),
    NA("Na");

    public final String value;

    FusionDriverInterpretation(String value) {
        this.value = value;
    }
}