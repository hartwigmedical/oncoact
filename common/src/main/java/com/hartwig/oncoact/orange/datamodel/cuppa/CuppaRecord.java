package com.hartwig.oncoact.orange.datamodel.cuppa;

import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CuppaRecord {

    @NotNull
    public abstract Set<CuppaPrediction> predictions();

}
