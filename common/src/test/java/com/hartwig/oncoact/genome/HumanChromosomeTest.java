package com.hartwig.oncoact.genome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HumanChromosomeTest {

    @Test
    public void testFromString() {
        assertEquals(HumanChromosome._1, HumanChromosome.fromString("1"));
        assertEquals(HumanChromosome._1, HumanChromosome.fromString("chr1"));
        assertEquals(HumanChromosome._1, HumanChromosome.fromString("CHR1"));
        assertEquals(HumanChromosome._1, HumanChromosome.fromString("ChR1"));

        assertEquals(HumanChromosome._2, HumanChromosome.fromString("2"));
        assertEquals(HumanChromosome._2, HumanChromosome.fromString("chr2"));

        assertEquals(HumanChromosome._22, HumanChromosome.fromString("22"));
        assertEquals(HumanChromosome._22, HumanChromosome.fromString("chr22"));

        assertEquals(HumanChromosome._X, HumanChromosome.fromString("X"));
        assertEquals(HumanChromosome._X, HumanChromosome.fromString("chrX"));

        assertEquals(HumanChromosome._Y, HumanChromosome.fromString("Y"));
        assertEquals(HumanChromosome._Y, HumanChromosome.fromString("chrY"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownChromosome() {
        HumanChromosome.fromString("HLA-DRB1*14:54:01");
    }

    @Test
    public void testContained() {
        assertTrue(HumanChromosome.contains("1"));
        assertTrue(HumanChromosome.contains("chr1"));
        assertFalse(HumanChromosome.contains("HLA-DRB1*14:54:01"));
    }

    @Test
    public void testSexChromosomes() {
        assertTrue(HumanChromosome._X.isAllosome());
        assertFalse(HumanChromosome._X.isAutosome());

        assertTrue(HumanChromosome._Y.isAllosome());
        assertFalse(HumanChromosome._Y.isAutosome());
    }
}
