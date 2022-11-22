package com.hartwig.oncoact.common.variant;

import com.hartwig.oncoact.common.genome.position.GenomePosition;

import org.jetbrains.annotations.NotNull;

public interface Variant extends GenomePosition, AllelicDepth {

    @NotNull
    VariantType type();

    @NotNull
    String gene();

    @NotNull
    String ref();

    @NotNull
    String alt();

    @NotNull
    String canonicalTranscript();

    @NotNull
    String canonicalEffect();

    @NotNull
    CodingEffect canonicalCodingEffect();

    @NotNull
    String canonicalHgvsCodingImpact();

    @NotNull
    String canonicalHgvsProteinImpact();
}
