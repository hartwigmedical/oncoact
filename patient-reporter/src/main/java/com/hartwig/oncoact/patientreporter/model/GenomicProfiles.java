package com.hartwig.oncoact.patientreporter.model;

import org.jetbrains.annotations.NotNull;

public abstract class GenomicProfiles {

    @NotNull
    public abstract TumorMutationalBurden tumorMutationalBurden();

    @NotNull
    public abstract Microsatellite microsatellite();

    @NotNull
    public abstract HomologousRecombinationDeficiency homologousRecombinationDeficiency();

    public abstract int tumorMutationalLoad();
}
