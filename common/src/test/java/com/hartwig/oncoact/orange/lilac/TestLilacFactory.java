package com.hartwig.oncoact.orange.lilac;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestLilacFactory {

    private TestLilacFactory() {
    }

    @NotNull
    public static ImmutableLilacHlaAllele.Builder builder() {
        return ImmutableLilacHlaAllele.builder()
                .allele(Strings.EMPTY)
                .tumorCopyNumber(0D)
                .somaticMissense(0D)
                .somaticNonsenseOrFrameshift(0D)
                .somaticSplice(0D)
                .somaticSynonymous(0D)
                .somaticInframeIndel(0D);
    }
}
