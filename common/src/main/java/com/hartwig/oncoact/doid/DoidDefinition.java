package com.hartwig.oncoact.doid;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class DoidDefinition {

    @NotNull
    public abstract String definitionVal();

    @NotNull
    public abstract List<String> definitionXrefs();
}
