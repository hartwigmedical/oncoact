package com.hartwig.oncoact.patientreporter.panel;

import java.util.Optional;

import com.hartwig.oncoact.patientreporter.PanelReport;

import com.hartwig.oncoact.patientreporter.failedreasondb.FailedReason;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class PanelFailReport implements PanelReport {

    static ImmutablePanelFailReport.Builder builder() {
        return ImmutablePanelFailReport.builder();
    }

    @Override
    @NotNull
    public abstract String qsFormNumber();

    @NotNull
    public abstract PanelFailReason panelFailReason();

    @NotNull
    public abstract FailedReason failExplanation();

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
