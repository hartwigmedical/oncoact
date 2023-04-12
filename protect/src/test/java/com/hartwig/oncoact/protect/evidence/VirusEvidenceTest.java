package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.virus.AnnotatedVirus;
import com.hartwig.hmftools.datamodel.virus.ImmutableVirusInterpreterData;
import com.hartwig.hmftools.datamodel.virus.VirusInterpretation;
import com.hartwig.hmftools.datamodel.virus.VirusInterpreterData;
import com.hartwig.hmftools.datamodel.virus.VirusLikelihoodType;
import com.hartwig.oncoact.orange.virus.TestVirusInterpreterFactory;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class VirusEvidenceTest {

    @Test
    public void canDetermineEvidenceForViruses() {
        VirusInterpreterData testRecord = createTestVirusInterpreterRecord();

        ActionableCharacteristic hpv = TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.HPV_POSITIVE).build();
        ActionableCharacteristic ebv = TestServeFactory.characteristicBuilder().type(TumorCharacteristicType.EBV_POSITIVE).build();

        VirusEvidence virusEvidence = new VirusEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(hpv, ebv));

        List<ProtectEvidence> evidences = virusEvidence.evidence(testRecord);
        assertEquals(2, evidences.size());

        // The test data has a reportable HPV virus
        ProtectEvidence hpvEvidence = find(evidences, VirusEvidence.HPV_POSITIVE_EVENT);
        assertTrue(hpvEvidence.reported());

        // The test data has a reportable LOW driver EBV virus
        ProtectEvidence ebvEvidence = find(evidences, VirusEvidence.EBV_POSITIVE_EVENT);
        assertFalse(ebvEvidence.reported());
    }

    @NotNull
    private static ProtectEvidence find(@NotNull List<ProtectEvidence> evidences, @NotNull String event) {
        for (ProtectEvidence evidence : evidences) {
            if (evidence.event().equals(event)) {
                return evidence;
            }
        }

        throw new IllegalStateException("Could not find evidence with genomic event: " + event);
    }

    @NotNull
    private static VirusInterpreterData createTestVirusInterpreterRecord() {
        Set<AnnotatedVirus> reportableViruses = Sets.newHashSet();
        reportableViruses.add(TestVirusInterpreterFactory.builder()
                .reported(true)
                .interpretation(VirusInterpretation.HPV)
                .virusDriverLikelihoodType(VirusLikelihoodType.HIGH)
                .build());
        reportableViruses.add(TestVirusInterpreterFactory.builder()
                .reported(true)
                .interpretation(VirusInterpretation.MCV)
                .virusDriverLikelihoodType(VirusLikelihoodType.LOW)
                .build());
        reportableViruses.add(TestVirusInterpreterFactory.builder()
                .reported(true)
                .interpretation(VirusInterpretation.EBV)
                .virusDriverLikelihoodType(VirusLikelihoodType.LOW)
                .build());
        reportableViruses.add(TestVirusInterpreterFactory.builder().reported(true).virusDriverLikelihoodType(VirusLikelihoodType.UNKNOWN).build());

        Set<AnnotatedVirus> allViruses = Sets.newHashSet();
        allViruses.addAll(reportableViruses);
        allViruses.add(TestVirusInterpreterFactory.builder()
                .reported(false)
                .interpretation(VirusInterpretation.EBV)
                .virusDriverLikelihoodType(VirusLikelihoodType.HIGH)
                .build());
        allViruses.add(TestVirusInterpreterFactory.builder()
                .reported(false)
                .interpretation(VirusInterpretation.EBV)
                .virusDriverLikelihoodType(VirusLikelihoodType.HIGH)
                .build());
        allViruses.add(TestVirusInterpreterFactory.builder().reported(false).virusDriverLikelihoodType(VirusLikelihoodType.UNKNOWN).build());

        return ImmutableVirusInterpreterData.builder().reportableViruses(reportableViruses).allViruses(allViruses).build();
    }
}