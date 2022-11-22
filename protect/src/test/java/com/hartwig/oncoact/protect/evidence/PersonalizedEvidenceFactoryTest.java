package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.common.protect.EvidenceType;
import com.hartwig.oncoact.protect.ServeTestFactory;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.cancertype.CancerType;
import com.hartwig.serve.datamodel.cancertype.ImmutableCancerType;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicAnnotation;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneLevelEvent;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.range.RangeType;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class PersonalizedEvidenceFactoryTest {

    @Test
    public void canDetermineOnLabel() {
        PersonalizedEvidenceFactory factoryOnLabelMatching = EvidenceTestFactory.create("162");
        ActionableHotspot hotspotOnLabelMatching = create("Cancer", "162");
        assertTrue(factoryOnLabelMatching.isOnLabel(hotspotOnLabelMatching.applicableCancerType(),
                hotspotOnLabelMatching.blacklistCancerTypes(),
                "treatment"));

        PersonalizedEvidenceFactory factoryNotBlacklisted = EvidenceTestFactory.create("10283");
        ActionableHotspot hotspotNotBlacklisted = create("prostate", "10283", "Breast", "0060081");
        assertTrue(factoryNotBlacklisted.isOnLabel(hotspotNotBlacklisted.applicableCancerType(),
                hotspotNotBlacklisted.blacklistCancerTypes(),
                "treatment"));

        PersonalizedEvidenceFactory factoryBlacklisted = EvidenceTestFactory.create("10283");
        ActionableHotspot hotspotBlacklisted = create("Cancer", "162", "Prostate", "10283");
        assertFalse(factoryBlacklisted.isOnLabel(hotspotBlacklisted.applicableCancerType(),
                hotspotBlacklisted.blacklistCancerTypes(),
                "treatment"));
    }

    @Test
    public void canDetermineBlacklistedEvidence() {
        PersonalizedEvidenceFactory factoryBlacklisted = EvidenceTestFactory.create("10283");
        ActionableHotspot hotspotBlacklisted = create("Cancer", "162", "Prostate", "10283");
        assertTrue(factoryBlacklisted.isBlacklisted(hotspotBlacklisted.blacklistCancerTypes(), "treatment"));

        PersonalizedEvidenceFactory factoryNotMatchWithBlacklisted = EvidenceTestFactory.create("0060081");
        ActionableHotspot hotspotNotMatchWithBlacklisted = create("Cancer", "162", "Prostate", "10283");
        assertFalse(factoryNotMatchWithBlacklisted.isBlacklisted(hotspotNotMatchWithBlacklisted.blacklistCancerTypes(), "treatment"));

        PersonalizedEvidenceFactory factoryNotBlacklisted = EvidenceTestFactory.create("10383");
        ActionableHotspot hotspotNotBlacklisted = create("Cancer", "162");
        assertFalse(factoryNotBlacklisted.isBlacklisted(hotspotNotBlacklisted.blacklistCancerTypes(), "treatment"));
    }

    @Test
    public void canDetermineEvidenceTypes() {
        assertEquals(EvidenceType.HOTSPOT_MUTATION,
                PersonalizedEvidenceFactory.determineEvidenceType(ServeTestFactory.createTestActionableHotspot()));

        ActionableRange range =
                ImmutableActionableRange.builder().from(ServeTestFactory.createTestActionableRange()).rangeType(RangeType.EXON).build();
        assertEquals(EvidenceType.EXON_MUTATION, PersonalizedEvidenceFactory.determineEvidenceType(range));

        ActionableGene gene = ImmutableActionableGene.builder()
                .from(ServeTestFactory.createTestActionableGene())
                .event(GeneLevelEvent.INACTIVATION)
                .build();
        assertEquals(EvidenceType.INACTIVATION, PersonalizedEvidenceFactory.determineEvidenceType(gene));

        ActionableGene amplification = ImmutableActionableGene.builder()
                .from(ServeTestFactory.createTestActionableGene())
                .event(GeneLevelEvent.AMPLIFICATION)
                .build();
        assertEquals(EvidenceType.AMPLIFICATION, PersonalizedEvidenceFactory.determineEvidenceType(amplification));

        ActionableGene overexpression = ImmutableActionableGene.builder()
                .from(ServeTestFactory.createTestActionableGene())
                .event(GeneLevelEvent.OVEREXPRESSION)
                .build();
        assertEquals(EvidenceType.OVER_EXPRESSION, PersonalizedEvidenceFactory.determineEvidenceType(overexpression));

        assertEquals(EvidenceType.FUSION_PAIR,
                PersonalizedEvidenceFactory.determineEvidenceType(ServeTestFactory.createTestActionableFusion()));

        assertEquals(EvidenceType.SIGNATURE,
                PersonalizedEvidenceFactory.determineEvidenceType(ServeTestFactory.createTestActionableCharacteristic()));
    }

    @Test
    public void canDetermineEvidenceTypesForAllRanges() {
        ActionableRange base = ServeTestFactory.createTestActionableRange();
        for (RangeType rangeType : RangeType.values()) {
            ActionableRange range = ImmutableActionableRange.builder().from(base).rangeType(rangeType).build();
            assertNotNull(PersonalizedEvidenceFactory.determineEvidenceType(range));
        }
    }

    @Test
    public void canDetermineEvidenceTypesFroAllGeneEvents() {
        ActionableGene base = ServeTestFactory.createTestActionableGene();
        for (GeneLevelEvent geneLevelEvent : GeneLevelEvent.values()) {
            ActionableGene gene = ImmutableActionableGene.builder().from(base).event(geneLevelEvent).build();
            assertNotNull(PersonalizedEvidenceFactory.determineEvidenceType(gene));
        }
    }

    @Test
    public void canDetermineEvidenceTypesForAllCharacteristics() {
        ActionableCharacteristic base = ServeTestFactory.createTestActionableCharacteristic();
        for (TumorCharacteristicAnnotation name : TumorCharacteristicAnnotation.values()) {
            ActionableCharacteristic characteristic = ImmutableActionableCharacteristic.builder().from(base).name(name).build();
            assertNotNull(PersonalizedEvidenceFactory.determineEvidenceType(characteristic));
        }
    }

    @Test
    public void canDetermineRangeRank() {
        ActionableRange range = ImmutableActionableRange.builder().from(ServeTestFactory.createTestActionableRange()).rank(2).build();

        assertEquals(2, (int) PersonalizedEvidenceFactory.determineRangeRank(range));

        assertNull(PersonalizedEvidenceFactory.determineRangeRank(ServeTestFactory.createTestActionableFusion()));
    }

    @NotNull
    private static ActionableHotspot create(@NotNull String cancerType, @NotNull String doid) {
        return create(cancerType, doid, null, null);
    }

    @NotNull
    private static ActionableHotspot create(@NotNull String cancerType, @NotNull String doid, @Nullable String blacklistCancerType,
            @Nullable String blacklistDoid) {
        Set<CancerType> blacklist = Sets.newHashSet();
        if (blacklistCancerType != null && blacklistDoid != null) {
            blacklist.add(ImmutableCancerType.builder().name(blacklistCancerType).doid(blacklistDoid).build());
        }

        ActionableEvent event = ServeTestFactory.create(Knowledgebase.CKB,
                "amp",
                Sets.newHashSet(),
                ImmutableTreatment.builder()
                        .treament("treatment A")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("drugClasses"))
                        .relevantTreatmentApproaches(Sets.newHashSet("drugClasses"))
                        .build(),
                ImmutableCancerType.builder().name(cancerType).doid(doid).build(),
                blacklist,
                EvidenceLevel.A,
                EvidenceDirection.RESPONSIVE,
                Sets.newHashSet());

        return ImmutableActionableHotspot.builder()
                .from(event)
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .build();
    }
}