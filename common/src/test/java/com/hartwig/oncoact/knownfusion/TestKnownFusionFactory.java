package com.hartwig.oncoact.knownfusion;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestKnownFusionFactory {

    private TestKnownFusionFactory() {
    }

    @NotNull
    public static KnownFusionCache createEmptyCache() {
        return createCache(Lists.newArrayList());
    }

    @NotNull
    public static KnownFusionCache createCache(@NotNull List<KnownFusionData> knownFusions) {
        return KnownFusionCache.fromKnownFusions(knownFusions);
    }

    @NotNull
    public static ImmutableKnownFusionData.Builder builder() {
        return ImmutableKnownFusionData.builder()
                .type(KnownFusionType.NONE)
                .fiveGene(Strings.EMPTY)
                .threeGene(Strings.EMPTY)
                .cancerTypes(Strings.EMPTY)
                .pubMedId(Strings.EMPTY)
                .highImpactPromiscuous(false)
                .specificExonsTransName(Strings.EMPTY)
                .fiveGeneExonRange(new int[]{})
                .threeGeneExonRange(new int[]{});
    }
}
