package com.hartwig.oncoact.copynumber;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class GenomeRegion {

    @NotNull
    public abstract Chromosome chromosome();

    public abstract int start();

    public abstract int end();

}
