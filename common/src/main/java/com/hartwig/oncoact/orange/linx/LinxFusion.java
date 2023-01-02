package com.hartwig.oncoact.orange.linx;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class LinxFusion {

    public abstract boolean reported();

    @NotNull
    public abstract LinxFusionType type();

    @NotNull
    public abstract String name();

    @NotNull
    public abstract String geneStart();

    @NotNull
    public abstract String geneTranscriptStart();

    @NotNull
    public abstract String geneContextStart();

    public abstract int fusedExonUp();

    @NotNull
    public abstract String geneEnd();

    @NotNull
    public abstract String geneTranscriptEnd();

    @NotNull
    public abstract String geneContextEnd();

    public abstract int fusedExonDown();

    @Nullable
    public abstract LinxFusionDriverLikelihood driverLikelihood();

    @NotNull
    public abstract LinxPhasedType phased();

    public abstract double junctionCopyNumber();


}
