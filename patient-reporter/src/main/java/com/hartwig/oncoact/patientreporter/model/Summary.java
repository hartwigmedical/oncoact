package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class Summary {

    @NotNull
    public abstract String titleReport();

    @NotNull
    public abstract String mostRelevantFindings();

    @Nullable
    public abstract String specialRemark();

    @NotNull
    public abstract TumorCharacteristics tumorCharacteristics();

    @NotNull
    public abstract GenomicAlterations genomicAlterations();

    @NotNull
    public abstract List<PharmacogeneticsGenotype> pharmacogenetics();

    @NotNull
    public abstract List<HlaAlleleSummary> hlaAlleles();

    public abstract boolean hlaQc();

    public static ImmutableSummary.Builder builder() {
        return ImmutableSummary.builder();
    }
}