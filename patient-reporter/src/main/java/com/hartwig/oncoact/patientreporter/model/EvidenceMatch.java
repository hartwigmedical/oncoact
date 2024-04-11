package com.hartwig.oncoact.patientreporter.model;


import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class EvidenceMatch {

    @NotNull
    public abstract EvidenceType type();

    @NotNull
    public abstract String rank();

    @NotNull
    public abstract Set<String> url();

    public static ImmutableEvidenceMatch.Builder builder() {
        return ImmutableEvidenceMatch.builder();
    }
}