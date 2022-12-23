package com.hartwig.oncoact.rose;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ActionabilityConclusion {

    @NotNull
    public abstract List<String> conclusion();
}