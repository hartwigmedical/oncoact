package com.hartwig.oncoact.lims;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class LimsShallowSeqData {

    @NotNull
    public abstract String sampleBarcode();

    @NotNull
    public abstract String sampleId();

    @NotNull
    public abstract String purityShallowSeq();

    public abstract boolean hasReliableQuality();

    public abstract boolean hasReliablePurity();
}
