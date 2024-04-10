package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class ObservedGeneFusion {

    @NotNull
    public abstract String fiveGene();

    @NotNull
    public abstract String threeGene();

    @NotNull
    public abstract ObservedGeneFusionType type();

    @NotNull
    public abstract String fivePromiscuousTranscript();

    @NotNull
    public abstract String threePromiscuousTranscript();

    @NotNull
    public abstract String fivePromiscuousEnd();

    @NotNull
    public abstract String threePromiscuousStart();

    @NotNull
    public abstract String copies();

    @NotNull
    public abstract PhasedType phasing();

    @NotNull
    public abstract FusionDriverInterpretation driver();

    public static ImmutableObservedGeneFusion.Builder builder() {
        return ImmutableObservedGeneFusion.builder();
    }
}