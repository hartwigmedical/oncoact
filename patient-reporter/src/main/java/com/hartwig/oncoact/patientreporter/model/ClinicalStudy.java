package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class ClinicalStudy {

    @NotNull
    public abstract String clinicalStudy();

    @NotNull
    public abstract List<EvidenceMatch> matches();

    @NotNull
    public abstract String genomicEvent();

    public static ImmutableClinicalStudy.Builder builder() {
        return ImmutableClinicalStudy.builder();
    }
}