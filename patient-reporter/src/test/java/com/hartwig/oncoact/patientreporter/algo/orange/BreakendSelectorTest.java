package com.hartwig.oncoact.patientreporter.algo.orange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.knownfusion.KnownFusionCache;
import com.hartwig.oncoact.knownfusion.KnownFusionData;
import com.hartwig.oncoact.knownfusion.KnownFusionType;
import com.hartwig.oncoact.knownfusion.TestKnownFusionFactory;
import com.hartwig.oncoact.orange.linx.LinxBreakend;
import com.hartwig.oncoact.orange.linx.LinxFusion;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class BreakendSelectorTest {

    @Test
    public void canSelectInterestingUnreportedBreakends() {
        LinxBreakend fiveGeneExon10 = createFive("five", "canonical", 10);
        LinxBreakend fiveGeneExon11 = createFive("five", "canonical", 11);
        LinxBreakend fiveGeneExon10NonCanonical = createFive("five", "non-canonical", 10);
        LinxBreakend fiveGeneExon20 = createFive("five", "canonical", 20);
        LinxBreakend threeGeneExon10 = createThree("three", "canonical", 10);
        LinxBreakend threeGeneExon20 = createThree("three", "canonical", 20);
        LinxBreakend threeGeneExon21 = createThree("three", "canonical", 21);
        LinxBreakend otherGene = createThree("other", "canonical", 10);
        List<LinxBreakend> allBreakends = Lists.newArrayList(fiveGeneExon10,
                fiveGeneExon11,
                fiveGeneExon10NonCanonical,
                fiveGeneExon20,
                threeGeneExon10,
                threeGeneExon20,
                threeGeneExon21,
                otherGene);

        int[] fiveGeneExonRange = { 8, 12 };
        KnownFusionData knownFive = createKnownFiveFusion("five", "canonical", fiveGeneExonRange);

        int[] threeGeneExonRange = { 20, 21 };
        KnownFusionData knownThree = createKnownThreeFusion("three", "canonical", threeGeneExonRange);

        KnownFusionCache knownFusionCache = TestKnownFusionFactory.createCache(Lists.newArrayList(knownFive, knownThree));

        LinxFusion fiveFusion = TestLinxFactory.fusionBuilder().geneStart("five").fusedExonUp(11).build();
        LinxFusion threeFusion = TestLinxFactory.fusionBuilder().geneEnd("three").fusedExonDown(21).build();
        List<LinxBreakend> potentiallyInteresting = BreakendSelector.selectInterestingUnreportedBreakends(allBreakends,
                Lists.newArrayList(fiveFusion, threeFusion),
                knownFusionCache);

        assertEquals(2, potentiallyInteresting.size());
        assertTrue(potentiallyInteresting.contains(fiveGeneExon10));
        assertTrue(potentiallyInteresting.contains(threeGeneExon20));
    }

    @NotNull
    private static LinxBreakend createFive(@NotNull String gene, @NotNull String transcript, int exon) {
        return create(gene, transcript, BreakendSelector.UPSTREAM_ORIENTATION, exon);
    }

    @NotNull
    private static LinxBreakend createThree(@NotNull String gene, @NotNull String transcript, int exon) {
        return create(gene, transcript, BreakendSelector.DOWNSTREAM_ORIENTATION, exon);
    }

    @NotNull
    private static LinxBreakend create(@NotNull String gene, @NotNull String transcript, @NotNull String geneOrientation, int exon) {
        return TestLinxFactory.breakendBuilder()
                .reported(false)
                .gene(gene)
                .transcriptId(transcript)
                .geneOrientation(geneOrientation)
                .nextSpliceExonRank(exon)
                .disruptive(true)
                .build();
    }

    @NotNull
    private static KnownFusionData createKnownFiveFusion(@NotNull String gene, @NotNull String specificExonsTransName,
            @NotNull int[] exonRange) {
        return createKnownFusion(KnownFusionType.PROMISCUOUS_5, gene, Strings.EMPTY, specificExonsTransName, exonRange, new int[] {});
    }

    @NotNull
    private static KnownFusionData createKnownThreeFusion(@NotNull String gene, @NotNull String specificExonsTransName,
            @NotNull int[] exonRange) {
        return createKnownFusion(KnownFusionType.PROMISCUOUS_3, Strings.EMPTY, gene, specificExonsTransName, new int[] {}, exonRange);
    }

    @NotNull
    private static KnownFusionData createKnownFusion(@NotNull KnownFusionType type, @NotNull String fiveGene, @NotNull String threeGene,
            @NotNull String specificExonsTransName, @NotNull int[] fiveGeneExonRange, @NotNull int[] threeGeneExonRange) {
        return TestKnownFusionFactory.builder()
                .type(type)
                .fiveGene(fiveGene)
                .threeGene(threeGene)
                .specificExonsTransName(specificExonsTransName)
                .fiveGeneExonRange(fiveGeneExonRange)
                .threeGeneExonRange(threeGeneExonRange)
                .build();
    }
}