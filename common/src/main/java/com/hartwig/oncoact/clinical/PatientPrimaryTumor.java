package com.hartwig.oncoact.clinical;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class PatientPrimaryTumor {

    @NotNull
    public abstract String patientIdentifier();

    @NotNull
    public abstract String location();

    @NotNull
    public abstract String subLocation();

    @NotNull
    public abstract String type();

    @NotNull
    public abstract String subType();

    @NotNull
    public abstract String extraDetails();

    @NotNull
    public abstract List<String> doids();

    @NotNull
    public abstract List<String> snomedConceptIds();

    public abstract boolean isOverridden();

}
