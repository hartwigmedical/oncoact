package com.hartwig.oncoact.common.drivercatalog.dnds;

import com.hartwig.oncoact.common.variant.CodingEffect;
import com.hartwig.oncoact.genome.GenomePosition;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface DndsVariant extends GenomePosition
{

    @NotNull
    String sampleId();

    @NotNull
    String ref();

    @NotNull
    String alt();

    @NotNull
    String gene();

    boolean biallelic();

    boolean hotspot();

    @NotNull
    CodingEffect worstCodingEffect();

    @NotNull
    CodingEffect canonicalCodingEffect();

    int repeatCount();
}
