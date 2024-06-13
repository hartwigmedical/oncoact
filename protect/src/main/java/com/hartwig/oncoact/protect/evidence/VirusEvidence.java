package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.virus.AnnotatedVirus;
import com.hartwig.hmftools.datamodel.virus.VirusInterpretation;
import com.hartwig.hmftools.datamodel.virus.VirusInterpreterData;
import com.hartwig.hmftools.datamodel.virus.VirusLikelihoodType;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public List<ProtectEvidence> evidence(@NotNull VirusInterpreterData virusInterpreter,
            @Nullable PatientInformationResponse diagnosticPatientData) {
        List<AnnotatedVirus> hpv = virusesWithInterpretation(virusInterpreter, VirusInterpretation.HPV);
        List<AnnotatedVirus> ebv = virusesWithInterpretation(virusInterpreter, VirusInterpretation.EBV);

        boolean reportHPV = hasReportedWithHighDriverLikelihood(hpv);
        boolean reportEBV = hasReportedWithHighDriverLikelihood(ebv);

        List<ProtectEvidence> result = Lists.newArrayList();
        for (ActionableCharacteristic virus : actionableViruses) {
            switch (virus.type()) {
                case HPV_POSITIVE: {
                    if (!hpv.isEmpty()) {
                        ProtectEvidence evidence = personalizedEvidenceFactory.somaticEvidence(virus, diagnosticPatientData, reportHPV)
                                .event(HPV_POSITIVE_EVENT)
                                .eventIsHighDriver(EvidenceDriverLikelihood.interpretVirus())
                                .build();
                        result.add(evidence);
                    }
                    break;
                }
                case EBV_POSITIVE: {
                    if (!ebv.isEmpty()) {
                        ProtectEvidence evidence = personalizedEvidenceFactory.somaticEvidence(virus, diagnosticPatientData, reportEBV)
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

    private static boolean hasReportedWithHighDriverLikelihood(@NotNull List<AnnotatedVirus> viruses) {
        for (AnnotatedVirus virus : viruses) {
            if (virus.reported() && virus.virusDriverLikelihoodType() == VirusLikelihoodType.HIGH) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    private static List<AnnotatedVirus> virusesWithInterpretation(@NotNull VirusInterpreterData virusInterpreter,
            @NotNull VirusInterpretation interpretationToInclude) {
        List<AnnotatedVirus> virusesWithInterpretation = Lists.newArrayList();
        for (AnnotatedVirus virus : virusInterpreter.reportableViruses()) {
            if (virus.interpretation() == interpretationToInclude) {
                virusesWithInterpretation.add(virus);
            }
        }

        for (AnnotatedVirus virus : virusInterpreter.allViruses()) {
            if ((!virusInterpreter.reportableViruses().contains(virus) && virus.interpretation() == interpretationToInclude)) {
                virusesWithInterpretation.add(virus);
            }
        }
        return virusesWithInterpretation;
    }
}
