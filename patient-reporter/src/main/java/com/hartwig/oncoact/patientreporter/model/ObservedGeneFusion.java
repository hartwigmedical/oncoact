package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class ObservedGeneFusion {

    @NotNull
    public abstract String name();

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

    public abstract int copies();

    @NotNull
    public abstract PhasedType phasing();

    @NotNull
    public abstract FusionDriverInterpretation driver();

}
