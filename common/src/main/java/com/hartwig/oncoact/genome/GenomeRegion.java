package com.hartwig.oncoact.genome;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class GenomeRegion {

    @NotNull
    public abstract String chromosome();

    public abstract int start();

    public abstract int end();

}
