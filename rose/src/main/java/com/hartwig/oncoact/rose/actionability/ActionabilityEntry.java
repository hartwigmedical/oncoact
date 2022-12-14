package com.hartwig.oncoact.rose.actionability;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ActionabilityEntry {

    @NotNull
    public abstract String match();

    @NotNull
    public abstract TypeAlteration type();

    public abstract Condition condition();

    @NotNull
    public abstract String conclusion();
}