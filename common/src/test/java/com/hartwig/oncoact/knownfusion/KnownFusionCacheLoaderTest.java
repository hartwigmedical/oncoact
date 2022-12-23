package com.hartwig.oncoact.knownfusion;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.google.common.io.Resources;

import org.junit.Test;

public class KnownFusionCacheLoaderTest {

    private static final String KNOWN_FUSION_FILE = Resources.getResource("known_fusion_cache/known_fusion_data.csv").getPath();

    @Test
    public void canLoadKnownFusionCache() throws IOException {
        KnownFusionCache cache = KnownFusionCacheLoader.load(KNOWN_FUSION_FILE);

        assertEquals(2, cache.knownFusions().size());
        // TODO Expand test to cover all fields.
    }
}