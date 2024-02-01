package com.hartwig.oncoact.copynumber;

import java.util.List;

import com.google.api.client.util.Lists;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleLossOfHeterozygosity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReportablePurpleGainLoss {

    private ReportablePurpleGainLoss() {
    }

    @NotNull
    public static List<PurpleGainLossData> toReportableGainLossLOH(
            @Nullable List<PurpleLossOfHeterozygosity> germlineLossOfHeterozygosity) {
        List<PurpleGainLossData> reportablePurpleGainLoss = Lists.newArrayList();

        if (germlineLossOfHeterozygosity != null) {
            for (PurpleLossOfHeterozygosity loh : germlineLossOfHeterozygosity) {
                reportablePurpleGainLoss.add(ImmutablePurpleGainLossData.builder()
                        .interpretation(toInterpretation(loh.geneProportion().name()))
                        .chromosome(loh.chromosome())
                        .chromosomeBand(loh.chromosomeBand())
                        .gene(loh.gene())
                        .transcript(loh.transcript())
                        .isCanonical(loh.isCanonical())
                        .minCopies(loh.minCopies())
                        .maxCopies(loh.maxCopies())
                        .build());
            }
        }

        return reportablePurpleGainLoss;
    }

    @NotNull
    public static List<PurpleGainLossData> toReportableGainLoss(@Nullable List<PurpleGainLoss> purpleGainLosses) {
        List<PurpleGainLossData> reportablePurpleGainLoss = Lists.newArrayList();

        if (purpleGainLosses != null) {
            for (PurpleGainLoss purpleGainLoss : purpleGainLosses) {
                reportablePurpleGainLoss.add(ImmutablePurpleGainLossData.builder()
                        .interpretation(toInterpretation(purpleGainLoss.interpretation().name()))
                        .chromosome(purpleGainLoss.chromosome())
                        .chromosomeBand(purpleGainLoss.chromosomeBand())
                        .gene(purpleGainLoss.gene())
                        .transcript(purpleGainLoss.transcript())
                        .isCanonical(purpleGainLoss.isCanonical())
                        .minCopies(purpleGainLoss.minCopies())
                        .maxCopies(purpleGainLoss.maxCopies())
                        .build());
            }
        }

        return reportablePurpleGainLoss;
    }

    @NotNull
    public static com.hartwig.oncoact.copynumber.CopyNumberInterpretation toInterpretation(@NotNull String interpretationInput) {
        for (com.hartwig.oncoact.copynumber.CopyNumberInterpretation interpretation : com.hartwig.oncoact.copynumber.CopyNumberInterpretation.values()) {
            if (interpretationInput.equals(interpretation.toString())) {
                if (interpretation == CopyNumberInterpretation.FULL_GENE) {
                    return CopyNumberInterpretation.FULL_LOSS;
                } else if (interpretation == CopyNumberInterpretation.PARTIAL_GENE) {
                    return CopyNumberInterpretation.PARTIAL_LOSS;
                } else {
                    return interpretation;
                }
            }
        }

        throw new IllegalStateException("Cannot resolve interpretation: " + interpretationInput);
    }
}
