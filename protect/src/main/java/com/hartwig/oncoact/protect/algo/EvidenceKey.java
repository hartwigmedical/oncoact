package com.hartwig.oncoact.protect.algo;

import java.util.Objects;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.ProtectEvidence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class EvidenceKey {

    @Nullable
    private final String gene;
    @NotNull
    private final String event;
    @NotNull
    private final String treatment;
    @NotNull
    private final Set<String> treatmentApproachesDrugClass;
    @NotNull
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
        return new EvidenceKey(evidence.gene(),
                evidence.event(),
                evidence.treatment().name(),
                evidence.treatment().treatmentApproachesDrugClass(),
                evidence.treatment().treatmentApproachesTherapy());
    }

    private EvidenceKey(@Nullable final String gene, @NotNull final String event, @NotNull final String treatment,
            @NotNull Set<String> treatmentApproachesDrugClass, @NotNull Set<String> treatmentApproachesTherapy) {
        this.gene = gene;
        this.event = event;
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
    @NotNull
    String treatment() {
        return treatment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EvidenceKey that = (EvidenceKey) o;
        return Objects.equals(gene, that.gene) && Objects.equals(event, that.event) && Objects.equals(treatment, that.treatment)
                && Objects.equals(treatmentApproachesDrugClass, that.treatmentApproachesDrugClass) && Objects.equals(
                treatmentApproachesTherapy,
                that.treatmentApproachesTherapy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gene, event, treatment, treatmentApproachesDrugClass, treatmentApproachesTherapy);
    }

    @Override
    public String toString() {
        return "EvidenceKey{" + "gene='" + gene + '\'' + ", event='" + event + '\'' + ", treatment='" + treatment + '\''
                + ", treatmentApproachesDrugClass=" + treatmentApproachesDrugClass + ", treatmentApproachesTherapy="
                + treatmentApproachesTherapy + '}';
    }
}
