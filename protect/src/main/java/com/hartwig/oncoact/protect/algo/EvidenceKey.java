package com.hartwig.oncoact.protect.algo;

import java.util.Objects;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.util.ActionabilityIntervation;
import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.Treatment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class EvidenceKey {

    @Nullable
    private final String gene;
    @NotNull
    private final String event;
    @Nullable
    private final String clinicalTrial;
    @Nullable
    private final String treatment;
    @Nullable
    private final Set<String> treatmentApproachesDrugClass;
    @Nullable
    private final Set<String> treatmentApproachesTherapy;

    @NotNull
    public static Set<EvidenceKey> buildKeySet(@NotNull Iterable<ProtectEvidence> evidences) {
        Set<EvidenceKey> keys = Sets.newHashSet();
        for (ProtectEvidence evidence : evidences) {
            keys.add(create(evidence));
        }
        return keys;
    }

    @NotNull
    public static EvidenceKey create(@NotNull ProtectEvidence evidence) {
        Treatment treatmentModel = evidence.treatment();
        ClinicalTrial clinicalTrialModel = evidence.clinicalTrial();

        return new EvidenceKey(evidence.gene(),
                evidence.event(),
                clinicalTrialModel != null ? clinicalTrialModel.studyNctId() : null,
                ActionabilityIntervation.therapyName(clinicalTrialModel, treatmentModel),
                treatmentModel != null ? treatmentModel.treatmentApproachesDrugClass() : null,
                treatmentModel != null ? treatmentModel.treatmentApproachesTherapy() : null);
    }

    private EvidenceKey(@Nullable final String gene, @NotNull final String event, @Nullable final String clinicalTrial,
            @Nullable final String treatment, @Nullable Set<String> treatmentApproachesDrugClass,
            @Nullable Set<String> treatmentApproachesTherapy) {
        this.gene = gene;
        this.event = event;
        this.clinicalTrial = clinicalTrial;
        this.treatment = treatment;
        this.treatmentApproachesDrugClass = treatmentApproachesDrugClass;
        this.treatmentApproachesTherapy = treatmentApproachesTherapy;
    }

    @VisibleForTesting
    @Nullable
    String gene() {
        return gene;
    }

    @VisibleForTesting
    @NotNull
    String event() {
        return event;
    }

    @VisibleForTesting
    @Nullable
    String treatment() {
        return treatment;
    }

    @Override
    public String toString() {
        return "EvidenceKey{" + "gene='" + gene + '\'' + ", event='" + event + '\'' + ", clinicalTrial='" + clinicalTrial + '\''
                + ", treatment='" + treatment + '\'' + ", treatmentApproachesDrugClass=" + treatmentApproachesDrugClass
                + ", treatmentApproachesTherapy=" + treatmentApproachesTherapy + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EvidenceKey that = (EvidenceKey) o;
        return Objects.equals(gene, that.gene) && Objects.equals(event, that.event) && Objects.equals(clinicalTrial, that.clinicalTrial)
                && Objects.equals(treatment, that.treatment) && Objects.equals(treatmentApproachesDrugClass,
                that.treatmentApproachesDrugClass) && Objects.equals(treatmentApproachesTherapy, that.treatmentApproachesTherapy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gene, event, clinicalTrial, treatment, treatmentApproachesDrugClass, treatmentApproachesTherapy);
    }

}
