package com.hartwig.oncoact.patientreporter.model;

public enum HomologousRecombinationDeficiencyStatus {
    HR_DEFICIENT("Deficient"),
    HR_PROFICIENT("Proficient"),
    CANNOT_BE_DETERMINED("Cannot be determined"),
    UNKNOWN("Unknown");

    public final String label;

    HomologousRecombinationDeficiencyStatus(String label) {
        this.label = label;
    }
}
