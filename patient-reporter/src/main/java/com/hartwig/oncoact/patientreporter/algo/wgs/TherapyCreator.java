package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.lama.client.model.ReportSettings;
import com.hartwig.oncoact.patientreporter.actionability.ClinicalTrialFactory;
import com.hartwig.oncoact.patientreporter.actionability.ReportableEvidenceItemFactory;
import com.hartwig.oncoact.patientreporter.model.*;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.serve.datamodel.EvidenceLevel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.hartwig.oncoact.patientreporter.algo.wgs.EvidenceMatchCreator.createEvidenceMatch;

class TherapyCreator {

    static Therapy createTherapy(ReportSettings reportSettings, List<ProtectEvidence> reportableEvidence) {

        List<ProtectEvidence> tumorSpecificEvidence = ReportableEvidenceItemFactory.extractNonTrialsOnLabel(reportableEvidence);
        List<ProtectEvidence> clinicalTrials = ClinicalTrialFactory.extractOnLabelTrials(reportableEvidence);
        List<ProtectEvidence> offLabelEvidence = ReportableEvidenceItemFactory.extractNonTrialsOffLabel(reportableEvidence);

        List<ProtectEvidence> allEvidences = com.google.common.collect.Lists.newArrayList();
        allEvidences.addAll(tumorSpecificEvidence);
        allEvidences.addAll(offLabelEvidence);

        boolean flagGermline = reportSettings.getFlagGermlineOnReport();
        Map<String, List<ProtectEvidence>> treatments = buildTreatmentMap(allEvidences, flagGermline, null, "treatmentApproach");

        Map<String, List<ProtectEvidence>> trials = buildTreatmentMap(clinicalTrials, flagGermline, true, "study");

        return ImmutableTherapy.builder()
                .highLevelEvidence(selectReportableItemsTreatment(treatments))
                .clinicalStudies(selectReportableItemsTrials(trials))
                .build();
    }

