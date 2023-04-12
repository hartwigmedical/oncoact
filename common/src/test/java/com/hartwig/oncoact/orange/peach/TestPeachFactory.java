package com.hartwig.oncoact.orange.peach;

import com.hartwig.hmftools.datamodel.peach.ImmutablePeachGenotype;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestPeachFactory {

    private TestPeachFactory() {
    }

    @NotNull
    public static ImmutablePeachGenotype.Builder builder() {
        return ImmutablePeachGenotype.builder()
                .gene(Strings.EMPTY)
                .haplotype(Strings.EMPTY)
                .function(Strings.EMPTY)
                .linkedDrugs(Strings.EMPTY)
                .urlPrescriptionInfo(Strings.EMPTY)
                .panelVersion(Strings.EMPTY)
                .repoVersion(Strings.EMPTY);
    }
}
