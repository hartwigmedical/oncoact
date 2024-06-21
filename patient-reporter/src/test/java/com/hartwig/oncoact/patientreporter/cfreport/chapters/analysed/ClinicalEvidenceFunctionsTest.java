package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.ImmutableProtectEvidence;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestProtectFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableClinicalTrial;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ClinicalEvidenceFunctionsTest {

    @Test
    public void canDetermineHigherEvidence() {
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatmentApproach(createProtectEvidenceList(EvidenceLevel.B,
                EvidenceDirection.RESPONSIVE,
                true), createProtectEvidence(EvidenceLevel.B, EvidenceDirection.RESISTANT, true)));
        assertFalse(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatmentApproach(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESISTANT,
                true), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.RESPONSIVE, true)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatmentApproach(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESISTANT,
                true), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.RESISTANT, true)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatmentApproach(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESPONSIVE,
                true), createProtectEvidence(EvidenceLevel.B, EvidenceDirection.RESPONSIVE, true)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatmentApproach(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.PREDICTED_RESPONSIVE,
                true), createProtectEvidence(EvidenceLevel.C, EvidenceDirection.RESPONSIVE, true)));
        assertFalse(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatmentApproach(createProtectEvidenceList(EvidenceLevel.C,
                EvidenceDirection.RESISTANT,
                true), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.RESPONSIVE, true)));

        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatment(createProtectEvidenceList(EvidenceLevel.B,
                EvidenceDirection.RESPONSIVE,
                true), createProtectEvidence(EvidenceLevel.B, EvidenceDirection.RESISTANT, true)));
        assertFalse(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatment(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESISTANT,
                true), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.RESPONSIVE, true)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatment(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESISTANT,
                true), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.RESISTANT, true)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatment(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESPONSIVE,
                true), createProtectEvidence(EvidenceLevel.B, EvidenceDirection.RESPONSIVE, true)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatment(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.PREDICTED_RESPONSIVE,
                true), createProtectEvidence(EvidenceLevel.C, EvidenceDirection.RESPONSIVE, true)));
        assertFalse(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatment(createProtectEvidenceList(EvidenceLevel.C,
                EvidenceDirection.RESISTANT,
                true), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.RESPONSIVE, true)));

        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTrial(createProtectEvidenceList(EvidenceLevel.B,
                EvidenceDirection.RESPONSIVE,
                true), createProtectEvidence(EvidenceLevel.B, EvidenceDirection.RESISTANT, true)));
        assertFalse(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTrial(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESISTANT,
                true), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.RESPONSIVE, true)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTrial(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESISTANT,
                true), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.RESISTANT, true)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTrial(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESPONSIVE,
                true), createProtectEvidence(EvidenceLevel.B, EvidenceDirection.RESPONSIVE, true)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTrial(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.PREDICTED_RESPONSIVE,
                true), createProtectEvidence(EvidenceLevel.C, EvidenceDirection.RESPONSIVE, true)));
        assertFalse(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTrial(createProtectEvidenceList(EvidenceLevel.C,
                EvidenceDirection.RESISTANT,
                true), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.RESPONSIVE, true)));

    }

    @NotNull
    private List<ProtectEvidence> createProtectEvidenceList(@NotNull EvidenceLevel level, @NotNull EvidenceDirection direction,
            boolean onLabel) {
        return Collections.singletonList(createProtectEvidence(level, direction, onLabel));
    }

    @NotNull
    public ProtectEvidence createProtectEvidence(@NotNull EvidenceLevel level, @NotNull EvidenceDirection direction, boolean onLabel) {
        return ImmutableProtectEvidence.builder()
                .event(Strings.EMPTY)
                .gene("KRAS")
                .event("amp")
                .germline(false)
                .reported(true)
                .clinicalTrial(ImmutableClinicalTrial.builder()
                        .studyNctId("NCT01")
                        .studyTitle("This is the study title")
                        .studyAcronym("This is the study acronym")
                        .gender("both")
                        .countriesOfStudy(Sets.newHashSet("Netherlands"))
                        .therapyNames(Sets.newHashSet("drug 1"))
                        .build())
                .treatment(ImmutableTreatment.builder()
                        .name("drug1")
                        .treatmentApproachesTherapy(Sets.newHashSet("therapy treatment approach"))
                        .treatmentApproachesDrugClass(Sets.newHashSet("drug treatment approach"))
                        .build())
                .matchGender(true)
                .treatment(ImmutableTreatment.builder().name(Strings.EMPTY).build())
                .onLabel(onLabel)
                .level(level)
                .direction(direction)
                .sources(Sets.newHashSet(TestProtectFactory.createSource(Knowledgebase.CKB_EVIDENCE)))
                .build();
    }

}