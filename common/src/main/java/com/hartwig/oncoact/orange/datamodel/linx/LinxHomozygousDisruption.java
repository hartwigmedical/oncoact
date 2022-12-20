package com.hartwig.oncoact.orange.datamodel.linx;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class LinxHomozygousDisruption {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String transcript();

    public abstract boolean isCanonical();
}
