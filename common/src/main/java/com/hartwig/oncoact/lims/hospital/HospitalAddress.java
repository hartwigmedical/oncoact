package com.hartwig.oncoact.lims.hospital;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class HospitalAddress {

    @NotNull
    public abstract String hospitalName();

    @NotNull
    public abstract String hospitalZip();

    @NotNull
    public abstract String hospitalCity();
}
