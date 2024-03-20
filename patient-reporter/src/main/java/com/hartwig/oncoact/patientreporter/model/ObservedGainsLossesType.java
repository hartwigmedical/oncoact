package com.hartwig.oncoact.patientreporter.model;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = {NotNull.class, Nullable.class})
public enum ObservedGainsLossesType {
    FULL_GAIN,
    PARTIAL_GAIN,
    FULL_LOSS,
    PARTIAL_LOSS,
    FULL_GENE,
    PARTIAL_GENE
}
