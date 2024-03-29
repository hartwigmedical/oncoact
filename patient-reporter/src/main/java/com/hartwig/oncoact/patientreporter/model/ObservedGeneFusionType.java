package com.hartwig.oncoact.patientreporter.model;

public enum ObservedGeneFusionType {
    NONE("None"),
    PROMISCUOUS_3("3' Promiscuous"),
    PROMISCUOUS_5("5' Promiscuous"),
    PROMISCUOUS_BOTH("5' and 3' Promiscuous"),
    IG_PROMISCUOUS("IG promiscuous"),
    KNOWN_PAIR("Known pair"),
    IG_KNOWN_PAIR("IG known pair"),
    EXON_DEL_DUP("Exon del dup"),
    PROMISCUOUS_ENHANCER_TARGET("promiscuous enhancer target");

    public final String type;

    ObservedGeneFusionType(String type) {
        this.type = type;
    }
}