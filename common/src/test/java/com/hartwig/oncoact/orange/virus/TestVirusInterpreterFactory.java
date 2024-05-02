package com.hartwig.oncoact.orange.virus;

import com.hartwig.hmftools.datamodel.virus.ImmutableVirusInterpreterEntry;
import com.hartwig.hmftools.datamodel.virus.VirusBreakendQCStatus;
import com.hartwig.hmftools.datamodel.virus.VirusLikelihoodType;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestVirusInterpreterFactory {

    private TestVirusInterpreterFactory() {
    }

    @NotNull
    public static ImmutableVirusInterpreterEntry.Builder builder() {
        return ImmutableVirusInterpreterEntry.builder()
                .reported(true)
                .name(Strings.EMPTY)
                .qcStatus(VirusBreakendQCStatus.NO_ABNORMALITIES)
                .integrations(0)
                .driverLikelihood(VirusLikelihoodType.LOW)
                .meanCoverage(0)
                .percentageCovered(0D);
    }
}
