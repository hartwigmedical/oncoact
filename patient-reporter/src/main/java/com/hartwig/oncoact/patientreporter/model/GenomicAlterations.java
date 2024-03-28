package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class GenomicAlterations {

    @NotNull
    public abstract String genesWithDriverMutation();

    @NotNull
    public abstract String amplifiedGenes();

    @NotNull
    public abstract String deletedGenes();

    @NotNull
    public abstract String homozygouslyDisruptedGenes();

    @NotNull
    public abstract String geneFusions();

    @Nullable
    public abstract String potentialHrdGenes();

    @Nullable
    public abstract String potentialMsiGenes();

    public static ImmutableGenomicAlterations.Builder builder() {
        return ImmutableGenomicAlterations.builder();
    }
}
