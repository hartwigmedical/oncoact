package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.chord.ChordRecord;
import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.characteristic.CharacteristicsFunctions;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;

import org.jetbrains.annotations.NotNull;

public class ChordEvidence {

    static final String HR_DEFICIENCY_EVENT = "HR deficiency";

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableCharacteristic> actionableCharacteristics;

    public ChordEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
            @NotNull final List<ActionableCharacteristic> actionableCharacteristics) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.actionableCharacteristics = actionableCharacteristics.stream()
                .filter(x -> x.type() == TumorCharacteristicType.HOMOLOGOUS_RECOMBINATION_DEFICIENT)
                .collect(Collectors.toList());
    }

    @NotNull
    public List<ProtectEvidence> evidence(@NotNull ChordRecord chordAnalysis) {
        List<ProtectEvidence> result = Lists.newArrayList();
        if (chordAnalysis.hrStatus() == ChordStatus.HR_DEFICIENT) {
            for (ActionableCharacteristic characteristic : actionableCharacteristics) {
                if (CharacteristicsFunctions.hasExplicitCutoff(characteristic)) {
                    if (CharacteristicsFunctions.evaluateVersusCutoff(characteristic, chordAnalysis.hrdValue())) {
                        result.add(toHRDEvidence(characteristic));
                    }
                } else {
                    result.add(toHRDEvidence(characteristic));
                }
            }
        }
        return result;
    }

    @NotNull
    private ProtectEvidence toHRDEvidence(@NotNull ActionableCharacteristic signature) {
        return personalizedEvidenceFactory.somaticReportableEvidence(signature).event(HR_DEFICIENCY_EVENT).eventIsHighDriver(null).build();
    }
}
