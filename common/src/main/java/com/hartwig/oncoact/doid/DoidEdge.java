package com.hartwig.oncoact.doid;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class DoidEdge {

    @NotNull
    public abstract String subject();

    @NotNull
    public abstract String object();

    @NotNull
    public abstract String predicate();
}
