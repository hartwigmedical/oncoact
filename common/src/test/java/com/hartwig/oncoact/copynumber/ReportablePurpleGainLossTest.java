package com.hartwig.oncoact.copynumber;

import static org.junit.Assert.assertEquals;

import com.hartwig.hmftools.datamodel.purple.CopyNumberInterpretation;
import com.hartwig.hmftools.datamodel.purple.GeneProportion;

import org.junit.Test;

public class ReportablePurpleGainLossTest {

    @Test
    public void canInterpretationGene() {
        assertEquals(CopyNumberInterpretation.FULL_LOSS, ReportablePurpleGainLoss.toInterpretationGene(GeneProportion.FULL_GENE.name()));
        assertEquals(CopyNumberInterpretation.PARTIAL_LOSS,
                ReportablePurpleGainLoss.toInterpretationGene(GeneProportion.PARTIAL_GENE.name()));
    }

}