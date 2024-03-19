package com.hartwig.oncoact.patientreporter.model;

import com.hartwig.serve.datamodel.EvidenceLevel;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class HighLevelEvidence {

    @NotNull
    public abstract String drugType();

    public abstract boolean isTumorTypeSpecific();

    @NotNull
    public abstract List<EvidenceMatch> matches();

    @NotNull
    public abstract EvidenceLevel level();

    public abstract boolean isResponsive();

    public abstract boolean isPredicted();

    @NotNull
    public abstract String genomicEvent();
}