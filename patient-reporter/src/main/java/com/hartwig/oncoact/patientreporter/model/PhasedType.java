package com.hartwig.oncoact.patientreporter.model;

public enum PhasedType {
    INFRAME("Inframe"),
    SKIPPED_EXONS("Skipped exons"),
    OUT_OF_FRAME("Out of frame");

    public final String type;

    PhasedType(String type) {
        this.type = type;
    }
}