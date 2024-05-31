package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.api.client.util.Lists;
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
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatment(createProtectEvidenceList(EvidenceLevel.B,
                EvidenceDirection.RESPONSIVE), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.RESPONSIVE)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatment(createProtectEvidenceList(EvidenceLevel.B,
                EvidenceDirection.RESISTANT), createProtectEvidence(EvidenceLevel.B, EvidenceDirection.RESPONSIVE)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatment(createProtectEvidenceList(EvidenceLevel.C,
                EvidenceDirection.RESPONSIVE), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.PREDICTED_RESPONSIVE)));
        assertFalse(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatment(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESPONSIVE), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.PREDICTED_RESPONSIVE)));
        assertFalse(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTreatment(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESPONSIVE), createProtectEvidence(EvidenceLevel.C, EvidenceDirection.RESISTANT)));

        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTrial(createProtectEvidenceList(EvidenceLevel.B,
                EvidenceDirection.RESPONSIVE), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.RESPONSIVE)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTrial(createProtectEvidenceList(EvidenceLevel.B,
                EvidenceDirection.RESISTANT), createProtectEvidence(EvidenceLevel.B, EvidenceDirection.RESPONSIVE)));
        assertTrue(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTrial(createProtectEvidenceList(EvidenceLevel.C,
                EvidenceDirection.RESPONSIVE), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.PREDICTED_RESPONSIVE)));
        assertFalse(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTrial(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESPONSIVE), createProtectEvidence(EvidenceLevel.A, EvidenceDirection.PREDICTED_RESPONSIVE)));
        assertFalse(ClinicalEvidenceFunctions.hasHigherOrEqualEvidenceForEventAndTrial(createProtectEvidenceList(EvidenceLevel.A,
                EvidenceDirection.RESPONSIVE), createProtectEvidence(EvidenceLevel.C, EvidenceDirection.RESISTANT)));

    }

    @NotNull
    public List<ProtectEvidence> createProtectEvidenceList(@NotNull EvidenceLevel level, @NotNull EvidenceDirection direction) {
        List<ProtectEvidence> evidences = Lists.newArrayList();
        evidences.add(createProtectEvidence(level, direction));
        return evidences;
    }

    @NotNull
    public ProtectEvidence createProtectEvidence(@NotNull EvidenceLevel level, @NotNull EvidenceDirection direction) {
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
                .onLabel(false)
                .level(level)
                .direction(direction)
                .sources(Sets.newHashSet(TestProtectFactory.createSource(Knowledgebase.CKB_EVIDENCE)))
                .build();
    }

}