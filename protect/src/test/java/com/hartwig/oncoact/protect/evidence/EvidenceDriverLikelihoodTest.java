package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.hartwig.oncoact.common.linx.FusionLikelihoodType;
import com.hartwig.oncoact.common.variant.DriverInterpretation;

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
        assertTrue(EvidenceDriverLikelihood.interpretFusion(FusionLikelihoodType.HIGH));
        assertFalse(EvidenceDriverLikelihood.interpretFusion(FusionLikelihoodType.LOW));
        assertFalse(EvidenceDriverLikelihood.interpretFusion(FusionLikelihoodType.NA));
    }

    @Test
    public void canInterpretCNV() {
        assertTrue(EvidenceDriverLikelihood.interpretCopyNumber());
    }

    @Test
    public void canInterpretVirus() {
        assertTrue(EvidenceDriverLikelihood.interpretVirus());
    }
}