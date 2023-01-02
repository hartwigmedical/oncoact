package com.hartwig.oncoact.copynumber;

import org.jetbrains.annotations.NotNull;

public enum Chromosome {
    _1,
    _2,
    _3,
    _4,
    _5,
    _6,
    _7,
    _8,
    _9,
    _10,
    _11,
    _12,
    _13,
    _14,
    _15,
    _16,
    _17,
    _18,
    _19,
    _20,
    _21,
    _22,
    _X,
    _Y;

    private static final String CHR_PREFIX = "chr";

    @NotNull
    public static Chromosome fromString(@NotNull String chromosome) {
        if (chromosome.toLowerCase().startsWith("chr")) {
            return Chromosome.valueOf("_" + chromosome.substring(3));
        }

        return Chromosome.valueOf("_" + chromosome);
    }

    public static boolean contains(@NotNull String chromosome) {
        String trimmedContig = stripChrPrefix(chromosome);
        if (isNumeric(trimmedContig)) {
            int integerContig = Integer.parseInt(trimmedContig);
            return integerContig >= 1 && integerContig <= 22;
        }

        return trimmedContig.equals("X") || trimmedContig.equals("Y");
    }

    private static boolean isNumeric(@NotNull String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    private static String stripChrPrefix(@NotNull final String chromosome) {
        if (chromosome.startsWith(CHR_PREFIX)) {
            return chromosome.substring(CHR_PREFIX.length());
        }

        return chromosome;
    }
}
