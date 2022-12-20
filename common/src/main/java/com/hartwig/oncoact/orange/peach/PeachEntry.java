package com.hartwig.oncoact.orange.peach;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class PeachEntry {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String haplotype();

    @NotNull
    public abstract String function();
}
