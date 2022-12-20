package com.hartwig.oncoact.drivergene;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class DriverGene {

    @NotNull
    public abstract String gene();

    public abstract boolean reportMissenseAndInframe();

    public abstract boolean reportNonsenseAndFrameshift();

    public abstract boolean reportSplice();

    public abstract boolean reportDeletion();

    public abstract boolean reportDisruption();

    public abstract boolean reportAmplification();

    public abstract boolean reportSomaticHotspot();

    @NotNull
    public abstract GermlineReportingMode reportGermlineVariant();

    @NotNull
    public abstract GermlineReportingMode reportGermlineHotspot();

    @NotNull
    public abstract DriverCategory likelihoodType();

    public abstract boolean reportGermlineDisruption();

    public abstract List<String> additionalReportedTranscripts();

    public abstract boolean reportPGX();
}
