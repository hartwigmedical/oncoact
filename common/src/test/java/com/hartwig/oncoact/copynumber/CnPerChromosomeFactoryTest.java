package com.hartwig.oncoact.copynumber;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.hartwig.oncoact.genome.HumanChromosome;
import com.hartwig.oncoact.genome.RefGenomeCoordinates;
import com.hartwig.oncoact.orange.purple.PurpleCopyNumber;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CnPerChromosomeFactoryTest {

    private static final double EPSILON = 1.0E-3;

    @Test
    public void canDetermineCnPerChromosomeArm() {
        List<PurpleCopyNumber> copyNumbers = Lists.newArrayList();
        // Chromosome 1: 1-123035434-249250621
        copyNumbers.add(TestPurpleFactory.copyNumberBuilder().chromosome("1").start(1).end(123035434).averageTumorCopyNumber(2D).build());
        copyNumbers.add(TestPurpleFactory.copyNumberBuilder()
                .chromosome("1")
                .start(123035435)
                .end(124035434)
                .averageTumorCopyNumber(300D)
                .build());
        copyNumbers.add(TestPurpleFactory.copyNumberBuilder()
                .chromosome("1")
                .start(124035435)
                .end(249250621)
                .averageTumorCopyNumber(3D)
                .build());

        List<CnPerChromosomeArmData> cnPerChromosomeArm =
                CnPerChromosomeFactory.extractCnPerChromosomeArm(copyNumbers, RefGenomeCoordinates.COORDS_37);

        assertEquals(2, cnPerChromosomeArm.size());
        CnPerChromosomeArmData cnPerChromosomeArmData1 =
                findByChromosomeAndArm(cnPerChromosomeArm, HumanChromosome._1, ChromosomeArm.P_ARM);
        assertEquals(2D, cnPerChromosomeArmData1.copyNumber(), EPSILON);

        CnPerChromosomeArmData cnPerChromosomeArmData2 =
                findByChromosomeAndArm(cnPerChromosomeArm, HumanChromosome._1, ChromosomeArm.Q_ARM);
        assertEquals(5.35312, cnPerChromosomeArmData2.copyNumber(), EPSILON);
    }

    @NotNull
    private static CnPerChromosomeArmData findByChromosomeAndArm(@NotNull List<CnPerChromosomeArmData> dataList,
            @NotNull HumanChromosome chromosome, @NotNull ChromosomeArm arm) {
        for (CnPerChromosomeArmData data : dataList) {
            if (data.chromosome() == chromosome && data.chromosomeArm() == arm) {
                return data;
            }
        }

        throw new IllegalStateException("Could not find data with chromosome " + chromosome + " and arm " + arm);
    }
}