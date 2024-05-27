package com.hartwig.oncoact.protect.evidence;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.hla.LilacAllele;
import com.hartwig.hmftools.datamodel.hla.LilacRecord;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HlaEvidence {

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableHLA> actionableHLA;

    public HlaEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
                       @NotNull final List<ActionableHLA> actionableHLA) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.actionableHLA = actionableHLA;
    }

    public List<ProtectEvidence> evidence(@NotNull LilacRecord lilac, @Nullable PatientInformationResponse diagnosticPatientData) {
        List<ProtectEvidence> result = Lists.newArrayList();
        for (LilacAllele lilacAllele : lilac.alleles()) {
            result.addAll(evidence(lilacAllele, lilac.qc(), diagnosticPatientData));
        }

        return result;
    }

    @NotNull
    private List<ProtectEvidence> evidence(@NotNull LilacAllele lilacAllele, @NotNull String lilacQc, @Nullable PatientInformationResponse diagnosticPatientData) {
        List<ProtectEvidence> result = Lists.newArrayList();

        for (ActionableHLA hla : actionableHLA) {
            if (hla.hlaAllele().equals(lilacAllele.allele().split(":")[0])) {
                ProtectEvidence evidence = personalizedEvidenceFactory.evidenceBuilder(hla, diagnosticPatientData, lilacQc.equals("PASS"))
                        .event(lilacAllele.allele())
                        .germline(false)
                        .build();
                result.add(evidence);
            }
        }
        return result;
    }
}