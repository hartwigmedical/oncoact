package com.hartwig.oncoact.patientreporter.cfreport.data;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.copynumber.ImmutableCnPerChromosomeArmData;
import com.hartwig.oncoact.genome.ChromosomeArm;
import com.hartwig.oncoact.genome.HumanChromosome;
import com.hartwig.oncoact.orange.purple.PurpleGainLoss;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.util.DataUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class GainsAndLossesTest {

    @Test
    public void canDetermineCopyNumberPArm() {
        List<CnPerChromosomeArmData> cnPerChromosome = Lists.newArrayList();
        String chromosome = "1";

        cnPerChromosome.add(ImmutableCnPerChromosomeArmData.builder()
                .chromosome(HumanChromosome.fromString(chromosome))
                .chromosomeArm(ChromosomeArm.P_ARM)
                .copyNumber(1.123)
                .build());

        assertEquals("1", GainsAndLosses.chromosomeArmCopyNumber(cnPerChromosome, createGainLoss(chromosome, "p.12")));
    }

    @Test
    public void canDetermineCopyNumberQArm() {
        List<CnPerChromosomeArmData> cnPerChromosome = Lists.newArrayList();
        String chromosome = "4";
        cnPerChromosome.add(ImmutableCnPerChromosomeArmData.builder()
                .chromosome(HumanChromosome.fromString(chromosome))
                .chromosomeArm(ChromosomeArm.Q_ARM)
                .copyNumber(4.51)
                .build());

        assertEquals("5", GainsAndLosses.chromosomeArmCopyNumber(cnPerChromosome, createGainLoss(chromosome, "q.12")));
    }

    @Test
    public void crashOnCopyNumberDifferentChromosomes() {
        List<CnPerChromosomeArmData> cnPerChromosome = Lists.newArrayList();
        cnPerChromosome.add(ImmutableCnPerChromosomeArmData.builder()
                .chromosome(HumanChromosome.fromString("1"))
                .chromosomeArm(ChromosomeArm.P_ARM)
                .copyNumber(1.123)
                .build());

        assertEquals(DataUtil.NA_STRING, GainsAndLosses.chromosomeArmCopyNumber(cnPerChromosome, createGainLoss("2", "p.12")));
    }

    @Test
    public void crashOnCopyNumberUnknownArms() {
        List<CnPerChromosomeArmData> cnPerChromosome = Lists.newArrayList();
        cnPerChromosome.add(ImmutableCnPerChromosomeArmData.builder()
                .chromosome(HumanChromosome.fromString("1"))
                .chromosomeArm(ChromosomeArm.UNKNOWN)
                .copyNumber(2.34)
                .build());
        assertEquals(DataUtil.NA_STRING, GainsAndLosses.chromosomeArmCopyNumber(cnPerChromosome, createGainLoss("1", "p.12")));
    }

    @Test
    public void crashOnCopyNumberDifferentArms() {
        List<CnPerChromosomeArmData> cnPerChromosome = Lists.newArrayList();
        cnPerChromosome.add(ImmutableCnPerChromosomeArmData.builder()
                .chromosome(HumanChromosome.fromString("1"))
                .chromosomeArm(ChromosomeArm.Q_ARM)
                .copyNumber(2.34)
                .build());
        assertEquals(DataUtil.NA_STRING, GainsAndLosses.chromosomeArmCopyNumber(cnPerChromosome, createGainLoss("1", "p.12")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void crashOnDetermineCopyNumberWeirdArm() {
        List<CnPerChromosomeArmData> cnPerChromosome = Lists.newArrayList();
        cnPerChromosome.add(ImmutableCnPerChromosomeArmData.builder()
                .chromosome(HumanChromosome.fromString("1"))
                .chromosomeArm(ChromosomeArm.Q_ARM)
                .copyNumber(1.123)
                .build());
        GainsAndLosses.chromosomeArmCopyNumber(cnPerChromosome, createGainLoss("1", ".12"));
    }

    @NotNull
    private static PurpleGainLoss createGainLoss(@NotNull String chromosome, @NotNull String chromosomeBand) {
        return TestPurpleFactory.gainLossBuilder().chromosome(chromosome).chromosomeBand(chromosomeBand).build();
    }
}