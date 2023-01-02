package com.hartwig.oncoact.copynumber;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CnPerChromosomeArmData {

    @NotNull
    public abstract Chromosome chromosome();

    @NotNull
    public abstract ChromosomeArm chromosomeArm();

    public abstract double copyNumber();

}
