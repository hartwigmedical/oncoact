package com.hartwig.oncoact.rose.conclusion;

import com.hartwig.oncoact.common.variant.DriverInterpretation;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class VariantKey {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String variantAnnotation();

    @NotNull
    public abstract DriverInterpretation driverInterpretation();

    public abstract boolean bialleic();
}
