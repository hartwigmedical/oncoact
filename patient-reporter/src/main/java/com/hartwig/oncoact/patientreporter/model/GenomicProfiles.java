package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class GenomicProfiles {

    @NotNull
    public abstract TumorMutationalBurden tumorMutationalBurden();

    @NotNull
    public abstract Microsatellite microsatellite();

    @NotNull
    public abstract HomologousRecombinationDeficiency homologousRecombinationDeficiency();

    public abstract int tumorMutationalLoad();

    public static ImmutableGenomicProfiles.Builder builder() {
        return ImmutableGenomicProfiles.builder();
    }
}