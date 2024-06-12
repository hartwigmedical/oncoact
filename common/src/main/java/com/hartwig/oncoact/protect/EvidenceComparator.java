package com.hartwig.oncoact.protect;

import java.util.Comparator;
import java.util.Objects;

import com.hartwig.oncoact.util.ActionabilityIntervation;
import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.Treatment;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvidenceComparator implements Comparator<ProtectEvidence> {

    @Override
    public int compare(@NotNull ProtectEvidence evidence1, @NotNull ProtectEvidence evidence2) {
        int reportedCompare = -Boolean.compare(evidence1.reported(), evidence2.reported());
        if (reportedCompare != 0) {
            return reportedCompare;
        }

        int geneCompare = StringUtils.compare(evidence1.gene(), evidence2.gene());
        if (geneCompare != 0) {
            return geneCompare;
        }

        int transcriptCompare = StringUtils.compare(evidence1.transcript(), evidence2.transcript());
        if (transcriptCompare != 0) {
            return transcriptCompare;
        }

        int isCanonicalCompare = compareBoolean(evidence1.isCanonical(), evidence2.isCanonical());
        if (isCanonicalCompare != 0) {
            return isCanonicalCompare;
        }

        int eventCompare = evidence1.event().compareTo(evidence2.event());
        if (eventCompare != 0) {
            return eventCompare;
        }

        int levelCompare = evidence1.level().compareTo(evidence2.level());
        if (levelCompare != 0) {
            return levelCompare;
        }

        int onLabelCompare = -Boolean.compare(evidence1.onLabel(), evidence2.onLabel());
        if (onLabelCompare != 0) {
            return onLabelCompare;
        }

        ClinicalTrial clinicalTrial1 = evidence1.clinicalTrial();
        ClinicalTrial clinicalTrial2 = evidence2.clinicalTrial();
        Treatment treatment1 = evidence1.treatment();
        Treatment treatment2 = evidence2.treatment();

        String therapy1 = ActionabilityIntervation.therapyName(clinicalTrial1, treatment1);
        String therapy2 = ActionabilityIntervation.therapyName(clinicalTrial2, treatment2);

        int clinicalTrialCompare =
                clinicalTrial1 != null && clinicalTrial2 != null ? clinicalTrial1.studyNctId().compareTo(clinicalTrial2.studyNctId()) : 0;
        if (clinicalTrialCompare != 0) {
            return clinicalTrialCompare;
        }

        int treatmentCompare = therapy1.compareTo(therapy2);
        if (treatmentCompare != 0) {
            return treatmentCompare;
        }

        int treatmentApproachesDrugClass = treatment1 != null && treatment2 != null ? treatment1.treatmentApproachesDrugClass()
                .toString()
                .compareTo(treatment2.treatmentApproachesDrugClass().toString()) : 0;
        if (treatmentApproachesDrugClass != 0) {
            return treatmentApproachesDrugClass;
        }

        int treatmentApproachesTherapy = treatment1 != null && treatment2 != null ? treatment1.treatmentApproachesTherapy()
                .toString()
                .compareTo(treatment2.treatmentApproachesTherapy().toString()) : 0;
        if (treatmentApproachesTherapy != 0) {
            return treatmentApproachesTherapy;
        }

        return evidence1.direction().compareTo(evidence2.direction());
    }

    private static int compareBoolean(@Nullable Boolean boolean1, @Nullable Boolean boolean2) {
        if (Objects.equals(boolean1, boolean2)) {
            return 0;
        } else if (boolean1 == null) {
            return -1;
        } else if (boolean2 == null) {
            return 1;
        } else {
            return boolean1.compareTo(boolean2);
        }
    }
}
