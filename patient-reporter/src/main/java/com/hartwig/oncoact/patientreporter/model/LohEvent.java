package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public abstract class LohEvent {

    @NotNull
    public abstract String location();

    @NotNull
    public abstract String gene();

    public abstract int tumorMinorAlleleCopies();

    public abstract int tumorCopies();
}
