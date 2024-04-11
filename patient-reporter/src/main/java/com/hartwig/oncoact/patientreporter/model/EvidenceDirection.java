package com.hartwig.oncoact.patientreporter.model;

public enum EvidenceDirection {

    RESPONSIVE(true, false),
    PREDICTED_RESPONSIVE(true, true),
    NO_BENEFIT(false, false),
    RESISTANT(false, false),
    PREDICTED_RESISTANT(false, true);

    private final boolean isResponsive;
    private final boolean isPredicted;

    EvidenceDirection(boolean isResponsive, boolean isPredicted) {
        this.isResponsive = isResponsive;
        this.isPredicted = isPredicted;
    }

    public boolean isResponsive() {
        return this.isResponsive;
    }

    public boolean isPredicted() {
        return this.isPredicted;
    }
}