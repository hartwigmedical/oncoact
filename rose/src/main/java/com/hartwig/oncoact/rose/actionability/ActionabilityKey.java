package com.hartwig.oncoact.rose.actionability;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ActionabilityKey {

    @NotNull
    public abstract String match();

    @Nullable
    public abstract TypeAlteration type();
}
