package com.hartwig.oncoact.genome;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import org.jetbrains.annotations.NotNull;

public enum RefGenomeCoordinates {

    COORDS_37(fromResource("lengths.37.tsv"), fromResource("centromeres.37.tsv")),
    COORDS_38(fromResource("lengths.38.tsv"), fromResource("centromeres.38.tsv"));

    private static final String FIELD_SEPARATOR = "\t";

    @NotNull
    private final Map<HumanChromosome, Integer> lengths;
    @NotNull
    private final Map<HumanChromosome, Integer> centromeres;

    RefGenomeCoordinates(@NotNull final Map<HumanChromosome, Integer> lengths, @NotNull final Map<HumanChromosome, Integer> centromeres) {
        this.lengths = lengths;
        this.centromeres = centromeres;
    }

    @NotNull
    public Map<HumanChromosome, Integer> lengths() {
        return lengths;
    }

    @NotNull
    public Map<HumanChromosome, Integer> centromeres() {
        return centromeres;
    }

    public int length(@NotNull String chromosome) {
        if (!HumanChromosome.contains(chromosome)) {
            return 0;
        }

        return lengths.get(HumanChromosome.fromString(chromosome));
    }

    @NotNull
    private static Map<HumanChromosome, Integer> fromResource(@NotNull String resource) {
        InputStream inputStream = RefGenomeCoordinates.class.getResourceAsStream("/refgenome/" + resource);
        return fromLines(new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.toList()));
    }

    @NotNull
    private static Map<HumanChromosome, Integer> fromLines(@NotNull List<String> lines) {
        Map<HumanChromosome, Integer> result = Maps.newHashMap();
        for (String line : lines) {
            String[] values = line.split(FIELD_SEPARATOR);
            result.put(HumanChromosome.fromString(values[0]), Integer.valueOf(values[1]));
        }

        return result;
    }
}
