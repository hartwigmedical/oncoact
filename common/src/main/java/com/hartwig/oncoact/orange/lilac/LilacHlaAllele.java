package com.hartwig.oncoact.orange.lilac;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class LilacHlaAllele {

    @NotNull
    public abstract String allele();

    public abstract double tumorCopyNumber();

    public abstract double somaticMissense();

    public abstract double somaticNonsenseOrFrameshift();

    public abstract double somaticSplice();

    public abstract double somaticInframeIndel();
}
