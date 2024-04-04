package com.hartwig.oncoact.patientreporter;

public enum QsFormNumber {
    FOR_082("HMF-FOR-082", false),
    FOR_083("HMF-FOR-083", false),
    FOR_100("HMF-FOR-100", false),
    FOR_102("HMF-FOR-102", false),
    FOR_080("HMF-FOR-080", true),
    FOR_209("HMF-FOR-209", true),
    FOR_344("HMF-FOR-344", false),
    FOR_345("HMF-FOR-345", false);


    public final String number;
    public final boolean isFailed;

    QsFormNumber(String number, boolean isFailed) {
        this.number = number;
        this.isFailed = isFailed;
    }
}