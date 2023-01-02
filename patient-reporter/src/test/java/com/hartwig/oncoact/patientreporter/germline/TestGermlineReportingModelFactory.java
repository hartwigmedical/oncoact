package com.hartwig.oncoact.patientreporter.germline;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

public final class TestGermlineReportingModelFactory {

    private TestGermlineReportingModelFactory() {
    }

    @NotNull
    public static GermlineReportingModel createEmpty() {
        return new GermlineReportingModel(Lists.newArrayList());
    }
}
