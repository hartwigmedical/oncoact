package com.hartwig.oncoact.common.virus;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class VirusTestFactory {

    private VirusTestFactory() {
    }

    @NotNull
    public static ImmutableAnnotatedVirus.Builder testAnnotatedVirusBuilder() {
        return ImmutableAnnotatedVirus.builder()
                .taxid(0)
                .name(Strings.EMPTY)
                .interpretation(Strings.EMPTY)
                .qcStatus(VirusBreakendQCStatus.NO_ABNORMALITIES)
                .integrations(0)
                .percentageCovered(1.0)
                .meanCoverage(1.0)
                .expectedClonalCoverage(1.0)
                .reported(false)
                .virusDriverLikelihoodType(VirusLikelihoodType.HIGH);
    }
}
