package com.hartwig.oncoact.genome;

import org.jetbrains.annotations.NotNull;

public interface GenomeRegion extends Comparable<GenomeRegion> {

    @NotNull
    String chromosome();

    int start();
    int end();

    default int bases() {
        return 1 + end() - start();
    }

    @Override
    default int compareTo(@NotNull GenomeRegion other) {
        if (chromosome().equals(other.chromosome())) {
            if (start() < other.start()) {
                return -1;
            } else if (start() == other.start()) {
                return 0;
            }
            return 1;
        }

        return ContigComparator.INSTANCE.compare(chromosome(), other.chromosome());
    }

    default boolean overlaps(@NotNull GenomeRegion other) {
        return other.chromosome().equals(chromosome()) && other.end() > start() && other.start() < end();
    }
}
