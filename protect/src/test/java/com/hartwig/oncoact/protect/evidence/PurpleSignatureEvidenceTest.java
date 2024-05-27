package com.hartwig.oncoact.protect.evidence;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.PurpleCharacteristics;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleTumorMutationalStatus;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicCutoffType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PurpleSignatureEvidenceTest {

    @Test
    public void canHandleNonPurpleSignatureEvidence() {
        ActionableCharacteristic nonPurple = TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.HOMOLOGOUS_RECOMBINATION_DEFICIENT).build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(nonPurple));

        PurpleCharacteristics characteristics = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 4.0, 0.0, PurpleTumorMutationalStatus.LOW);
        assertEquals(0, purpleSignatureEvidence.evidence(characteristics, null).size());
    }

    @Test
    public void canDetermineMSI() {
        ActionableCharacteristic signatureDefault = TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.MICROSATELLITE_UNSTABLE).build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.MICROSATELLITE_UNSTABLE).cutoffType(TumorCharacteristicCutoffType.EQUAL_OR_GREATER).cutoff(4D).build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 4.0, 0.0, PurpleTumorMutationalStatus.LOW);
        assertEquals(2, purpleSignatureEvidence.evidence(both, null).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 3.0, 0.0, PurpleTumorMutationalStatus.LOW);
        assertEquals(1, purpleSignatureEvidence.evidence(one, null).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSS, 3.0, 0.0, PurpleTumorMutationalStatus.LOW);
        assertEquals(0, purpleSignatureEvidence.evidence(none, null).size());
    }

    @Test
    public void canDetermineMSS() {
        ActionableCharacteristic signatureDefault = TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.MICROSATELLITE_STABLE).build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.MICROSATELLITE_STABLE).cutoffType(TumorCharacteristicCutoffType.LOWER).cutoff(4D).build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSS, 2.0, 0.0, PurpleTumorMutationalStatus.LOW);
        assertEquals(2, purpleSignatureEvidence.evidence(both, null).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSS, 5.0, 0.0, PurpleTumorMutationalStatus.LOW);
        assertEquals(1, purpleSignatureEvidence.evidence(one, null).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 5.0, 0.0, PurpleTumorMutationalStatus.LOW);
        assertEquals(0, purpleSignatureEvidence.evidence(none, null).size());
    }

    @Test
    public void canDetermineHighTMB() {
        ActionableCharacteristic signatureDefault = TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN).build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN).cutoffType(TumorCharacteristicCutoffType.EQUAL_OR_GREATER).cutoff(10D).build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 5.0, 18, PurpleTumorMutationalStatus.HIGH);
        assertEquals(2, purpleSignatureEvidence.evidence(both, null).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 5.0, 8.0, PurpleTumorMutationalStatus.LOW);
        assertEquals(0, purpleSignatureEvidence.evidence(none, null).size());
    }

    @Test
    public void canDetermineLowTMB() {
        ActionableCharacteristic signatureDefault = TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_BURDEN).build();

        ActionableCharacteristic signatureWithCutoff = TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_BURDEN).cutoffType(TumorCharacteristicCutoffType.LOWER).cutoff(8D).build();

        PurpleSignatureEvidence purpleSignatureEvidence = new PurpleSignatureEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(signatureDefault, signatureWithCutoff));

        PurpleCharacteristics both = createCharacteristics(PurpleMicrosatelliteStatus.MSS, 2.0, 7D, PurpleTumorMutationalStatus.LOW);
        assertEquals(2, purpleSignatureEvidence.evidence(both, null).size());

        PurpleCharacteristics one = createCharacteristics(PurpleMicrosatelliteStatus.MSS, 3.0, 9D, PurpleTumorMutationalStatus.LOW);
        assertEquals(1, purpleSignatureEvidence.evidence(one, null).size());

        PurpleCharacteristics none = createCharacteristics(PurpleMicrosatelliteStatus.MSI, 5.0, 17D, PurpleTumorMutationalStatus.HIGH);
        assertEquals(0, purpleSignatureEvidence.evidence(none, null).size());
    }

    @Test
    public void canConvertCharacteristicToEvent() {
        assertEquals("Microsatellite unstable", PurpleSignatureEvidence.toEvent(TumorCharacteristicType.MICROSATELLITE_UNSTABLE));
    }

    @NotNull
    private static PurpleCharacteristics createCharacteristics(@NotNull PurpleMicrosatelliteStatus msStatus, double microsatelliteIndelsPerMb, double tumorMutationalBurden, @NotNull PurpleTumorMutationalStatus status) {
        return TestPurpleFactory.characteristicsBuilder().microsatelliteIndelsPerMb(microsatelliteIndelsPerMb).microsatelliteStatus(msStatus).tumorMutationalBurdenPerMb(tumorMutationalBurden).tumorMutationalBurdenStatus(status).build();
    }
}