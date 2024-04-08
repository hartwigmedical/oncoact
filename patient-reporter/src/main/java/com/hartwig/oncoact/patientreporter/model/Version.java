package com.hartwig.oncoact.patientreporter.model;


import com.hartwig.oncoact.patientreporter.QsFormNumber;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class Version {
    @Nullable
    public abstract String molecularPipeline();

    @NotNull
    public abstract String reportingPipeline();

    @NotNull
    public abstract String udiDi();

    @NotNull
    public abstract QsFormNumber qsFormNumber();

    public static ImmutableVersion.Builder builder() {
        return ImmutableVersion.builder();
    }
}