package com.hartwig.oncoact.doid;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class DoidRestriction {

    @NotNull
    public abstract String propertyId();

    @NotNull
    public abstract String fillerId();
}
