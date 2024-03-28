package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class PrimaryTumor {

    @NotNull
    public abstract String location();

    @NotNull
    public abstract String type();

    public static ImmutablePrimaryTumor.Builder builder() {
        return ImmutablePrimaryTumor.builder();
    }
}