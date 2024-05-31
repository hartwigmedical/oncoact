package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableCancerType;
import com.hartwig.serve.datamodel.ImmutableClinicalTrial;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class PersonalizedEvidenceFactoryTest {

    @Test
    public void canDetermineOnLabel() {
        PersonalizedEvidenceFactory factoryOnLabelMatching = TestPersonalizedEvidenceFactory.create("162");
        ActionableHotspot hotspotOnLabelMatching = create("Cancer", "162");
        assertTrue(factoryOnLabelMatching.isOnLabel(hotspotOnLabelMatching.applicableCancerType(),
                hotspotOnLabelMatching.blacklistCancerTypes(),
                "treatment"));

        PersonalizedEvidenceFactory factoryNotBlacklisted = TestPersonalizedEvidenceFactory.create("10283");
        ActionableHotspot hotspotNotBlacklisted = create("prostate", "10283", "Breast", "0060081");
        assertTrue(factoryNotBlacklisted.isOnLabel(hotspotNotBlacklisted.applicableCancerType(),
                hotspotNotBlacklisted.blacklistCancerTypes(),
                "treatment"));

        PersonalizedEvidenceFactory factoryBlacklisted = TestPersonalizedEvidenceFactory.create("10283");
        ActionableHotspot hotspotBlacklisted = create("Cancer", "162", "Prostate", "10283");
        assertFalse(factoryBlacklisted.isOnLabel(hotspotBlacklisted.applicableCancerType(),
                hotspotBlacklisted.blacklistCancerTypes(),
                "treatment"));
    }

    @Test
    public void canDetermineMatchGender() {
        assertNull(PersonalizedEvidenceFactory.matchGender(null, null));
        assertNull(PersonalizedEvidenceFactory.matchGender("both", null));
        assertNull(PersonalizedEvidenceFactory.matchGender(null, "female"));

        assertTrue(PersonalizedEvidenceFactory.matchGender("female", "female"));
        assertTrue(PersonalizedEvidenceFactory.matchGender("male", "male"));
        assertTrue(PersonalizedEvidenceFactory.matchGender("both", "female"));
        assertTrue(PersonalizedEvidenceFactory.matchGender("both", "male"));

        assertFalse(PersonalizedEvidenceFactory.matchGender("female", "male"));
        assertFalse(PersonalizedEvidenceFactory.matchGender("male", "female"));
    }

    @Test
    public void canDetermineReportable() {
        assertTrue(PersonalizedEvidenceFactory.isReportable(true, true));
        assertTrue(PersonalizedEvidenceFactory.isReportable(null, true));

        assertFalse(PersonalizedEvidenceFactory.isReportable(false, true));
        assertFalse(PersonalizedEvidenceFactory.isReportable(false, false));
        assertFalse(PersonalizedEvidenceFactory.isReportable(null, false));

    }

    @Test
    public void canDetermineBlacklistedEvidence() {
        PersonalizedEvidenceFactory factoryBlacklisted = TestPersonalizedEvidenceFactory.create("10283");
        ActionableHotspot hotspotBlacklisted = create("Cancer", "162", "Prostate", "10283");
        assertTrue(factoryBlacklisted.isBlacklisted(hotspotBlacklisted.blacklistCancerTypes(), "treatment"));

        PersonalizedEvidenceFactory factoryNotMatchWithBlacklisted = TestPersonalizedEvidenceFactory.create("0060081");
        ActionableHotspot hotspotNotMatchWithBlacklisted = create("Cancer", "162", "Prostate", "10283");
        assertFalse(factoryNotMatchWithBlacklisted.isBlacklisted(hotspotNotMatchWithBlacklisted.blacklistCancerTypes(), "treatment"));

        PersonalizedEvidenceFactory factoryNotBlacklisted = TestPersonalizedEvidenceFactory.create("10383");
        ActionableHotspot hotspotNotBlacklisted = create("Cancer", "162");
        assertFalse(factoryNotBlacklisted.isBlacklisted(hotspotNotBlacklisted.blacklistCancerTypes(), "treatment"));
    }

    @Test
    public void canDetermineEvidenceTypes() {
        ActionableHotspot hotspot = TestServeFactory.createTestActionableHotspot();
        assertEquals(EvidenceType.HOTSPOT_MUTATION, PersonalizedEvidenceFactory.determineEvidenceType(hotspot, null));

        ActionableRange codon = TestServeFactory.createTestActionableRange();
        assertEquals(EvidenceType.CODON_MUTATION, PersonalizedEvidenceFactory.determineEvidenceType(codon, "codon"));

        ActionableRange exon = TestServeFactory.createTestActionableRange();
        assertEquals(EvidenceType.EXON_MUTATION, PersonalizedEvidenceFactory.determineEvidenceType(exon, "exon"));

        ActionableGene gene = TestServeFactory.geneBuilder().event(GeneEvent.INACTIVATION).build();
        assertEquals(EvidenceType.INACTIVATION, PersonalizedEvidenceFactory.determineEvidenceType(gene, null));

        ActionableGene amplification = TestServeFactory.geneBuilder().event(GeneEvent.AMPLIFICATION).build();
        assertEquals(EvidenceType.AMPLIFICATION, PersonalizedEvidenceFactory.determineEvidenceType(amplification, null));

        ActionableGene overexpression = TestServeFactory.geneBuilder().event(GeneEvent.OVEREXPRESSION).build();
        assertEquals(EvidenceType.OVER_EXPRESSION, PersonalizedEvidenceFactory.determineEvidenceType(overexpression, null));

        ActionableFusion fusion = TestServeFactory.createTestActionableFusion();
        assertEquals(EvidenceType.FUSION_PAIR, PersonalizedEvidenceFactory.determineEvidenceType(fusion, null));

        ActionableCharacteristic characteristic = TestServeFactory.createTestActionableCharacteristic();
        assertEquals(EvidenceType.SIGNATURE, PersonalizedEvidenceFactory.determineEvidenceType(characteristic, null));

        ActionableHLA hla = TestServeFactory.createTestActionableHLA();
        assertEquals(EvidenceType.HLA, PersonalizedEvidenceFactory.determineEvidenceType(hla, null));
    }

    @Test
    public void canDetermineEvidenceTypesFroAllGeneEvents() {
        ActionableGene base = TestServeFactory.createTestActionableGene();
        for (GeneEvent geneLevelEvent : GeneEvent.values()) {
            ActionableGene gene = ImmutableActionableGene.builder().from(base).event(geneLevelEvent).build();
            assertNotNull(PersonalizedEvidenceFactory.determineEvidenceType(gene, null));
        }
    }

    @Test
    public void canDetermineEvidenceTypesForAllCharacteristics() {
        ActionableCharacteristic base = TestServeFactory.createTestActionableCharacteristic();
        for (TumorCharacteristicType type : TumorCharacteristicType.values()) {
            ActionableCharacteristic characteristic = ImmutableActionableCharacteristic.builder().from(base).type(type).build();
            assertNotNull(PersonalizedEvidenceFactory.determineEvidenceType(characteristic, null));
        }
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

        ActionableEvent event = TestServeFactory.create(Knowledgebase.CKB_EVIDENCE,
                "amp",
                Sets.newHashSet(),
                ImmutableClinicalTrial.builder()
                        .studyNctId("nct1")
                        .studyTitle("title")
                        .countriesOfStudy(Sets.newHashSet("Netherlands"))
                        .build(),
                ImmutableCancerType.builder().name(cancerType).doid(doid).build(),
                blacklist,
                EvidenceLevel.A,
                EvidenceDirection.RESPONSIVE,
                Sets.newHashSet());

        return ImmutableActionableHotspot.builder()
                .from(event)
                .gene(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .build();
    }
}