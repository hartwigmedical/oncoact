package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class TumorCharacteristics {

    public abstract double purity();

    @NotNull
    public abstract TissueOfOriginPrediction tissueOfOriginPrediction();

    @NotNull
    public abstract TumorMutationalBurden tumorMutationalBurden();

    @NotNull
    public abstract HomologousRecombinationDeficiency homologousRecombinationDeficiency();

    @NotNull
    public abstract List<String> viruses();


}
