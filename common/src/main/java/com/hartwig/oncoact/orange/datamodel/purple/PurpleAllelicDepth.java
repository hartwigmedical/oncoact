package com.hartwig.oncoact.orange.datamodel.purple;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class PurpleAllelicDepth {

    public abstract int totalReadCount();

    public abstract int alleleReadCount();
}
