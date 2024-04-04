package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class Therapy {

    @NotNull
    public abstract List<HighLevelEvidence> highLevelEvidence();

    @NotNull
    public abstract List<ClinicalStudy> clinicalStudies();
}