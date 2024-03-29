package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class ObservedViralInsertion {

    @NotNull
    public abstract String virus();

    @NotNull
    public abstract String detectedIntegrationSites();

    public abstract long viralCoveragePercentage();

    public abstract VirusDriverInterpretation virusDriverInterpretation();

    public static ImmutableObservedViralInsertion.Builder builder() {
        return ImmutableObservedViralInsertion.builder();
    }
}