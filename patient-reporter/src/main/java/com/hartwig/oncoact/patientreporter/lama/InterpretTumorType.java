package com.hartwig.oncoact.patientreporter.lama;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class InterpretTumorType {

    @NotNull
    public abstract String location();

    @NotNull
    public abstract String type();
}
