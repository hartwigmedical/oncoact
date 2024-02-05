package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.purple.CopyNumberInterpretation;
import com.hartwig.oncoact.copynumber.Chromosome;
import com.hartwig.oncoact.copynumber.ChromosomeArm;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.copynumber.PurpleGainLossData;
import com.hartwig.oncoact.patientreporter.algo.CurationFunctions;

import org.jetbrains.annotations.NotNull;

public final class GainsAndLosses {

    private GainsAndLosses() {
    }

    @NotNull
    public static List<PurpleGainLossData> sort(@NotNull List<PurpleGainLossData> gainsAndLosses) {
        return gainsAndLosses.stream().sorted((gainLoss1, gainLoss2) -> {
            String location1 = GeneUtil.zeroPrefixed(gainLoss1.chromosome() + gainLoss1.chromosomeBand());
            String location2 = GeneUtil.zeroPrefixed(gainLoss2.chromosome() + gainLoss2.chromosomeBand());

            if (location1.equals(location2)) {
                return gainLoss1.gene().compareTo(gainLoss2.gene());
            } else {
                return location1.compareTo(location2);
            }
        }).collect(Collectors.toList());
    }

    @NotNull
    public static Set<String> amplifiedGenes(@NotNull Iterable<PurpleGainLossData> reportableGainLosses) {
        Set<String> genes = Sets.newTreeSet();
        for (PurpleGainLossData gainLoss : reportableGainLosses) {
            if (gainLoss.interpretation() == CopyNumberInterpretation.FULL_GAIN
                    || gainLoss.interpretation() == CopyNumberInterpretation.PARTIAL_GAIN) {
                genes.add(CurationFunctions.curateGeneNamePdf(gainLoss.gene()));
            }
        }
        return genes;
    }

    @NotNull
    public static Set<String> lostGenes(@NotNull Iterable<PurpleGainLossData> reportableGainLosses) {
        Set<String> genes = Sets.newTreeSet();
        for (PurpleGainLossData gainLoss : reportableGainLosses) {
            if (gainLoss.interpretation() == CopyNumberInterpretation.FULL_LOSS
                    || gainLoss.interpretation() == CopyNumberInterpretation.PARTIAL_LOSS) {
                genes.add(CurationFunctions.curateGeneNamePdf(gainLoss.gene()));
            }
        }
        return genes;
    }

    @NotNull
    public static String chromosomeArmCopyNumber(@NotNull List<CnPerChromosomeArmData> cnPerChromosomeData,
            @NotNull PurpleGainLossData gainLoss) {
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

    @NotNull
    public static String interpretation(@NotNull PurpleGainLossData gainLoss) {
        return gainLoss.interpretation().toString().toLowerCase().replaceAll("_", " ");
    }
}
