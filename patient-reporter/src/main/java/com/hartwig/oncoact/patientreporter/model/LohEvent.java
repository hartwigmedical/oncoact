package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class LohEvent {

    @NotNull
    public abstract String location();

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String tumorMinorAlleleCopies();

    @NotNull
    public abstract String tumorCopies();

    public static ImmutableLohEvent.Builder builder() {
        return ImmutableLohEvent.builder();
    }
}