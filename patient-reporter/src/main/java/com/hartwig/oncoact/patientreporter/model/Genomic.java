package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class Genomic {

    public abstract double purity();

    public abstract double averagePloidy();

    public abstract boolean hasReliablePurity();

    public abstract boolean hasReliableQuality();

    @NotNull
    public abstract List<ObservedVariant> variants();

    @NotNull
    public abstract List<ObservedGainsLosses> gainsLosses();

    @NotNull
    public abstract List<ObservedGeneFusion> geneFusions();

    @NotNull
    public abstract List<ObservedHomozygousDisruption> homozygousDisruptions();

    @NotNull
    public abstract List<LohEvent> lohEventsHrd();

    @NotNull
    public abstract List<LohEvent> lohEventsMsi();

    @NotNull
    public abstract List<ObservedGeneDisruption> geneDisruptions();

    @NotNull
    public abstract List<ObservedViralInsertion> viralInsertions();

    @NotNull
    public abstract List<Pharmacogenetics> pharmacogenetics();

    @NotNull
    public abstract List<HlaAllele> hlaAlleles();

    @NotNull
    public abstract String hlaQc();

    @NotNull
    public abstract GenomicProfiles profiles();

    public static ImmutableGenomic.Builder builder() {
        return ImmutableGenomic.builder();
    }
}