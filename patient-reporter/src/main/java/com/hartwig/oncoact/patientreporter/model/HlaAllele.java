package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class HlaAllele {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String germlineAllele();

    @NotNull
    public abstract String germlineCopies();

    @NotNull
    public abstract String tumorCopies();

    @NotNull
    public abstract String numberSomaticMutations();

    @NotNull
    public abstract String interpretationPresenceInTumor();

    public static ImmutableHlaAllele.Builder builder() {
        return ImmutableHlaAllele.builder();
    }
}