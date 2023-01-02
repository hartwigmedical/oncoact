package com.hartwig.oncoact.copynumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ChromosomeTest {

    @Test
    public void canResolveChromosomeFromString() {
        assertEquals(Chromosome._1, Chromosome.fromString("1"));
        assertEquals(Chromosome._1, Chromosome.fromString("chr1"));
        assertEquals(Chromosome._1, Chromosome.fromString("CHR1"));
        assertEquals(Chromosome._1, Chromosome.fromString("ChR1"));

        assertEquals(Chromosome._2, Chromosome.fromString("2"));
        assertEquals(Chromosome._2, Chromosome.fromString("chr2"));

        assertEquals(Chromosome._22, Chromosome.fromString("22"));
        assertEquals(Chromosome._22, Chromosome.fromString("chr22"));

        assertEquals(Chromosome._X, Chromosome.fromString("X"));
        assertEquals(Chromosome._X, Chromosome.fromString("chrX"));

        assertEquals(Chromosome._Y, Chromosome.fromString("Y"));
        assertEquals(Chromosome._Y, Chromosome.fromString("chrY"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void crashOnInvalidChromosome() {
        Chromosome.fromString("HLA-DRB1*14:54:01");
    }

    @Test
    public void canDetermineIfChromosomeIsContained() {
        assertTrue(Chromosome.contains("1"));
        assertTrue(Chromosome.contains("chr1"));
        assertFalse(Chromosome.contains("HLA-DRB1*14:54:01"));
    }
}
