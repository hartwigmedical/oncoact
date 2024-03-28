package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class Sample {
    @NotNull
    public abstract LocalDate arrivalDate();

    @NotNull
    public abstract String sampleBarcode();

    public static ImmutableSample.Builder builder() {
        return ImmutableSample.builder();
    }
}