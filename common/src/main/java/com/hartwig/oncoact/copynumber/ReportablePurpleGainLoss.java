package com.hartwig.oncoact.copynumber;

import java.util.List;

import com.google.api.client.util.Lists;
import com.hartwig.hmftools.datamodel.purple.CopyNumberInterpretation;
import com.hartwig.hmftools.datamodel.purple.GeneProportion;
import com.hartwig.hmftools.datamodel.purple.ImmutablePurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleLossOfHeterozygosity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReportablePurpleGainLoss {

    private ReportablePurpleGainLoss() {
    }

    @NotNull
    public static List<PurpleGainLoss> toReportableGainLossLOH(@Nullable List<PurpleLossOfHeterozygosity> germlineLossOfHeterozygosity) {
        List<PurpleGainLoss> reportablePurpleGainLoss = Lists.newArrayList();

        if (germlineLossOfHeterozygosity != null) {
            for (PurpleLossOfHeterozygosity loh : germlineLossOfHeterozygosity) {
                reportablePurpleGainLoss.add(ImmutablePurpleGainLoss.builder()
                        .interpretation(toInterpretationGene(loh.geneProportion().name()))
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
    public static CopyNumberInterpretation toInterpretationGene(@NotNull String interpretationInput) {
        if (interpretationInput.equals(GeneProportion.FULL_GENE.name())) {
            return CopyNumberInterpretation.FULL_LOSS;
        } else if (interpretationInput.equals(GeneProportion.PARTIAL_GENE.name())) {
            return CopyNumberInterpretation.PARTIAL_LOSS;
        }
        throw new IllegalStateException("Cannot resolve interpretation: " + interpretationInput);
    }
}