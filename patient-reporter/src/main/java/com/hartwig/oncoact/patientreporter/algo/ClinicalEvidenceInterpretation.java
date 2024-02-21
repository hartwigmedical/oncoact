package com.hartwig.oncoact.patientreporter.algo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.oncoact.protect.ProtectEvidence;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ClinicalEvidenceInterpretation {

    @NotNull
    public static Map<String, List<ProtectEvidence>> buildTreatmentMap(@NotNull List<ProtectEvidence> evidences, boolean reportGermline,
                                                                       Boolean requireOnLabel, @NotNull String name) {
        Map<String, List<ProtectEvidence>> evidencePerTreatmentMap = Maps.newHashMap();

        for (ProtectEvidence evidence : evidences) {
            if ((reportGermline || !evidence.germline()) && (requireOnLabel == null || evidence.onLabel() == requireOnLabel)) {
                String treatment = Strings.EMPTY;
                List<ProtectEvidence> treatmentEvidences = Lists.newArrayList();
                Set<String> treatmentApproaches = evidence.treatment().sourceRelevantTreatmentApproaches();
                String treatmentJoin = String.join(",", treatmentApproaches);
                if (name.equals("treatmentApproach")) {
                    if (!treatmentJoin.isEmpty()) {
                        List<String> treatentSort = Lists.newArrayList(evidence.treatment().sourceRelevantTreatmentApproaches());
                        Collections.sort(treatentSort);
                        treatment = String.join(",", treatentSort);
                        treatmentEvidences = evidencePerTreatmentMap.getOrDefault(treatment, new ArrayList<>());
                        if (!hasHigherOrEqualEvidenceForEventAndTreatmentApproach(treatmentEvidences, evidence)
                                && !treatment.equals(Strings.EMPTY)) {
                            treatmentEvidences.add(evidence);
                            evidencePerTreatmentMap.put(treatment, treatmentEvidences);
                        }
                    }
                } else {
                    if (treatmentJoin.isEmpty()) {
                        treatment = evidence.treatment().name();
                        treatmentEvidences = evidencePerTreatmentMap.getOrDefault(treatment, new ArrayList<>());
                        if (!hasHigherOrEqualEvidenceForEventAndTreatment(treatmentEvidences, evidence)
                                && !treatment.equals(Strings.EMPTY)) {
                            treatmentEvidences.add(evidence);
                            evidencePerTreatmentMap.put(treatment, treatmentEvidences);
                        }

                    }
                }

            }
        }
        return evidencePerTreatmentMap;
    }

    private static boolean hasHigherOrEqualEvidenceForEventAndTreatment(@NotNull List<ProtectEvidence> evidences,
                                                                        @NotNull ProtectEvidence evidenceToCheck) {
        for (ProtectEvidence evidence : evidences) {
            if (evidence.treatment().name().equals(evidenceToCheck.treatment().name()) && StringUtils.equals(evidence.gene(),
                    evidenceToCheck.gene()) && evidence.event().equals(evidenceToCheck.event())) {
                if (!evidenceToCheck.level().isHigher(evidence.level())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasHigherOrEqualEvidenceForEventAndTreatmentApproach(@NotNull List<ProtectEvidence> evidences,
                                                                                @NotNull ProtectEvidence evidenceToCheck) {
        for (ProtectEvidence evidence : evidences) {
            if (evidence.treatment().relevantTreatmentApproaches().equals(evidenceToCheck.treatment().relevantTreatmentApproaches())
                    && StringUtils.equals(evidence.gene(), evidenceToCheck.gene()) && evidence.event().equals(evidenceToCheck.event())) {
                if (!evidenceToCheck.level().isHigher(evidence.level())) {
                    return true;
                }
            }
        }
        return false;
    }
}