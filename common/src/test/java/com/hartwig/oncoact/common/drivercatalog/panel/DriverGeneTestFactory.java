package com.hartwig.oncoact.common.drivercatalog.panel;

import com.hartwig.oncoact.common.drivercatalog.DriverCategory;

import org.apache.logging.log4j.util.Strings;

public final class DriverGeneTestFactory
{
    public static ImmutableDriverGene.Builder builder()
    {
        return ImmutableDriverGene.builder()
                .gene(Strings.EMPTY)
                .reportMissenseAndInframe(false)
                .reportNonsenseAndFrameshift(false)
                .reportSplice(false)
                .reportDeletion(false)
                .reportDisruption(false)
                .reportAmplification(false)
                .reportSomaticHotspot(false)
                .reportGermlineVariant(DriverGeneGermlineReporting.NONE)
                .reportGermlineHotspot(DriverGeneGermlineReporting.NONE)
                .likelihoodType(DriverCategory.ONCO)
                .reportGermlineDisruption(false)
                .reportPGX(false);
    }
}
