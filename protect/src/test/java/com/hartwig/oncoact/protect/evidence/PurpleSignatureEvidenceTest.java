package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.orange.purple.PurpleCharacteristics;
import com.hartwig.oncoact.orange.purple.PurpleMicrosatelliteStatus;
import com.hartwig.oncoact.orange.purple.PurpleTumorMutationalStatus;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicCutoffType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class PurpleSignatureEvidenceTest {

    @Test
    public void canHandleNonPurpleSignatureEvidence() {
        ActionableCharacteristic nonPurple =
                TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.HOMOLOGOUS_RECOMBINATION_DEFICIENT).build();

        PurpleSignatureEvidence purpleSignatureEvidence =
                new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(nonPurple));

        PurpleCharacteristics characteristics =
                createCharacteristics(PurpleMicrosatelliteStatus.MSI, PurpleTumorMutationalStatus.HIGH, 4.0, 241, 0.0);
        assertEquals(0, purpleSignatureEvidence.evidence(characteristics).size());
    }

    @Test
    public void canDetermineMSI() {
        ActionableCharacteristic signatureDefault =
                TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.MICROSATELLITE_UNSTABLE).build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder()
                .type(TumorCharacteristicType.MICROSATELLITE_UNSTABLE)
                .cutoffType(TumorCharacteristicCutoffType.EQUAL_OR_GREATER)
                .cutoff(4D)
                .build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSI, PurpleTumorMutationalStatus.HIGH, 4.0, 241, 0.0);
        assertEquals(2, purpleSignatureEvidence.evidence(both).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSI, PurpleTumorMutationalStatus.HIGH, 3.0, 241, 0.0);
        assertEquals(1, purpleSignatureEvidence.evidence(one).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSS, PurpleTumorMutationalStatus.HIGH, 3.0, 241, 0.0);
        assertEquals(0, purpleSignatureEvidence.evidence(none).size());
    }

    @Test
    public void canDetermineMSS() {
        ActionableCharacteristic signatureDefault =
                TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.MICROSATELLITE_STABLE).build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder()
                .type(TumorCharacteristicType.MICROSATELLITE_STABLE)
                .cutoffType(TumorCharacteristicCutoffType.LOWER)
                .cutoff(4D)
                .build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSS, PurpleTumorMutationalStatus.HIGH, 2.0, 241, 0.0);
        assertEquals(2, purpleSignatureEvidence.evidence(both).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSS, PurpleTumorMutationalStatus.HIGH, 5.0, 241, 0.0);
        assertEquals(1, purpleSignatureEvidence.evidence(one).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSI, PurpleTumorMutationalStatus.HIGH, 5.0, 241, 0.0);
        assertEquals(0, purpleSignatureEvidence.evidence(none).size());
    }

    @Test
    public void canDetermineHighTML() {
        ActionableCharacteristic signatureDefault =
                TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_LOAD).build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder()
                .type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_LOAD)
                .cutoffType(TumorCharacteristicCutoffType.GREATER)
                .cutoff(100D)
                .build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSS, PurpleTumorMutationalStatus.HIGH, 2.0, 241, 0.0);
        assertEquals(2, purpleSignatureEvidence.evidence(both).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSS, PurpleTumorMutationalStatus.LOW, 5.0, 120, 0.0);
        assertEquals(1, purpleSignatureEvidence.evidence(one).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSI, PurpleTumorMutationalStatus.LOW, 5.0, 80, 0.0);
        assertEquals(0, purpleSignatureEvidence.evidence(none).size());
    }

    @Test
    public void canDetermineLowTML() {
        ActionableCharacteristic signatureDefault =
                TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_LOAD).build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder()
                .type(TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_LOAD)
                .cutoffType(TumorCharacteristicCutoffType.EQUAL_OR_LOWER)
                .cutoff(100D)
                .build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSS, PurpleTumorMutationalStatus.LOW, 2.0, 80, 0.0);
        assertEquals(2, purpleSignatureEvidence.evidence(both).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSS, PurpleTumorMutationalStatus.HIGH, 5.0, 100, 0.0);
        assertEquals(1, purpleSignatureEvidence.evidence(one).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSI, PurpleTumorMutationalStatus.HIGH, 5.0, 120, 0.0);
        assertEquals(0, purpleSignatureEvidence.evidence(none).size());
    }

    @Test
    public void canDetermineHighTMB() {
        ActionableCharacteristic signatureDefault =
                TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN).build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder()
                .type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN)
                .cutoffType(TumorCharacteristicCutoffType.EQUAL_OR_GREATER)
                .cutoff(12D)
                .build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSS, PurpleTumorMutationalStatus.LOW, 2.0, 80, 12.0);
        assertEquals(2, purpleSignatureEvidence.evidence(both).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSS, PurpleTumorMutationalStatus.HIGH, 5.0, 100, 11.0);
        assertEquals(1, purpleSignatureEvidence.evidence(one).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSI, PurpleTumorMutationalStatus.HIGH, 5.0, 120, 8.0);
        assertEquals(0, purpleSignatureEvidence.evidence(none).size());
    }

    @Test
    public void canDetermineLowTMB() {
        ActionableCharacteristic signatureDefault = TestServeFactory.characteristicBuilder()
                .type(TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_BURDEN)
                .build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder()
                .type(TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_BURDEN)
                .cutoffType(TumorCharacteristicCutoffType.LOWER)
                .cutoff(8D)
                .build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSS, PurpleTumorMutationalStatus.LOW, 2.0, 80, 7D);
        assertEquals(2, purpleSignatureEvidence.evidence(both).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSS, PurpleTumorMutationalStatus.HIGH, 5.0, 100, 9D);
        assertEquals(1, purpleSignatureEvidence.evidence(one).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSI, PurpleTumorMutationalStatus.HIGH, 5.0, 120, 11D);
        assertEquals(0, purpleSignatureEvidence.evidence(none).size());
    }

    @Test
    public void canConvertCharacteristicToEvent() {
        assertEquals("Microsatellite unstable", PurpleSignatureEvidence.toEvent(TumorCharacteristicType.MICROSATELLITE_UNSTABLE));
    }

    @NotNull
    private static PurpleCharacteristics createCharacteristics(@NotNull PurpleMicrosatelliteStatus msStatus,
            @NotNull PurpleTumorMutationalStatus tmlStatus, double microsatelliteIndelsPerMb, int tumorMutationalLoad,
            double tumorMutationalBurden) {
        return TestPurpleFactory.characteristicsBuilder()
                .microsatelliteIndelsPerMb(microsatelliteIndelsPerMb)
                .microsatelliteStabilityStatus(msStatus)
                .tumorMutationalLoad(tumorMutationalLoad)
                .tumorMutationalLoadStatus(tmlStatus)
                .tumorMutationalBurden(tumorMutationalBurden)
                .build();
    }
}