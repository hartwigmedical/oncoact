package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class Pharmacogenetics {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String genotype();

    @NotNull
    public abstract String function();

    @NotNull
    public abstract String linkedDrugs();

    @NotNull
    public abstract PharmacogeneticsSource source();

    public static ImmutablePharmacogenetics.Builder builder() {
        return ImmutablePharmacogenetics.builder();
    }
}