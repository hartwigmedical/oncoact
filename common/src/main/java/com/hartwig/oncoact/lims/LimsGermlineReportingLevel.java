package com.hartwig.oncoact.lims;

import org.jetbrains.annotations.NotNull;

public enum LimsGermlineReportingLevel {
    REPORT_WITH_NOTIFICATION("report with notification"),
    REPORT_WITHOUT_NOTIFICATION("report without notification"),
    NO_REPORTING("no reporting");

    @NotNull
    private final String display;

    LimsGermlineReportingLevel(@NotNull final String display) {
        this.display = display;
    }

    @NotNull
    public String display() {
        return display;
    }
}
