package com.hartwig.oncoact.protect.evidence;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.orange.datamodel.lilac.LilacHlaAllele;
import com.hartwig.oncoact.orange.datamodel.lilac.LilacRecord;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;

import org.jetbrains.annotations.NotNull;

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

    public List<ProtectEvidence> evidence(@NotNull LilacRecord lilac) {
        List<ProtectEvidence> result = Lists.newArrayList();
        for (LilacHlaAllele lilacAllele : lilac.alleles()) {
            result.addAll(evidence(lilacAllele, lilac.qc()));
        }

        return result;
    }

    @NotNull
    private List<ProtectEvidence> evidence(@NotNull LilacHlaAllele lilacAllele, @NotNull String lilacQc) {
        List<ProtectEvidence> result = Lists.newArrayList();

        for (ActionableHLA hla : actionableHLA) {
            if (hla.hlaAllele().equals(lilacAllele.allele().split(":")[0])) {
                ProtectEvidence evidence = personalizedEvidenceFactory.evidenceBuilder(hla)
                        .event(lilacAllele.allele())
                        .reported(lilacQc.equals("PASS"))
                        .germline(false)
                        .build();
                result.add(evidence);
            }
        }
        return result;
    }
}