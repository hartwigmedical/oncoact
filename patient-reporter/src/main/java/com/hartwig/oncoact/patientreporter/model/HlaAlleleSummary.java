package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class HlaAlleleSummary {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract List<String> germlineAlleles();
}
