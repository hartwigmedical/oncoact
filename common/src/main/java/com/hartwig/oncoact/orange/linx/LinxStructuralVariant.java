package com.hartwig.oncoact.orange.linx;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class LinxStructuralVariant {

    public abstract int svId();

    public abstract int clusterId();
}
