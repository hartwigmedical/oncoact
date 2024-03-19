package com.hartwig.oncoact.patientreporter.model;

import org.jetbrains.annotations.NotNull;

public abstract class HomologousRecombinationDeficiency {

    public abstract double value();

    @NotNull
    public abstract HomologousRecombinationDeficiencyStatus status();
}
