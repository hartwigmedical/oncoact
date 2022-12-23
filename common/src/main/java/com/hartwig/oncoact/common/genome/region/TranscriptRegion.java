package com.hartwig.oncoact.common.genome.region;

import com.hartwig.oncoact.genome.GenomeRegion;

import org.jetbrains.annotations.NotNull;

public interface TranscriptRegion extends GenomeRegion {

    @NotNull
    String transName();

    boolean isCanonical();

    @NotNull
    String geneName();

    @NotNull
    String chromosomeBand();
}
