package com.hartwig.oncoact.knownfusion;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class KnownFusionData {

    @NotNull
    public abstract KnownFusionType type();

    @NotNull
    public abstract String fiveGene();

    @NotNull
    public abstract String threeGene();

    @NotNull
    public abstract String cancerTypes();

    @NotNull
    public abstract String pubMedId();

    public abstract boolean highImpactPromiscuous();

    @NotNull
    public abstract String specificExonsTransName();

    @NotNull
    public abstract int[] fiveGeneExonRange();

    @NotNull
    public abstract int[] threeGeneExonRange();

}
