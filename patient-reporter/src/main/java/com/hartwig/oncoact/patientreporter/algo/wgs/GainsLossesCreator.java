package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.hartwig.hmftools.datamodel.purple.CopyNumberInterpretation;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.oncoact.copynumber.Chromosome;
import com.hartwig.oncoact.copynumber.ChromosomeArm;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneUtil;
import com.hartwig.oncoact.patientreporter.model.ObservedGainsLosses;
import com.hartwig.oncoact.patientreporter.model.ObservedGainsLossesType;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class GainsLossesCreator {

    static List<ObservedGainsLosses> createGainsLosses(
            @NotNull List<PurpleGainLoss> gainsAndLosses,
            @NotNull List<CnPerChromosomeArmData> cnPerChromosome,
            boolean hasReliablePurity
    ) {
        List<ObservedGainsLosses> observedGainsLosses = Lists.newArrayList();

        for (PurpleGainLoss purpleGainLoss : gainsAndLosses) {
            observedGainsLosses.add(ObservedGainsLosses.builder()
                    .chromosome(purpleGainLoss.chromosome())
                    .region(purpleGainLoss.chromosomeBand())
                    .gene(purpleGainLoss.gene())
                    .type(interpretation(purpleGainLoss.interpretation()))
                    .minCopies(roundCopyNumber(purpleGainLoss.minCopies(), hasReliablePurity))
                    .maxCopies(roundCopyNumber(purpleGainLoss.maxCopies(), hasReliablePurity))
                    .chromosomeArmCopies(chromosomeArmCopyNumber(cnPerChromosome, purpleGainLoss))
                    .build());
        }
        return observedGainsLosses;
    }

    @NotNull
    public static ObservedGainsLossesType interpretation(@NotNull CopyNumberInterpretation interpretation) {
        switch (interpretation) {
            case FULL_GAIN:
                return ObservedGainsLossesType.FULL_GAIN;
            case FULL_LOSS:
                return ObservedGainsLossesType.FULL_LOSS;
            case PARTIAL_GAIN:
                return ObservedGainsLossesType.PARTIAL_GAIN;
            case PARTIAL_LOSS:
                return ObservedGainsLossesType.PARTIAL_LOSS;
            default:
                return ObservedGainsLossesType.UNKNOWN;
        }
    }

    @NotNull
    public static String roundCopyNumber(Double copyNumber, boolean hasReliablePurity) {
        return hasReliablePurity && !copyNumber.isNaN() ? String.valueOf(Math.round(copyNumber)) : Formats.NA_STRING;
    }

    @NotNull
    public static String chromosomeArmCopyNumber(@NotNull List<CnPerChromosomeArmData> cnPerChromosomeData,
                                                 @NotNull PurpleGainLoss gainLoss) {
        ChromosomeArm chromosomeArm;
        if (gainLoss.chromosomeBand().startsWith("p")) {
            chromosomeArm = ChromosomeArm.P_ARM;
        } else if (gainLoss.chromosomeBand().startsWith("q")) {
            chromosomeArm = ChromosomeArm.Q_ARM;
        } else {
            throw new IllegalArgumentException("Chromosome arm could not be resolved from band: " + gainLoss.chromosomeBand() + "!");
        }

        Double copyNumber = null;
        for (CnPerChromosomeArmData cnPerChromosome : cnPerChromosomeData) {
            if (Chromosome.fromString(gainLoss.chromosome()) == cnPerChromosome.chromosome()
                    && chromosomeArm == cnPerChromosome.chromosomeArm()) {
                copyNumber = cnPerChromosome.copyNumber();
            }
        }

        return GeneUtil.roundCopyNumber(copyNumber);
    }
}