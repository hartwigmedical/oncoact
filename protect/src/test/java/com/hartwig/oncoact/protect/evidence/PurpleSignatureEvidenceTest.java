package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.PurpleCharacteristics;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
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

        PurpleCharacteristics characteristics = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 4.0, 0.0);
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

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 4.0, 0.0);
        assertEquals(2, purpleSignatureEvidence.evidence(both).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 3.0, 0.0);
        assertEquals(1, purpleSignatureEvidence.evidence(one).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSS, 3.0, 0.0);
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

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSS, 2.0, 0.0);
        assertEquals(2, purpleSignatureEvidence.evidence(both).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSS, 5.0, 0.0);
        assertEquals(1, purpleSignatureEvidence.evidence(one).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 5.0, 0.0);
        assertEquals(0, purpleSignatureEvidence.evidence(none).size());
    }

    @Test
    public void canDetermineHighTMB() {
        ActionableCharacteristic signatureDefault =
                TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN).build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder()
                .type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN)
                .cutoffType(TumorCharacteristicCutoffType.EQUAL_OR_GREATER)
                .cutoff(16D)
                .build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSS, 2.0, 18);
        assertEquals(2, purpleSignatureEvidence.evidence(both).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 5.0, 8.0);
        assertEquals(0, purpleSignatureEvidence.evidence(none).size());
    }

    @Test
    public void canDetermineLowTMB() {
        ActionableCharacteristic signatureDefault =
                TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_BURDEN).build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder()
                .type(TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_BURDEN)
                .cutoffType(TumorCharacteristicCutoffType.LOWER)
                .cutoff(8D)
                .build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(),
                Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSS, 2.0, 7D);
        assertEquals(2, purpleSignatureEvidence.evidence(both).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSS, 3.0, 9D);
        assertEquals(1, purpleSignatureEvidence.evidence(one).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 5.0, 17D);
        assertEquals(0, purpleSignatureEvidence.evidence(none).size());
    }

    @Test
    public void canConvertCharacteristicToEvent() {
        assertEquals("Microsatellite unstable", PurpleSignatureEvidence.toEvent(TumorCharacteristicType.MICROSATELLITE_UNSTABLE));
    }

    @NotNull
    private static PurpleCharacteristics createCharacteristics(@NotNull PurpleMicrosatelliteStatus msStatus,
            double microsatelliteIndelsPerMb, double tumorMutationalBurden) {
        return TestPurpleFactory.characteristicsBuilder()
                .microsatelliteIndelsPerMb(microsatelliteIndelsPerMb)
                .microsatelliteStatus(msStatus)
                .tumorMutationalBurdenPerMb(tumorMutationalBurden)
                .build();
    }
}