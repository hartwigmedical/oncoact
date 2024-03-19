package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class Biopsy {

    @NotNull
    public abstract String location();

    @NotNull
    public abstract String subLocation();

    @NotNull
    public abstract Lateralisation lateralisation();

    @NotNull
    public abstract String isPrimaryTumor();
}
