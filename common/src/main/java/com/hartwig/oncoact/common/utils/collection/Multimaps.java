package com.hartwig.oncoact.common.utils.collection;

import java.util.Collection;
import java.util.function.Predicate;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.hartwig.oncoact.genome.Chromosome;
import com.hartwig.oncoact.genome.GenomePosition;
import com.hartwig.oncoact.genome.GenomeRegion;
import com.hartwig.oncoact.genome.HumanChromosome;

import org.jetbrains.annotations.NotNull;

public final class Multimaps {

    private Multimaps() {
    }

    @NotNull
    public static <T extends GenomeRegion> ListMultimap<Chromosome, T> fromRegions(@NotNull final Collection<T> regions) {
        final ListMultimap<Chromosome, T> result = ArrayListMultimap.create();
        for (T region : regions) {
            if (HumanChromosome.contains(region.chromosome())) {
                result.put(HumanChromosome.fromString(region.chromosome()), region);
            }
        }

        return result;
    }

    @NotNull
    public static <T extends GenomePosition> ListMultimap<Chromosome, T> fromPositions(@NotNull final Collection<T> regions) {
        final ListMultimap<Chromosome, T> result = ArrayListMultimap.create();
        for (T region : regions) {
            if (HumanChromosome.contains(region.chromosome())) {
                result.put(HumanChromosome.fromString(region.chromosome()), region);
            }
        }

        return result;
    }

    @NotNull
    public static <T,U> ListMultimap<T, U> filterEntries(@NotNull final ListMultimap<T, U> map, @NotNull final Predicate<U> predicate) {
        final ListMultimap<T, U> result = ArrayListMultimap.create();
        for (T key : map.keySet()) {
            for (U value : map.get(key)) {
                if (predicate.test(value)) {
                    result.put(key, value);
                }
            }
        }
        return result;
    }
}
