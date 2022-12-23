package com.hartwig.oncoact.disruption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.orange.linx.ImmutableLinxBreakend;
import com.hartwig.oncoact.orange.linx.LinxBreakend;
import com.hartwig.oncoact.orange.linx.LinxBreakendType;
import com.hartwig.oncoact.orange.linx.LinxStructuralVariant;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;

import org.junit.Test;

public class GeneDisruptionFactoryTest {

    private static final double EPSILON = 1.0E-10;

    @Test
    public void canDetermineClusterId() {
        LinxBreakend breakend = TestLinxFactory.breakendBuilder().svId(1).build();

        LinxStructuralVariant match = TestLinxFactory.structuralVariantBuilder().svId(1).clusterId(2).build();
        assertEquals(2, (int) GeneDisruptionFactory.determineClusterId(Lists.newArrayList(match), breakend));

        LinxStructuralVariant noMatch = TestLinxFactory.structuralVariantBuilder().svId(2).clusterId(1).build();
        assertNull(GeneDisruptionFactory.determineClusterId(Lists.newArrayList(noMatch), breakend));
    }

    @Test
    public void canConvertPairedBreakend() {
        ImmutableLinxBreakend.Builder pairedBreakendBuilder = TestLinxFactory.breakendBuilder()
                .svId(1)
                .gene("ROPN1B")
                .transcriptId("ENST1")
                .chromosome("3")
                .chrBand("p12")
                .type(LinxBreakendType.INV)
                .junctionCopyNumber(1.12);
        List<LinxBreakend> pairedBreakends =
                Lists.newArrayList(pairedBreakendBuilder.exonUp(3).exonDown(4).undisruptedCopyNumber(4.3).build(),
                        pairedBreakendBuilder.exonUp(8).exonDown(9).undisruptedCopyNumber(2.1).build());

        List<GeneDisruption> geneDisruptions = GeneDisruptionFactory.convert(pairedBreakends, Lists.newArrayList());

        assertEquals(1, geneDisruptions.size());

        GeneDisruption disruption = geneDisruptions.get(0);
        assertEquals("INV", disruption.type());
        assertEquals("3p12", disruption.location());
        assertEquals("ROPN1B", disruption.gene());
        assertEquals("ENST1", disruption.transcriptId());
        assertEquals("Intron 3 -> Intron 8", disruption.range());
        assertEquals(3, disruption.firstAffectedExon());
        assertEquals(2.1, disruption.undisruptedCopyNumber(), EPSILON);

        Double copyNumber = disruption.junctionCopyNumber();
        assertNotNull(copyNumber);
        assertEquals(1.12, copyNumber, EPSILON);
    }

    @Test
    public void doesNotPairBreakendsOnDifferentTranscripts() {
        ImmutableLinxBreakend.Builder pairedBreakendBuilder = TestLinxFactory.breakendBuilder().svId(1);
        List<LinxBreakend> pairedDisruptions = Lists.newArrayList(pairedBreakendBuilder.transcriptId("ENST 1").svId(1).build(),
                pairedBreakendBuilder.transcriptId("ENST 2").svId(1).build(),
                pairedBreakendBuilder.transcriptId("ENST 2").svId(1).build());

        List<GeneDisruption> disruptions = GeneDisruptionFactory.convert(pairedDisruptions, Lists.newArrayList());

        assertEquals(2, disruptions.size());
    }
}