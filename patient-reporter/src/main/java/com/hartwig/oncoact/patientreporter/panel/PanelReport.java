package com.hartwig.oncoact.patientreporter.panel;

import java.util.Optional;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class PanelReport implements com.hartwig.oncoact.patientreporter.PanelReport {

    @Override
    @NotNull
    public abstract String qsFormNumber();

    @Nullable
    public abstract String pipelineVersion();

    @Override
    @NotNull
    public abstract Optional<String> comments();

    @Override
    public abstract boolean isCorrectedReport();

    @Override
    public abstract boolean isCorrectedReportExtern();

    @Override
    @NotNull
    public abstract String signaturePath();

    @Override
    @NotNull
    public abstract String logoCompanyPath();

    @NotNull
    @Override
    public abstract String reportDate();
}
