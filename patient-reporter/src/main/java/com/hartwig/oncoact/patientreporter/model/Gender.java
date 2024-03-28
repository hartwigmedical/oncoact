package com.hartwig.oncoact.patientreporter.model;

public enum Gender {
    MALE("M"),
    FEMALE("F");

    public final String gender;

    Gender(String gender) {
        this.gender = gender;
    }
}