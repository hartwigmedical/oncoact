package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import com.google.common.collect.Sets;

import org.junit.Test;

public class SummaryChapterTest {

    @Test
    public void canSortSummaryOfGenesCorrectly() {
        Set<String> genesDisplay = Sets.newTreeSet();
        genesDisplay.add("A");
        genesDisplay.add("C");
        genesDisplay.add("B");
        Set<String> correctGenes = Sets.newHashSet("A", "B", "C");
        assertEquals(correctGenes, genesDisplay);
    }
}