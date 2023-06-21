package com.hartwig.oncoact.copynumber;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.CopyNumberInterpretation;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReportableCNVFactoryTest {

    @Test
    public void canTestSomaticGainsLosses() {
        PurpleGainLoss somaticLoss = TestPurpleFactory.gainLossBuilder().interpretation(CopyNumberInterpretation.FULL_LOSS).build();
        PurpleGainLoss somaticGain = TestPurpleFactory.gainLossBuilder().interpretation(CopyNumberInterpretation.FULL_GAIN).build();
        List<PurpleGainLoss> gainLosses = Lists.newArrayList(somaticLoss, somaticGain);
        assertEquals(2, gainLosses.size());
    }

    @Test
    public void canTestSomaticGermlineGainsLosses() {
        PurpleGainLoss somaticLoss = TestPurpleFactory.gainLossBuilder().interpretation(CopyNumberInterpretation.FULL_LOSS).build();
        PurpleGainLoss somaticGain = TestPurpleFactory.gainLossBuilder().interpretation(CopyNumberInterpretation.FULL_GAIN).build();
        PurpleGainLoss germlineLoss = TestPurpleFactory.gainLossBuilder().interpretation(CopyNumberInterpretation.FULL_LOSS).build();
        List<PurpleGainLoss> gainLosses = Lists.newArrayList(somaticLoss, somaticGain, germlineLoss);
        assertEquals(3, gainLosses.size());
    }
}