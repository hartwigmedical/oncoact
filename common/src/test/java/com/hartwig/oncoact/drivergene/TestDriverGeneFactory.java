package com.hartwig.oncoact.drivergene;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestDriverGeneFactory {

    private TestDriverGeneFactory() {
    }

    @NotNull
    public static ImmutableDriverGene.Builder builder() {
        return ImmutableDriverGene.builder()
                .gene(Strings.EMPTY)
                .reportMissenseAndInframe(false)
                .reportNonsenseAndFrameshift(false)
                .reportSplice(false)
                .reportDeletion(false)
                .reportDisruption(false)
                .reportAmplification(false)
                .reportSomaticHotspot(false)
                .reportGermlineVariant(GermlineReportingMode.NONE)
                .reportGermlineHotspot(GermlineReportingMode.NONE)
                .likelihoodType(DriverCategory.ONCO)
                .reportGermlineDisruption(false)
                .reportPGX(false);
    }
}
