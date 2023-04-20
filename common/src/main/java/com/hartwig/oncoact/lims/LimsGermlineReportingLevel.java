package com.hartwig.oncoact.lims;

import com.hartwig.oncoact.lims.cohort.LimsCohortConfig;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @NotNull
    static LimsGermlineReportingLevel fromLimsInputs(boolean limsSampleReportGermlineVariants, @NotNull String germlineReportingLevelString,
            @NotNull String sampleId, @Nullable LimsCohortConfig cohort) {
        return REPORT_WITHOUT_NOTIFICATION;
    }
}
