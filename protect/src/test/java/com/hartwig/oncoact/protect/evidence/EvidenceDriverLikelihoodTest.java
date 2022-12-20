package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.hartwig.oncoact.orange.linx.LinxFusionDriverLikelihood;
import com.hartwig.oncoact.variant.DriverInterpretation;

import org.junit.Test;

public class EvidenceDriverLikelihoodTest {

    @Test
    public void canInterpretVariants() {
        assertTrue(EvidenceDriverLikelihood.interpretVariant(DriverInterpretation.HIGH));
        assertFalse(EvidenceDriverLikelihood.interpretVariant(DriverInterpretation.MEDIUM));
        assertFalse(EvidenceDriverLikelihood.interpretVariant(DriverInterpretation.LOW));
    }

    @Test
    public void canInterpretFusions() {
        assertTrue(EvidenceDriverLikelihood.interpretFusion(LinxFusionDriverLikelihood.HIGH));
        assertFalse(EvidenceDriverLikelihood.interpretFusion(LinxFusionDriverLikelihood.LOW));
        assertFalse(EvidenceDriverLikelihood.interpretFusion(LinxFusionDriverLikelihood.NA));
    }

    @Test
    public void canInterpretGainLoss() {
        assertTrue(EvidenceDriverLikelihood.interpretGainLoss());
    }

    @Test
    public void canInterpretVirus() {
        assertTrue(EvidenceDriverLikelihood.interpretVirus());
    }
}