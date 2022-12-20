package com.hartwig.oncoact.orange.chord;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ChordRecord {

    public abstract double hrdValue();

    @NotNull
    public abstract ChordStatus hrStatus();
}
