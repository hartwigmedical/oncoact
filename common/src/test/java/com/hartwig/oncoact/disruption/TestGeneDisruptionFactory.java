package com.hartwig.oncoact.disruption;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestGeneDisruptionFactory {

    private TestGeneDisruptionFactory() {
    }

    @NotNull
    public static ImmutableGeneDisruption.Builder builder() {
        return ImmutableGeneDisruption.builder()
                .location(Strings.EMPTY)
                .gene(Strings.EMPTY)
                .transcriptId(Strings.EMPTY)
                .isCanonical(false)
                .range(Strings.EMPTY)
                .type(Strings.EMPTY)
                .junctionCopyNumber(0D)
                .undisruptedCopyNumber(0D)
                .firstAffectedExon(0);
    }
}
