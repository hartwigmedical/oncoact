package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class Purity {

    @Nullable
    public abstract Double value();

    @NotNull
    public abstract String label();

    public abstract boolean isReliable();

    public static ImmutablePurity.Builder builder() {
        return ImmutablePurity.builder();
    }
}