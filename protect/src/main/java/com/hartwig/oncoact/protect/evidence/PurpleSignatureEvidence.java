package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.purple.PurpleCharacteristics;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleTumorMutationalStatus;
import com.hartwig.oncoact.protect.ImmutableProtectEvidence;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.characteristic.CharacteristicsFunctions;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PurpleSignatureEvidence {

    static final Set<TumorCharacteristicType> PURPLE_CHARACTERISTICS = Sets.newHashSet(TumorCharacteristicType.MICROSATELLITE_UNSTABLE,
            TumorCharacteristicType.MICROSATELLITE_STABLE,
            TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_LOAD,
            TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_LOAD,
            TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN,
            TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_BURDEN);

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableCharacteristic> actionableSignatures;

    public PurpleSignatureEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
            @NotNull final List<ActionableCharacteristic> actionableCharacteristics) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.actionableSignatures =
                actionableCharacteristics.stream().filter(x -> PURPLE_CHARACTERISTICS.contains(x.type())).collect(Collectors.toList());
    }

    @NotNull
    public List<ProtectEvidence> evidence(@NotNull PurpleCharacteristics characteristics,
            @Nullable PatientInformationResponse diagnosticPatientData) {
        List<ProtectEvidence> result = Lists.newArrayList();
        for (ActionableCharacteristic signature : actionableSignatures) {
            ProtectEvidence evidence;
            switch (signature.type()) {
                case MICROSATELLITE_UNSTABLE: {
                    evidence = evaluateMSI(signature, characteristics, diagnosticPatientData);
                    break;
                }
                case MICROSATELLITE_STABLE: {
                    evidence = evaluateMSS(signature, characteristics, diagnosticPatientData);
                    break;
                }
                case HIGH_TUMOR_MUTATIONAL_BURDEN: {
                    evidence = evaluateHighTMB(signature, characteristics, diagnosticPatientData);
                    break;
                }
                case LOW_TUMOR_MUTATIONAL_BURDEN: {
                    evidence = evaluateLowTMB(signature, characteristics, diagnosticPatientData);
                    break;
                }
                default: {
                    throw new IllegalStateException("Signature not a supported purple signature: " + signature.type());
                }
            }

            if (evidence != null) {
                result.add(evidence);
            }
        }

        return result;
    }

    @Nullable
    private ProtectEvidence evaluateMSI(@NotNull ActionableCharacteristic signature, @NotNull PurpleCharacteristics characteristics,
            @Nullable PatientInformationResponse diagnosticPatientData) {
        boolean isMatch = CharacteristicsFunctions.hasExplicitCutoff(signature) ? CharacteristicsFunctions.evaluateVersusCutoff(signature,
                characteristics.microsatelliteIndelsPerMb()) : characteristics.microsatelliteStatus() == PurpleMicrosatelliteStatus.MSI;

        return isMatch ? toEvidence(signature, diagnosticPatientData) : null;
    }

    @Nullable
    private ProtectEvidence evaluateMSS(@NotNull ActionableCharacteristic signature, @NotNull PurpleCharacteristics characteristics,
            @Nullable PatientInformationResponse diagnosticPatientData) {
        boolean isMatch = CharacteristicsFunctions.hasExplicitCutoff(signature) ? CharacteristicsFunctions.evaluateVersusCutoff(signature,
                characteristics.microsatelliteIndelsPerMb()) : characteristics.microsatelliteStatus() == PurpleMicrosatelliteStatus.MSS;

        return isMatch ? toEvidence(signature, diagnosticPatientData) : null;
    }

    @Nullable
    private ProtectEvidence evaluateHighTMB(@NotNull ActionableCharacteristic signature, @NotNull PurpleCharacteristics characteristics,
            @Nullable PatientInformationResponse diagnosticPatientData) {
        boolean isMatch = CharacteristicsFunctions.hasExplicitCutoff(signature)
                ? CharacteristicsFunctions.evaluateVersusCutoff(signature,
                characteristics.tumorMutationalBurdenPerMb())
                : characteristics.tumorMutationalBurdenStatus() == PurpleTumorMutationalStatus.HIGH;

        return isMatch ? toEvidence(signature, diagnosticPatientData) : null;
    }

    @Nullable
    private ProtectEvidence evaluateLowTMB(@NotNull ActionableCharacteristic signature, @NotNull PurpleCharacteristics characteristics,
            @Nullable PatientInformationResponse diagnosticPatientData) {
        boolean isMatch = CharacteristicsFunctions.hasExplicitCutoff(signature)
                ? CharacteristicsFunctions.evaluateVersusCutoff(signature,
                characteristics.tumorMutationalBurdenPerMb())
                : characteristics.tumorMutationalBurdenStatus() == PurpleTumorMutationalStatus.LOW;

        return isMatch ? toEvidence(signature, diagnosticPatientData) : null;
    }

    @NotNull
    private ProtectEvidence toEvidence(@NotNull ActionableCharacteristic signature,
            @Nullable PatientInformationResponse diagnosticPatientData) {
        ImmutableProtectEvidence.Builder builder;
        if (signature.type() == TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_LOAD
                || signature.type() == TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_BURDEN
                || signature.type() == TumorCharacteristicType.MICROSATELLITE_STABLE) {
            builder = personalizedEvidenceFactory.somaticEvidence(signature, diagnosticPatientData, false);
        } else {
            builder = personalizedEvidenceFactory.somaticReportableEvidence(signature, diagnosticPatientData, true);
        }

        return builder.event(toEvent(signature.type())).eventIsHighDriver(null).build();
    }

    @NotNull
    @VisibleForTesting
    static String toEvent(@NotNull TumorCharacteristicType characteristic) {
        String reformatted = characteristic.toString().replaceAll("_", " ");
        return reformatted.substring(0, 1).toUpperCase() + reformatted.substring(1).toLowerCase();
    }
}
