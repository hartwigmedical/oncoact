package com.hartwig.oncoact.knownfusion;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class KnownFusionCache {

    @NotNull
    private final List<KnownFusionData> knownFusions;
    @NotNull
    private final Map<KnownFusionType, List<KnownFusionData>> knownFusionsByType;

    public KnownFusionCache(@NotNull final List<KnownFusionData> knownFusions,
            @NotNull final Map<KnownFusionType, List<KnownFusionData>> knownFusionsByType) {
        this.knownFusions = knownFusions;
        this.knownFusionsByType = knownFusionsByType;
    }

    @NotNull
    public List<KnownFusionData> knownFusions() {
        return knownFusions;
    }

    @NotNull
    public List<KnownFusionData> fusionsByType(@NotNull KnownFusionType type) {
        return knownFusionsByType.get(type);
    }

    public boolean hasKnownFusion(@NotNull String fiveGene, @NotNull String threeGene) {
        return knownFusionsByType.get(KnownFusionType.KNOWN_PAIR)
                .stream()
                .anyMatch(x -> x.fiveGene().equals(fiveGene) && x.threeGene().equals(threeGene));
    }

    public boolean hasKnownIgFusion(@NotNull String fiveGene, @NotNull String threeGene) {
        return knownFusionsByType.get(KnownFusionType.IG_KNOWN_PAIR)
                .stream()
                .anyMatch(x -> x.fiveGene().equals(fiveGene) && x.threeGene().equals(threeGene));
    }

    public boolean hasKnownPairGene(@NotNull String gene) {
        return knownFusionsByType.get(KnownFusionType.KNOWN_PAIR)
                .stream()
                .anyMatch(x -> x.fiveGene().equals(gene) && x.threeGene().equals(gene));
    }

    public boolean hasPromiscuousIgFusion(@NotNull String gene) {
        return knownFusionsByType.get(KnownFusionType.IG_PROMISCUOUS).stream().anyMatch(x -> x.fiveGene().equals(gene));
    }

    public boolean hasPromiscuousFiveGene(@NotNull String gene) {
        return knownFusionsByType.get(KnownFusionType.PROMISCUOUS_5).stream().anyMatch(x -> x.fiveGene().equals(gene));
    }

    public boolean hasPromiscuousThreeGene(@NotNull String gene) {
        return knownFusionsByType.get(KnownFusionType.PROMISCUOUS_3).stream().anyMatch(x -> x.threeGene().equals(gene));
    }

    public boolean hasAnyIgFusion(@NotNull String gene) {
        return knownFusionsByType.get(KnownFusionType.IG_KNOWN_PAIR)
                .stream()
                .anyMatch(x -> x.fiveGene().equals(gene) || x.threeGene().equals(gene));
    }

    public boolean hasAnyExonDelDup(@NotNull String gene) {
        return knownFusionsByType.get(KnownFusionType.EXON_DEL_DUP)
                .stream()
                .anyMatch(x -> x.fiveGene().equals(gene) && x.threeGene().equals(gene));
    }
}
