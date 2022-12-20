package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.orange.datamodel.virus.VirusDriverLikelihood;
import com.hartwig.oncoact.orange.datamodel.virus.VirusInterpretation;
import com.hartwig.oncoact.orange.datamodel.virus.VirusInterpreterEntry;
import com.hartwig.oncoact.orange.datamodel.virus.VirusInterpreterRecord;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;

import org.jetbrains.annotations.NotNull;

public class VirusEvidence {

    static final String HPV_POSITIVE_EVENT = "HPV positive";
    static final String EBV_POSITIVE_EVENT = "EBV positive";

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableCharacteristic> actionableViruses;

    public VirusEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
            @NotNull final List<ActionableCharacteristic> actionableCharacteristics) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.actionableViruses = actionableCharacteristics.stream()
                .filter(x -> x.type() == TumorCharacteristicType.HPV_POSITIVE || x.type() == TumorCharacteristicType.EBV_POSITIVE)
                .collect(Collectors.toList());
    }

    @NotNull
    public List<ProtectEvidence> evidence(@NotNull VirusInterpreterRecord virusInterpreter) {
        List<VirusInterpreterEntry> hpv = virusesWithInterpretation(virusInterpreter, VirusInterpretation.HPV);
        List<VirusInterpreterEntry> ebv = virusesWithInterpretation(virusInterpreter, VirusInterpretation.EBV);

        boolean reportHPV = hasReportedWithHighDriverLikelihood(hpv);
        boolean reportEBV = hasReportedWithHighDriverLikelihood(ebv);

        List<ProtectEvidence> result = Lists.newArrayList();
        for (ActionableCharacteristic virus : actionableViruses) {
            switch (virus.type()) {
                case HPV_POSITIVE: {
                    if (!hpv.isEmpty()) {
                        ProtectEvidence evidence = personalizedEvidenceFactory.somaticEvidence(virus)
                                .reported(reportHPV)
                                .event(HPV_POSITIVE_EVENT)
                                .eventIsHighDriver(EvidenceDriverLikelihood.interpretVirus())
                                .build();
                        result.add(evidence);
                    }
                    break;
                }
                case EBV_POSITIVE: {
                    if (!ebv.isEmpty()) {
                        ProtectEvidence evidence = personalizedEvidenceFactory.somaticEvidence(virus)
                                .reported(reportEBV)
                                .event(EBV_POSITIVE_EVENT)
                                .eventIsHighDriver(EvidenceDriverLikelihood.interpretVirus())
                                .build();
                        result.add(evidence);
                    }
                    break;
                }
            }
        }
        return result;
    }

    private static boolean hasReportedWithHighDriverLikelihood(@NotNull List<VirusInterpreterEntry> viruses) {
        for (VirusInterpreterEntry virus : viruses) {
            if (virus.reported() && virus.driverLikelihood() == VirusDriverLikelihood.HIGH) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    private static List<VirusInterpreterEntry> virusesWithInterpretation(@NotNull VirusInterpreterRecord virusInterpreter,
            @NotNull VirusInterpretation interpretationToInclude) {
        List<VirusInterpreterEntry> virusesWithInterpretation = Lists.newArrayList();
        for (VirusInterpreterEntry virus : virusInterpreter.reportableViruses()) {
            if (virus.interpretation() == interpretationToInclude) {
                virusesWithInterpretation.add(virus);
            }
        }

        for (VirusInterpreterEntry virus : virusInterpreter.allViruses()) {
            if ((!virusInterpreter.reportableViruses().contains(virus) && virus.interpretation() == interpretationToInclude)) {
                virusesWithInterpretation.add(virus);
            }
        }
        return virusesWithInterpretation;
    }
}
