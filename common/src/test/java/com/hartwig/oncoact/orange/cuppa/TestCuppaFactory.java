package com.hartwig.oncoact.orange.cuppa;

import com.hartwig.hmftools.datamodel.cuppa.ImmutableCuppaPrediction;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestCuppaFactory {

    private TestCuppaFactory() {
    }

    @NotNull
    public static ImmutableCuppaPrediction.Builder builder() {
        return ImmutableCuppaPrediction.builder().cancerType(Strings.EMPTY).likelihood(0D);
    }
}