    @NotNull
    public static Map<String, List<ProtectEvidence>> buildTreatmentMap(@NotNull List<ProtectEvidence> evidences, boolean reportGermline,
                                                                       Boolean requireOnLabel, @NotNull String name) {
        Map<String, List<ProtectEvidence>> evidencePerTreatmentMap = Maps.newHashMap();

        for (ProtectEvidence evidence : evidences) {
            if ((reportGermline || !evidence.germline()) && (requireOnLabel == null || evidence.onLabel() == requireOnLabel)) {
                String treatment = Strings.EMPTY;
                List<ProtectEvidence> treatmentEvidences = com.google.common.collect.Lists.newArrayList();
                Set<String> treatmentApproaches = evidence.treatment().sourceRelevantTreatmentApproaches();
                String treatmentJoin = String.join(",", treatmentApproaches);
                if (name.equals("treatmentApproach")) {
                    if (!treatmentJoin.isEmpty()) {
                        List<String> treatentSort = com.google.common.collect.Lists.newArrayList(evidence.treatment().sourceRelevantTreatmentApproaches());
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

    @NotNull
    private static List<HighLevelEvidence> selectReportableItemsTreatment(@NotNull Map<String, List<ProtectEvidence>> treatmentMap) {
        List<HighLevelEvidence> highLevelEvidences = Lists.newArrayList();
        for (EvidenceLevel level : EvidenceLevel.values()) {
            Set<String> sortedTreatments = Sets.newTreeSet(treatmentMap.keySet());
            for (String treatment : sortedTreatments) {
                List<ProtectEvidence> evidences = treatmentMap.get(treatment);
                if (level == highestEvidence(treatmentMap.get(treatment))) {
                    for (ProtectEvidence responsive : sort(evidences)) {
                        highLevelEvidences.add(HighLevelEvidence.builder()
                                .drugType(treatment)
                                .isTumorTypeSpecific(responsive.onLabel())
                                .matches(createEvidenceMatch(responsive.sources()))
                                .level(getEvidenceLevel(responsive.level()))
                                .isResponsive(getEvidenceDirection(responsive.direction()).isResponsive())
                                .isPredicted(getEvidenceDirection(responsive.direction()).isPredicted())
                                .genomicEvent(responsive.event())
                                .build());
                    }
                }
            }
        }
        return highLevelEvidences;
    }

    private static EvidenceDirection getEvidenceDirection(com.hartwig.serve.datamodel.EvidenceDirection direction) {
        switch (direction) {
            case RESPONSIVE:
                return com.hartwig.oncoact.patientreporter.model.EvidenceDirection.RESPONSIVE;
            case PREDICTED_RESPONSIVE:
                return com.hartwig.oncoact.patientreporter.model.EvidenceDirection.PREDICTED_RESPONSIVE;
            case NO_BENEFIT:
                return com.hartwig.oncoact.patientreporter.model.EvidenceDirection.NO_BENEFIT;
            case RESISTANT:
                return com.hartwig.oncoact.patientreporter.model.EvidenceDirection.RESISTANT;
            case PREDICTED_RESISTANT:
                return com.hartwig.oncoact.patientreporter.model.EvidenceDirection.PREDICTED_RESISTANT;
            default:
                throw new IllegalStateException("Unknown direction known: " + direction);
        }
    }

    private static com.hartwig.oncoact.patientreporter.model.EvidenceLevel getEvidenceLevel(EvidenceLevel level) {
        switch (level) {
            case A:
                return com.hartwig.oncoact.patientreporter.model.EvidenceLevel.A;
            case B:
                return com.hartwig.oncoact.patientreporter.model.EvidenceLevel.B;
            case C:
                return com.hartwig.oncoact.patientreporter.model.EvidenceLevel.C;
            case D:
                return com.hartwig.oncoact.patientreporter.model.EvidenceLevel.D;
            default:
                throw new IllegalStateException("Unknown evidence level known: " + level);
        }
    }


    @NotNull
    private static List<ClinicalStudy> selectReportableItemsTrials(@NotNull Map<String, List<ProtectEvidence>> treatmentMap) {
        List<ClinicalStudy> clinicalStudies = Lists.newArrayList();
        for (EvidenceLevel level : EvidenceLevel.values()) {
            Set<String> sortedTreatments = Sets.newTreeSet(treatmentMap.keySet());
            for (String treatment : sortedTreatments) {
                List<ProtectEvidence> evidences = treatmentMap.get(treatment);
                if (level == highestEvidence(treatmentMap.get(treatment))) {
                    for (ProtectEvidence responsive : sort(evidences)) {
                        clinicalStudies.add(ClinicalStudy.builder()
                                .clinicalStudy(treatment)
                                .matches(createEvidenceMatch(responsive.sources()))
                                .genomicEvent(responsive.event())
                                .build());
                    }
                }
            }
        }
        return clinicalStudies;
    }


    @NotNull
    private static EvidenceLevel highestEvidence(@NotNull List<ProtectEvidence> evidences) {
        EvidenceLevel highest = null;
        for (ProtectEvidence evidence : evidences) {
            if (highest == null || evidence.level().isHigher(highest)) {
                highest = evidence.level();
            }
        }

        return highest;
    }

    @NotNull
    private static List<ProtectEvidence> sort(@NotNull List<ProtectEvidence> evidenceItems) {
        return evidenceItems.stream().sorted((item1, item2) -> {
            if (item1.treatment().equals(item2.treatment())) {
                if (item1.level().equals(item2.level())) {
                    if (item1.direction().equals(item2.direction())) {
                        return compareOnNaturalOrder(item1.onLabel(), item2.onLabel());
                    } else {
                        return item1.direction().compareTo(item2.direction());
                    }
                } else {
                    return item1.level().compareTo(item2.level());
                }
            } else {
                return item1.treatment().name().compareTo(item2.treatment().name());
            }
        }).collect(Collectors.toList());
    }

    public static int compareOnNaturalOrder(boolean x, boolean y) {
        if (x == y) {
            return 0;
        } else {
            return x ? -1 : 1;
        }
    }
}