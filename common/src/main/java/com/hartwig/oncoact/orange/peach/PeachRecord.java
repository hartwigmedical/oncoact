package com.hartwig.oncoact.orange.peach;

import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class PeachRecord {

    @NotNull
    public abstract Set<PeachEntry> entries();
}
