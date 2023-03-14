package com.hartwig.oncoact.orange.virus;

import com.hartwig.hmftools.datamodel.virus.ImmutableAnnotatedVirus;
import com.hartwig.hmftools.datamodel.virus.VirusBreakendQCStatus;
import com.hartwig.hmftools.datamodel.virus.VirusLikelihoodType;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestVirusInterpreterFactory {

    private TestVirusInterpreterFactory() {
    }

    @NotNull
    public static ImmutableAnnotatedVirus.Builder builder() {
        return ImmutableAnnotatedVirus.builder()
                .reported(true)
                .name(Strings.EMPTY)
                .qcStatus(VirusBreakendQCStatus.NO_ABNORMALITIES)
                .integrations(0)
                .virusDriverLikelihoodType(VirusLikelihoodType.LOW)
                .percentageCovered(0D);
    }
}
