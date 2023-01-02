package com.hartwig.oncoact.knownfusion;

import java.util.List;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.jetbrains.annotations.NotNull;

public class KnownFusionCache {

    @NotNull
    private final List<KnownFusionData> knownFusions;
    @NotNull
    private final Map<KnownFusionType, List<KnownFusionData>> knownFusionsByType;

    @NotNull
    static KnownFusionCache fromKnownFusions(@NotNull List<KnownFusionData> knownFusions) {
        Map<KnownFusionType, List<KnownFusionData>> knownFusionsByType = Maps.newHashMap();
        for (KnownFusionType type : KnownFusionType.values()) {
            if (type != KnownFusionType.NONE) {
                knownFusionsByType.put(type, Lists.newArrayList());
            }
        }

        for (KnownFusionData knownFusion : knownFusions) {
            if (knownFusion.type() != KnownFusionType.NONE) {
                List<KnownFusionData> current = knownFusionsByType.get(knownFusion.type());
                current.add(knownFusion);
                knownFusionsByType.put(knownFusion.type(), current);
            }
        }

        return new KnownFusionCache(knownFusions, knownFusionsByType);
    }

    private KnownFusionCache(@NotNull final List<KnownFusionData> knownFusions,
            @NotNull final Map<KnownFusionType, List<KnownFusionData>> knownFusionsByType) {
        this.knownFusions = knownFusions;
        this.knownFusionsByType = knownFusionsByType;
    }

    @NotNull
    @VisibleForTesting
    List<KnownFusionData> knownFusions() {
        return knownFusions;
    }

    @NotNull
    public List<KnownFusionData> fusionsByType(@NotNull KnownFusionType type) {
        return knownFusionsByType.get(type);
    }
}
