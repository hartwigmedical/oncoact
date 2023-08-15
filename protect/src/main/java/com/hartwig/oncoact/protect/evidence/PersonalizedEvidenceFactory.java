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
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;

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
    public ImmutableProtectEvidence.Builder somaticEvidence(@NotNull ActionableEvent event) {
        return evidenceBuilder(event).reported(false).germline(false);
    }

    @NotNull
    public ImmutableProtectEvidence.Builder somaticReportableEvidence(@NotNull ActionableEvent event) {
        return evidenceBuilder(event).reported(true).germline(false);
    }

    @NotNull
    public ImmutableProtectEvidence.Builder evidenceBuilderRange(@NotNull ActionableEvent actionable, @Nullable String range,
            @Nullable Integer rangeRank) {
        return evidenceBuilder(actionable, Sets.newHashSet(resolveProtectSource(actionable, range, rangeRank)));
    }

    @NotNull
    public ImmutableProtectEvidence.Builder evidenceBuilder(@NotNull ActionableEvent actionable) {
        return evidenceBuilder(actionable, Sets.newHashSet(resolveProtectSource(actionable, null, null)));
    }

    @NotNull
    public ImmutableProtectEvidence.Builder evidenceBuilder(@NotNull ActionableEvent actionable,
            @NotNull Set<KnowledgebaseSource> protectSource) {
        return ImmutableProtectEvidence.builder()
                .treatment(ImmutableTreatment.builder()
                        .name(actionable.treatment().name())
                        .sourceRelevantTreatmentApproaches(actionable.treatment().sourceRelevantTreatmentApproaches())
                        .relevantTreatmentApproaches(actionable.treatment().relevantTreatmentApproaches())
                        .build())
                .onLabel(isOnLabel(actionable.applicableCancerType(), actionable.blacklistCancerTypes(), actionable.treatment().name()))
                .level(actionable.level())
                .direction(actionable.direction())
                .sources(protectSource);
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
    static EvidenceType determineEvidenceType(@NotNull ActionableEvent actionable, @Nullable String range) {
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
            throw new IllegalStateException("Unexpected actionable event detected in variant evidence: " + actionable);
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
            case DELETION:
                return EvidenceType.DELETION;
            case UNDEREXPRESSION:
                return EvidenceType.UNDER_EXPRESSION;
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