package com.hartwig.oncoact.protect.evidence;

import java.util.Objects;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.doid.DoidParents;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ImmutableKnowledgebaseSource;
import com.hartwig.oncoact.protect.ImmutableProtectEvidence;
import com.hartwig.oncoact.protect.KnowledgebaseSource;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.Treatment;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PersonalizedEvidenceFactory {

    private static final Logger LOGGER = LogManager.getLogger(PersonalizedEvidenceFactory.class);

    @NotNull
    private final Set<String> patientTumorDoids;

    @NotNull
    private final DoidParents doidParentModel;

    public PersonalizedEvidenceFactory(@NotNull final Set<String> patientTumorDoids, @NotNull final DoidParents doidParentModel) {
        this.patientTumorDoids = patientTumorDoids;
        this.doidParentModel = doidParentModel;
    }

    @NotNull
    public ImmutableProtectEvidence.Builder somaticEvidence(@NotNull ActionableEvent event,
            @Nullable PatientInformationResponse diagnosticPatientData, boolean report) {
        return evidenceBuilder(event, diagnosticPatientData, report).germline(false);
    }

    @NotNull
    public ImmutableProtectEvidence.Builder somaticReportableEvidence(@NotNull ActionableEvent event,
            @Nullable PatientInformationResponse diagnosticPatientData, boolean report) {
        return evidenceBuilder(event, diagnosticPatientData, report).germline(false);
    }

    @NotNull
    public ImmutableProtectEvidence.Builder evidenceBuilderRange(@NotNull ActionableEvent actionable, @Nullable String range,
            @Nullable Integer rangeRank, @Nullable PatientInformationResponse diagnosticPatientData, boolean report) {
        return evidenceBuilder(actionable,
                Sets.newHashSet(resolveProtectSource(actionable, range, rangeRank)),
                diagnosticPatientData,
                report);
    }

    @NotNull
    public ImmutableProtectEvidence.Builder evidenceBuilder(@NotNull ActionableEvent actionable,
            @Nullable PatientInformationResponse diagnosticPatientData, boolean report) {
        return evidenceBuilderRange(actionable, null, null, diagnosticPatientData, report);
    }

    @NotNull
    public ImmutableProtectEvidence.Builder evidenceBuilder(@NotNull ActionableEvent actionable,
            @NotNull Set<KnowledgebaseSource> protectSource, @Nullable PatientInformationResponse diagnosticPatientData, boolean report) {
        ClinicalTrial clinicalTrial = extractOptionalClinicalTrial(actionable);
        String genderCkb = clinicalTrial != null ? clinicalTrial.gender() : null;
        String diagnosticGender = diagnosticPatientData != null ? diagnosticPatientData.getGender() : null;

        Boolean matchGender = matchGender(genderCkb, diagnosticGender);
        return ImmutableProtectEvidence.builder()
                .reported(isReportable(matchGender, report))
                .clinicalTrial(extractOptionalClinicalTrial(actionable))
                .matchGender(matchGender)
                .treatment(extractOptionalTreatment(actionable))
                .onLabel(isOnLabel(actionable.applicableCancerType(), actionable.blacklistCancerTypes(), ""))
                .level(actionable.level())
                .direction(actionable.direction())
                .sources(protectSource);
    }

    @Nullable
    private static Treatment extractOptionalTreatment(@NotNull ActionableEvent event) {
        Treatment treatment = null;
        if (event.intervention() instanceof Treatment) {
            treatment = (Treatment) event.intervention();
        }
        return treatment;
    }

    @Nullable
    private static ClinicalTrial extractOptionalClinicalTrial(@NotNull ActionableEvent event) {
        ClinicalTrial clinicalTrial = null;
        if (event.intervention() instanceof ClinicalTrial) {
            clinicalTrial = (ClinicalTrial) event.intervention();
        }

        return clinicalTrial;
    }

    public static boolean isReportable(@Nullable Boolean gender, boolean reportable) {
        if (reportable) {
            return gender == null || gender;
        } else {
            return false;
        }
    }

    public static Boolean matchGender(@Nullable String genderCkb, @Nullable String diagnosticGender) {
        if (diagnosticGender != null && genderCkb != null) {
            if (genderCkb.equalsIgnoreCase(diagnosticGender)) {
                return true;
            } else if (genderCkb.equalsIgnoreCase("both")) {
                return diagnosticGender.equalsIgnoreCase("female") || diagnosticGender.equalsIgnoreCase("male");
            } else {
                return false;
            }
        }
        return null;
    }

    public boolean isOnLabel(@NotNull CancerType applicableCancerType, @NotNull Set<CancerType> blacklistCancerTypes,
            @NotNull String treatment) {
        return patientTumorDoids.contains(applicableCancerType.doid());
    }

    @VisibleForTesting
    boolean isBlacklisted(@NotNull Set<CancerType> blacklistCancerTypes, @NotNull String treatment) {
        Set<String> blacklistDoids = extractDoidStrings(blacklistCancerTypes);
        Set<String> allDoids = Sets.newHashSet();

        if (!blacklistDoids.isEmpty()) {
            LOGGER.info(" Starting doid resolving for blacklisting evidence  '{}' for treatment '{}'", blacklistDoids, treatment);
        }

        for (String doid : blacklistDoids) {
            allDoids.add(doid);
            allDoids.addAll(doidParentModel.parents(doid));
        }

        for (String result : allDoids) {
            for (String doidPatient : patientTumorDoids) {
                if (doidPatient.equals(result)) {
                    return true;
                }
            }
        }
        return false;
    }

    @NotNull
    private static Set<String> extractDoidStrings(@NotNull Set<CancerType> cancerTypes) {
        Set<String> doids = Sets.newHashSet();
        for (CancerType cancerType : cancerTypes) {
            doids.add(cancerType.doid());
        }
        return doids;
    }

    @NotNull
    private static KnowledgebaseSource resolveProtectSource(@NotNull ActionableEvent actionable, @Nullable String range,
            @Nullable Integer rangeRank) {
        return ImmutableKnowledgebaseSource.builder()
                .name(actionable.source())
                .sourceEvent(actionable.sourceEvent())
                .sourceUrls(Sets.newTreeSet(actionable.sourceUrls()))
                .evidenceType(determineEvidenceType(actionable, range))
                .rangeRank(rangeRank)
                .evidenceUrls(Sets.newTreeSet(actionable.evidenceUrls()))
                .build();
    }

    @VisibleForTesting
    @NotNull
    public static EvidenceType determineEvidenceType(@NotNull ActionableEvent actionable, @Nullable String range) {
        if (actionable instanceof ActionableHotspot) {
            return EvidenceType.HOTSPOT_MUTATION;
        } else if (actionable instanceof ActionableRange) {
            return fromActionableRange(actionable, range);
        } else if (actionable instanceof ActionableGene) {
            return fromActionableGene((ActionableGene) actionable);
        } else if (actionable instanceof ActionableFusion) {
            return EvidenceType.FUSION_PAIR;
        } else if (actionable instanceof ActionableCharacteristic) {
            return fromActionableCharacteristic((ActionableCharacteristic) actionable);
        } else if (actionable instanceof ActionableHLA) {
            return EvidenceType.HLA;
        } else {
            throw new IllegalStateException("Unexpected actionable event detected in evidence: " + actionable);
        }
    }

    @NotNull
    private static EvidenceType fromActionableRange(final @NotNull ActionableEvent actionable, final @Nullable String range) {
        if (Objects.equals(range, "codon")) {
            return EvidenceType.CODON_MUTATION;
        } else if (Objects.equals(range, "exon")) {
            return EvidenceType.EXON_MUTATION;
        } else {
            throw new IllegalStateException("Unexpected actionable event detected in evidence " + actionable + "with range " + range);
        }
    }

    @NotNull
    private static EvidenceType fromActionableGene(@NotNull ActionableGene gene) {
        switch (gene.event()) {
            case AMPLIFICATION:
                return EvidenceType.AMPLIFICATION;
            case OVEREXPRESSION:
                return EvidenceType.OVER_EXPRESSION;
            case PRESENCE_OF_PROTEIN:
                return EvidenceType.PRESENCE_OF_PROTEIN;
            case DELETION:
                return EvidenceType.DELETION;
            case UNDEREXPRESSION:
                return EvidenceType.UNDER_EXPRESSION;
            case ABSENCE_OF_PROTEIN:
                return EvidenceType.ABSENCE_OF_PROTEIN;
            case ACTIVATION:
                return EvidenceType.ACTIVATION;
            case INACTIVATION:
                return EvidenceType.INACTIVATION;
            case ANY_MUTATION:
                return EvidenceType.ANY_MUTATION;
            case FUSION:
                return EvidenceType.PROMISCUOUS_FUSION;
            case WILD_TYPE:
                return EvidenceType.WILD_TYPE;
            default: {
                throw new IllegalStateException("Unsupported gene level event: " + gene.event());
            }
        }
    }

    @NotNull
    private static EvidenceType fromActionableCharacteristic(@NotNull ActionableCharacteristic characteristic) {
        switch (characteristic.type()) {
            case MICROSATELLITE_UNSTABLE:
            case MICROSATELLITE_STABLE:
            case HIGH_TUMOR_MUTATIONAL_LOAD:
            case LOW_TUMOR_MUTATIONAL_LOAD:
            case HIGH_TUMOR_MUTATIONAL_BURDEN:
            case LOW_TUMOR_MUTATIONAL_BURDEN:
            case HOMOLOGOUS_RECOMBINATION_DEFICIENT:
                return EvidenceType.SIGNATURE;
            case HPV_POSITIVE:
            case EBV_POSITIVE:
                return EvidenceType.VIRAL_PRESENCE;
            default: {
                throw new IllegalStateException("Unsupported tumor characteristic: " + characteristic.type());
            }
        }
    }
}