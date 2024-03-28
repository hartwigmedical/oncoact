package com.hartwig.oncoact.patientreporter.model;

public enum Lateralisation {
    LEFT("Left"),
    RIGHT("Right"),
    UNKNOWN("Unknown"),
    NULL("-");

    public final String lateralisation;

    Lateralisation(String lateralisation) {
        this.lateralisation = lateralisation;
    }
}