package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.common.protect.ImmutableProtectEvidence;
import com.hartwig.oncoact.common.protect.ProtectEvidence;
import com.hartwig.oncoact.common.purple.TumorMutationalStatus;
import com.hartwig.oncoact.common.purple.loader.PurpleData;
import com.hartwig.oncoact.common.variant.msi.MicrosatelliteStatus;
import com.hartwig.oncoact.protect.characteristic.CharacteristicsFunctions;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PurpleSignatureEvidence {

    private static final double DEFAULT_HIGH_TMB_CUTOFF = 10D;

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
    public List<ProtectEvidence> evidence(@NotNull PurpleData purpleData) {
        List<ProtectEvidence> result = Lists.newArrayList();
        for (ActionableCharacteristic signature : actionableSignatures) {
            ProtectEvidence evidence;
            switch (signature.type()) {
                case MICROSATELLITE_UNSTABLE: {
                    evidence = evaluateMSI(signature, purpleData);
                    break;
                }
                case MICROSATELLITE_STABLE: {
                    evidence = evaluateMSS(signature, purpleData);
                    break;
                }
                case HIGH_TUMOR_MUTATIONAL_LOAD: {
                    evidence = evaluateHighTML(signature, purpleData);
                    break;
                }
                case LOW_TUMOR_MUTATIONAL_LOAD: {
                    evidence = evaluateLowTML(signature, purpleData);
                    break;
                }
                case HIGH_TUMOR_MUTATIONAL_BURDEN: {
                    evidence = evaluateHighTMB(signature, purpleData);
                    break;
                }
                case LOW_TUMOR_MUTATIONAL_BURDEN: {
                    evidence = evaluateLowTMB(signature, purpleData);
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
    private ProtectEvidence evaluateMSI(@NotNull ActionableCharacteristic signature, @NotNull PurpleData purpleData) {
        boolean isMatch = CharacteristicsFunctions.hasExplicitCutoff(signature) ? CharacteristicsFunctions.evaluateVersusCutoff(signature,
                purpleData.microsatelliteIndelsPerMb()) : purpleData.microsatelliteStatus() == MicrosatelliteStatus.MSI;

        return isMatch ? toEvidence(signature) : null;
    }

    @Nullable
    private ProtectEvidence evaluateMSS(@NotNull ActionableCharacteristic signature, @NotNull PurpleData purpleData) {
        boolean isMatch = CharacteristicsFunctions.hasExplicitCutoff(signature) ? CharacteristicsFunctions.evaluateVersusCutoff(signature,
                purpleData.microsatelliteIndelsPerMb()) : purpleData.microsatelliteStatus() == MicrosatelliteStatus.MSS;

        return isMatch ? toEvidence(signature) : null;
    }

    @Nullable
    private ProtectEvidence evaluateHighTML(@NotNull ActionableCharacteristic signature, @NotNull PurpleData purpleData) {
        boolean isMatch = CharacteristicsFunctions.hasExplicitCutoff(signature) ? CharacteristicsFunctions.evaluateVersusCutoff(signature,
                purpleData.tumorMutationalLoad()) : purpleData.tumorMutationalLoadStatus() == TumorMutationalStatus.HIGH;

        return isMatch ? toEvidence(signature) : null;
    }

    @Nullable
    private ProtectEvidence evaluateLowTML(@NotNull ActionableCharacteristic signature, @NotNull PurpleData purpleData) {
        boolean isMatch = CharacteristicsFunctions.hasExplicitCutoff(signature) ? CharacteristicsFunctions.evaluateVersusCutoff(signature,
                purpleData.tumorMutationalLoad()) : purpleData.tumorMutationalLoadStatus() == TumorMutationalStatus.LOW;

        return isMatch ? toEvidence(signature) : null;
    }

    @Nullable
    private ProtectEvidence evaluateHighTMB(@NotNull ActionableCharacteristic signature, @NotNull PurpleData purpleData) {
        boolean isMatch = CharacteristicsFunctions.hasExplicitCutoff(signature) ? CharacteristicsFunctions.evaluateVersusCutoff(signature,
                purpleData.tumorMutationalBurdenPerMb()) : purpleData.tumorMutationalBurdenPerMb() >= DEFAULT_HIGH_TMB_CUTOFF;

        return isMatch ? toEvidence(signature) : null;
    }

    @Nullable
    private ProtectEvidence evaluateLowTMB(@NotNull ActionableCharacteristic signature, @NotNull PurpleData purpleData) {
        boolean isMatch = CharacteristicsFunctions.hasExplicitCutoff(signature) ? CharacteristicsFunctions.evaluateVersusCutoff(signature,
                purpleData.tumorMutationalBurdenPerMb()) : purpleData.tumorMutationalBurdenPerMb() < DEFAULT_HIGH_TMB_CUTOFF;

        return isMatch ? toEvidence(signature) : null;
    }

    @NotNull
    private ProtectEvidence toEvidence(@NotNull ActionableCharacteristic signature) {
        ImmutableProtectEvidence.Builder builder;
        // TODO: Figure out whether we want to report evidence on "absence of signatures".
        if (signature.type() == TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_LOAD
                || signature.type() == TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_BURDEN
                || signature.type() == TumorCharacteristicType.MICROSATELLITE_STABLE) {
            builder = personalizedEvidenceFactory.somaticEvidence(signature);
        } else {
            builder = personalizedEvidenceFactory.somaticReportableEvidence(signature);
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
