package com.hartwig.oncoact.patientreporter.panel;

import java.util.Optional;

import com.hartwig.oncoact.patientreporter.PanelReport;
import com.hartwig.oncoact.reporting.SampleReport;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class PanelFailReport implements PanelReport {

    @Override
    @NotNull
    public abstract SampleReport sampleReport();

    @Override
    @NotNull
    public abstract String qsFormNumber();

    @NotNull
    public abstract PanelFailReason panelFailReason();

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
