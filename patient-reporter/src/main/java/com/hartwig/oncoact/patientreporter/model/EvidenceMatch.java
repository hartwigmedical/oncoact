package com.hartwig.oncoact.patientreporter.model;


import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class EvidenceMatch {

    @NotNull
    public abstract String type();

    @Nullable
    public abstract Integer rank();

    @NotNull
    public abstract String url();
}
