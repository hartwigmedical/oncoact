package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class Patient {

    @NotNull
    public abstract String name();

    @NotNull
    public abstract Gender gender();

    @Nullable
    public abstract String birthDate();

    public static ImmutablePatient.Builder builder() {
        return ImmutablePatient.builder();
    }
}