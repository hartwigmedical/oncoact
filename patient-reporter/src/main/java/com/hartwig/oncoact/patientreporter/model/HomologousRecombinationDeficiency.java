package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class HomologousRecombinationDeficiency {

    public abstract double value();

    @NotNull
    public abstract HomologousRecombinationDeficiencyStatus status();

    public static ImmutableHomologousRecombinationDeficiency.Builder builder() {
        return ImmutableHomologousRecombinationDeficiency.builder();
    }
}