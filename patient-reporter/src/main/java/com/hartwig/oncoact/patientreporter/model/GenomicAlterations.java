package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class GenomicAlterations {

    @NotNull
    public abstract List<String> genesWithDriverMutation();

    @NotNull
    public abstract List<String> amplifiedGenes();

    @NotNull
    public abstract List<String> deletedGenes();

    @NotNull
    public abstract List<String> homozygouslyDisruptedGenes();

    @NotNull
    public abstract List<String> geneFusions();

    @Nullable
    public abstract List<String> potentialHrdGenes();

    @Nullable
    public abstract List<String> potentialMsiGenes();
}
