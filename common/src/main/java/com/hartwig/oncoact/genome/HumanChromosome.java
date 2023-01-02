package com.hartwig.oncoact.genome;

import org.jetbrains.annotations.NotNull;

public enum HumanChromosome implements Chromosome {
    _1(true, false),
    _2(true, false),
    _3(true, false),
    _4(true, false),
    _5(true, false),
    _6(true, false),
    _7(true, false),
    _8(true, false),
    _9(true, false),
    _10(true, false),
    _11(true, false),
    _12(true, false),
    _13(true, false),
    _14(true, false),
    _15(true, false),
    _16(true, false),
    _17(true, false),
    _18(true, false),
    _19(true, false),
    _20(true, false),
    _21(true, false),
    _22(true, false),
    _X(false, true),
    _Y(false, true);

    private static final String CHR_PREFIX = "chr";

    private final boolean isAutosome;
    private final boolean isAllosome;
    @NotNull
    private final String name;

    HumanChromosome(final boolean isAutosome, boolean isAllosome) {
        this.isAutosome = isAutosome;
        this.isAllosome = isAllosome;
        this.name = name().substring(1).intern();
    }

    @Override
    public boolean isAutosome() {
        return isAutosome;
    }

    @Override
    public boolean isAllosome() {
        return isAllosome;
    }

    @NotNull
    public static Chromosome valueOf(@NotNull GenomePosition position) {
        return fromString(position.chromosome());
    }

    @NotNull
    public static Chromosome valueOf(@NotNull GenomeRegion region) {
        return fromString(region.chromosome());
    }

    @NotNull
    public static HumanChromosome fromString(@NotNull String chromosome) {
        if (chromosome.toLowerCase().startsWith("chr")) {
            return HumanChromosome.valueOf("_" + chromosome.substring(3));
        }

        return HumanChromosome.valueOf("_" + chromosome);
    }

    public static boolean contains(@NotNull String chromosome) {
        final String trimmedContig = stripChrPrefix(chromosome);
        if (isNumeric(trimmedContig)) {
            final int integerContig = Integer.parseInt(trimmedContig);
            return integerContig >= 1 && integerContig <= 22;
        }

        return trimmedContig.equals("X") || trimmedContig.equals("Y");
    }

    @Override
    public String toString() {
        return name;
    }

    private static boolean isNumeric(@NotNull String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int chromosomeRank(final String chromosome) {
        String chrTrimmed = stripChrPrefix(chromosome);

        if (chrTrimmed.equalsIgnoreCase("X")) {
            return 23;
        } else if (chrTrimmed.equalsIgnoreCase("Y")) {
            return 24;
        } else if (chrTrimmed.equalsIgnoreCase("MT") || chrTrimmed.equalsIgnoreCase("M")) {
            return 25;
        } else {
            try {
                return Integer.parseInt(chrTrimmed);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }

    @NotNull
    private static String stripChrPrefix(@NotNull final String chromosome) {
        if (chromosome.startsWith(CHR_PREFIX)) {
            return chromosome.substring(CHR_PREFIX.length());
        }

        return chromosome;
    }
}
