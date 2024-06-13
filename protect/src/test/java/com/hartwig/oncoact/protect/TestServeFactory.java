package com.hartwig.oncoact.protect;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableCancerType;
import com.hartwig.serve.datamodel.ImmutableClinicalTrial;
import com.hartwig.serve.datamodel.Intervention;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableActionableRange;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestServeFactory {

    private TestServeFactory() {
    }

    @NotNull
    public static ActionableHotspot createTestActionableHotspot() {
        return hotspotBuilder().build();
    }

    @NotNull
    public static ImmutableActionableHotspot.Builder hotspotBuilder() {
        return ImmutableActionableHotspot.builder()
                .from(createTestBaseEvent())
                .gene(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY);
    }

    @NotNull
    public static ActionableRange createTestActionableRange() {
        return rangeBuilder().build();
    }

    @NotNull
    public static ImmutableActionableRange.Builder rangeBuilder() {
        return ImmutableActionableRange.builder()
                .from(createTestBaseEvent())
                .gene(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .start(0)
                .end(0)
                .applicableMutationType(MutationType.ANY);
    }

    @NotNull
    public static ActionableGene createTestActionableGene() {
        return geneBuilder().build();
    }

    @NotNull
    public static ImmutableActionableGene.Builder geneBuilder() {
        return ImmutableActionableGene.builder().from(createTestBaseEvent()).gene(Strings.EMPTY).event(GeneEvent.ANY_MUTATION);
    }

    @NotNull
    public static ActionableFusion createTestActionableFusion() {
        return fusionBuilder().build();
    }

    @NotNull
    public static ImmutableActionableFusion.Builder fusionBuilder() {
        return ImmutableActionableFusion.builder().from(createTestBaseEvent()).geneUp(Strings.EMPTY).geneDown(Strings.EMPTY);
    }

    @NotNull
    public static ActionableCharacteristic createTestActionableCharacteristic() {
        return characteristicBuilder().build();
    }

    @NotNull
    public static ImmutableActionableCharacteristic.Builder characteristicBuilder() {
        return ImmutableActionableCharacteristic.builder()
                .from(createTestBaseEvent())
                .type(TumorCharacteristicType.MICROSATELLITE_UNSTABLE);
    }

    @NotNull
    public static ActionableHLA createTestActionableHLA() {
        return hlaBuilder().build();
    }

    @NotNull
    public static ImmutableActionableHLA.Builder hlaBuilder() {
        return ImmutableActionableHLA.builder().from(createTestBaseEvent()).hlaAllele(Strings.EMPTY);
    }

    @NotNull
    private static ActionableEvent createTestBaseEvent() {
        return createTestBaseEvent(Knowledgebase.UNKNOWN);
    }

    @NotNull
    private static ActionableEvent createTestBaseEvent(@NotNull Knowledgebase source) {
        return create(source,
                "source event",
                Sets.newHashSet(),
                ImmutableClinicalTrial.builder()
                        .studyNctId("nct1")
                        .studyTitle("title")
                        .countriesOfStudy(Sets.newHashSet("Netherlands"))
                        .build(),
                ImmutableCancerType.builder().name(Strings.EMPTY).doid(Strings.EMPTY).build(),
                Sets.newHashSet(),
                EvidenceLevel.D,
                EvidenceDirection.RESPONSIVE,
                Sets.newHashSet());
    }

    @NotNull
    public static ActionableEvent create(@NotNull Knowledgebase source, @NotNull String sourceEvent, @NotNull Set<String> sourceUrls,
            @NotNull Intervention intervention, @NotNull CancerType applicableCancerType, @NotNull Set<CancerType> blacklistCancerTypes,
            @NotNull EvidenceLevel level, @NotNull EvidenceDirection direction, @NotNull Set<String> evidenceUrls) {
        return new ActionableEventImpl(source,
                sourceEvent,
                sourceUrls,
                intervention,
                applicableCancerType,
                blacklistCancerTypes,
                level,
                direction,
                evidenceUrls);
    }

    private static class ActionableEventImpl implements ActionableEvent {

        @NotNull
        private final Knowledgebase source;
        @NotNull
        private final String sourceEvent;
        @NotNull
        private final Set<String> sourceUrls;
        @NotNull
        private final Intervention intervention;
        @NotNull
        private final CancerType applicableCancerType;
        @NotNull
        private final Set<CancerType> blacklistCancerTypes;
        @NotNull
        private final EvidenceLevel level;
        @NotNull
        private final EvidenceDirection direction;
        @NotNull
        private final Set<String> evidenceUrls;

        public ActionableEventImpl(@NotNull Knowledgebase source, @NotNull String sourceEvent, @NotNull Set<String> sourceUrls,
                @NotNull Intervention intervention, @NotNull CancerType applicableCancerType, @NotNull Set<CancerType> blacklistCancerTypes,
                @NotNull EvidenceLevel level, @NotNull EvidenceDirection direction, @NotNull Set<String> evidenceUrls) {
            this.source = source;
            this.sourceEvent = sourceEvent;
            this.sourceUrls = sourceUrls;
            this.intervention = intervention;
            this.applicableCancerType = applicableCancerType;
            this.blacklistCancerTypes = blacklistCancerTypes;
            this.level = level;
            this.direction = direction;
            this.evidenceUrls = evidenceUrls;
        }

        @NotNull
        @Override
        public Knowledgebase source() {
            return source;
        }

        @NotNull
        @Override
        public String sourceEvent() {
            return sourceEvent;
        }

        @NotNull
        @Override
        public Set<String> sourceUrls() {
            return sourceUrls;
        }

        @NotNull
        @Override
        public Intervention intervention() {
            return intervention;
        }

        @NotNull
        @Override
        public CancerType applicableCancerType() {
            return applicableCancerType;
        }

        @NotNull
        @Override
        public Set<CancerType> blacklistCancerTypes() {
            return blacklistCancerTypes;
        }

        @NotNull
        @Override
        public EvidenceLevel level() {
            return level;
        }

        @NotNull
        @Override
        public EvidenceDirection direction() {
            return direction;
        }

        @NotNull
        @Override
        public Set<String> evidenceUrls() {
            return evidenceUrls;
        }

        @Override
        public String toString() {
            return "ActionableEventImpl{" + "source=" + source + ", sourceEvent='" + sourceEvent + '\'' + ", sourceUrls=" + sourceUrls
                    + ", intervention=" + intervention + ", applicableCancerType=" + applicableCancerType + ", blacklistCancerTypes="
                    + blacklistCancerTypes + ", level=" + level + ", direction=" + direction + ", evidenceUrls=" + evidenceUrls + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ActionableEventImpl that = (ActionableEventImpl) o;
            return source == that.source && Objects.equals(sourceEvent, that.sourceEvent) && Objects.equals(sourceUrls, that.sourceUrls)
                    && Objects.equals(intervention, that.intervention) && Objects.equals(applicableCancerType, that.applicableCancerType)
                    && Objects.equals(blacklistCancerTypes, that.blacklistCancerTypes) && level == that.level && direction == that.direction
                    && Objects.equals(evidenceUrls, that.evidenceUrls);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source,
                    sourceEvent,
                    sourceUrls,
                    intervention,
                    applicableCancerType,
                    blacklistCancerTypes,
                    level,
                    direction,
                    evidenceUrls);
        }
    }
}
