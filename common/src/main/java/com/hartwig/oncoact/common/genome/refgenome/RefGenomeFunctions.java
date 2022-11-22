package com.hartwig.oncoact.common.genome.refgenome;

import org.jetbrains.annotations.NotNull;

public final class RefGenomeFunctions {

    private static final String CHR_PREFIX = "chr";

    private RefGenomeFunctions() {
    }

    @NotNull
    public static String stripChrPrefix(@NotNull final String chromosome) {
        if (chromosome.startsWith(CHR_PREFIX)) {
            return chromosome.substring(CHR_PREFIX.length());
        }

        return chromosome;
    }

    @NotNull
    public static String enforceChrPrefix(@NotNull final String chromosome) {
        if (!chromosome.startsWith(CHR_PREFIX)) {
            return CHR_PREFIX + chromosome;
        }

        return chromosome;
    }
}
