package com.hartwig.oncoact.orange.linx;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class LinxBreakend {

    public abstract boolean reported();

    public abstract boolean disruptive();

    public abstract int svId();

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String chromosome();

    @NotNull
    public abstract String chrBand();

    @NotNull
    public abstract String transcriptId();

    public abstract boolean canonical();

    @NotNull
    public abstract LinxBreakendType type();

    public abstract double junctionCopyNumber();

    public abstract double undisruptedCopyNumber();

    public abstract int nextSpliceExonRank();

    public abstract int exonUp();

    public abstract int exonDown();

    @NotNull
    public abstract String geneOrientation();

    public abstract int orientation();

    public abstract int strand();

    @NotNull
    public abstract LinxRegionType regionType();

    @NotNull
    public abstract LinxCodingType codingType();

}
