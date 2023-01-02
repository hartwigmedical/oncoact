package com.hartwig.oncoact.copynumber;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.oncoact.genome.GenomeRegion;
import com.hartwig.oncoact.genome.HumanChromosome;
import com.hartwig.oncoact.genome.ImmutableGenomeRegion;
import com.hartwig.oncoact.genome.RefGenomeCoordinates;
import com.hartwig.oncoact.orange.purple.PurpleCopyNumber;

import org.jetbrains.annotations.NotNull;

public final class CnPerChromosomeFactory {

    private CnPerChromosomeFactory() {
    }

    @NotNull
    public static List<CnPerChromosomeArmData> extractCnPerChromosomeArm(@NotNull Iterable<PurpleCopyNumber> copyNumbers,
            @NotNull RefGenomeCoordinates refGenomeCoordinates) {
        List<CnPerChromosomeArmData> cnPerChromosomeArmData = Lists.newArrayList();
        for (HumanChromosome chromosome : refGenomeCoordinates.lengths().keySet()) {
            Map<ChromosomeArm, GenomeRegion> genomeRegion = determineArmRegions(chromosome, refGenomeCoordinates);

            for (Map.Entry<ChromosomeArm, GenomeRegion> entry : genomeRegion.entrySet()) {
                GenomeRegion arm = entry.getValue();

                double copyNumberArm = 0;
                for (PurpleCopyNumber purpleCopyNumber : copyNumbers) {
                    HumanChromosome copyNumberChromosome = HumanChromosome.fromString(purpleCopyNumber.chromosome());

                    if (copyNumberChromosome.equals(chromosome) && overlaps(arm, purpleCopyNumber)) {
                        double copyNumber = purpleCopyNumber.averageTumorCopyNumber();
                        int totalLengthSegment = bases(purpleCopyNumber);
                        copyNumberArm += (copyNumber * totalLengthSegment) / bases(arm);
                    }
                }

                if (copyNumberArm > 0) {
                    cnPerChromosomeArmData.add(ImmutableCnPerChromosomeArmData.builder()
                            .chromosome(chromosome)
                            .chromosomeArm(entry.getKey())
                            .copyNumber(copyNumberArm)
                            .build());
                }
            }
        }

        return cnPerChromosomeArmData;
    }

    @NotNull
    private static Map<ChromosomeArm, GenomeRegion> determineArmRegions(@NotNull HumanChromosome chromosome,
            @NotNull RefGenomeCoordinates refGenomeCoordinates) {
        int centromerePos = refGenomeCoordinates.centromeres().get(chromosome);
        int chrLength = refGenomeCoordinates.lengths().get(chromosome);

        Map<ChromosomeArm, GenomeRegion> chromosomeArmGenomeRegionMap = Maps.newHashMap();

        GenomeRegion partBeforeCentromere = createGenomeRegion(chromosome.toString(), 1, centromerePos);
        GenomeRegion partAfterCentromere = createGenomeRegion(chromosome.toString(), centromerePos + 1, chrLength);

        if (bases(partBeforeCentromere) < bases(partAfterCentromere)) {
            chromosomeArmGenomeRegionMap.put(ChromosomeArm.P_ARM, partBeforeCentromere);
            chromosomeArmGenomeRegionMap.put(ChromosomeArm.Q_ARM, partAfterCentromere);
        } else {
            chromosomeArmGenomeRegionMap.put(ChromosomeArm.P_ARM, partAfterCentromere);
            chromosomeArmGenomeRegionMap.put(ChromosomeArm.Q_ARM, partBeforeCentromere);
        }
        return chromosomeArmGenomeRegionMap;
    }

    @NotNull
    private static GenomeRegion createGenomeRegion(@NotNull String chromosome, int start, int end) {
        return ImmutableGenomeRegion.builder().chromosome(chromosome).start(start).end(end).build();
    }

    private static boolean overlaps(@NotNull GenomeRegion region, @NotNull PurpleCopyNumber purpleCopyNumber) {
        return purpleCopyNumber.chromosome().equals(region.chromosome()) && purpleCopyNumber.end() > region.start()
                && purpleCopyNumber.start() < region.end();
    }

    private static int bases(@NotNull GenomeRegion region) {
        return bases(region.start(), region.end());
    }

    private static int bases(@NotNull PurpleCopyNumber purpleCopyNumber) {
        return bases(purpleCopyNumber.start(), purpleCopyNumber.end());
    }

    private static int bases(int start, int end) {
        return 1 + end - start;
    }
}