package com.hartwig.oncoact.patientreporter.model;

public enum HomologousRecombinationDeficiencyStatus {
    HR_DEFICIENT("Deficient", true),
    HR_PROFICIENT("Proficient", true),
    CANNOT_BE_DETERMINED("Cannot be determined", false),
    UNKNOWN("Unknown", false);

    public final String label;
    public final boolean isReliable;

    HomologousRecombinationDeficiencyStatus(String label, boolean isReliable) {

        this.label = label;
        this.isReliable = isReliable;
    }
}