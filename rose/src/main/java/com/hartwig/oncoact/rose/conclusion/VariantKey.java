package com.hartwig.oncoact.rose.conclusion;

import com.hartwig.oncoact.variant.DriverInterpretation;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class VariantKey {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String variantAnnotation();

    @Nullable
    public abstract DriverInterpretation driverInterpretation();

    @Nullable
    public abstract Boolean biallelic();

    @NotNull
    public abstract String canonicalEffect();
}
