package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class HlaAlleleFail {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String germlineAllele();

    @NotNull
    public abstract String germlineCopies();

    public static ImmutableHlaAlleleFail.Builder builder() {
        return ImmutableHlaAlleleFail.builder();
    }
}