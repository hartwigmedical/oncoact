package com.hartwig.oncoact.doid;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class DoidSynonym {

    @NotNull
    public abstract String pred();

    @NotNull
    public abstract String val();

    @NotNull
    public abstract List<String> xrefs();
}

