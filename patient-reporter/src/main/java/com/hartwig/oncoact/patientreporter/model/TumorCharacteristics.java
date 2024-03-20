package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class TumorCharacteristics {

    @NotNull
    public abstract Purity purity();

    @NotNull
    public abstract String tissueOfOriginPrediction();

    @NotNull
    public abstract String tumorMutationalBurden();

    @NotNull
    public abstract String microsatellite();

    @NotNull
    public abstract String homologousRecombinationDeficiency();

    @NotNull
    public abstract String viruses();

    public static ImmutableTumorCharacteristics.Builder builder() {
        return ImmutableTumorCharacteristics.builder();
    }
}
