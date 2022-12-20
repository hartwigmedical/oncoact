package com.hartwig.oncoact.orange.purple;

import java.util.List;
import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class PurpleVariant implements Variant {

    public abstract boolean reported();

    @NotNull
    @Override
    public abstract PurpleVariantType type();

    @NotNull
    @Override
    public abstract String gene();

    @NotNull
    @Override
    public abstract String chromosome();

    @Override
    public abstract int position();

    @NotNull
    @Override
    public abstract String ref();

    @NotNull
    @Override
    public abstract String alt();

    public abstract double adjustedCopyNumber();

    public abstract double minorAlleleCopyNumber();

    public abstract double variantCopyNumber();

    @NotNull
    public abstract PurpleHotspotType hotspot();

    @NotNull
    public abstract PurpleAllelicDepth tumorDepth();

    public abstract double subclonalLikelihood();

    public abstract boolean biallelic();

    @NotNull
    public abstract PurpleGenotypeStatus genotypeStatus();

    @Nullable
    public abstract List<Integer> localPhaseSets();

    @NotNull
    public abstract PurpleTranscriptImpact canonicalImpact();

    @NotNull
    public abstract Set<PurpleTranscriptImpact> otherImpacts();
}
