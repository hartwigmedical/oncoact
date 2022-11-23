package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.common.fusion.KnownFusionType;
import com.hartwig.oncoact.common.linx.LinxFusion;
import com.hartwig.oncoact.common.protect.EventGenerator;
import com.hartwig.oncoact.common.protect.ProtectEvidence;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FusionEvidence {

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableGene> actionablePromiscuous;
    @NotNull
    private final List<ActionableFusion> actionableFusions;

    public FusionEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
            @NotNull final List<ActionableGene> actionableGenes, @NotNull final List<ActionableFusion> actionableFusions) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.actionablePromiscuous = actionableGenes.stream()
                .filter(x -> x.event().equals(GeneEvent.FUSION) || x.event() == GeneEvent.ACTIVATION
                        || x.event() == GeneEvent.ANY_MUTATION)
                .collect(Collectors.toList());
        this.actionableFusions = actionableFusions;
    }

    @NotNull
    public List<ProtectEvidence> evidence(@NotNull List<LinxFusion> reportableFusions, @NotNull List<LinxFusion> allFusions) {
        List<ProtectEvidence> evidences = Lists.newArrayList();
        for (LinxFusion reportable : reportableFusions) {
            evidences.addAll(evidence(reportable));
        }

        for (LinxFusion allFusion : allFusions) {
            if (!allFusion.reported()) {
                evidences.addAll(evidence(allFusion));
            }
        }
        return evidences;
    }

    @NotNull
    private List<ProtectEvidence> evidence(@NotNull LinxFusion fusion) {
        List<ProtectEvidence> evidences = Lists.newArrayList();
        for (ActionableGene promiscuous : actionablePromiscuous) {
            if (promiscuous.event().equals(GeneEvent.FUSION) && match(fusion, promiscuous)) {
                evidences.add(evidence(fusion, promiscuous));
            }
            if (promiscuous.event().equals(GeneEvent.ACTIVATION) && match(fusion, promiscuous)) {
                evidences.add(unreportableEvidence(fusion, promiscuous));
            }

            if (promiscuous.event().equals(GeneEvent.ANY_MUTATION) && match(fusion, promiscuous)) {
                evidences.add(unreportableEvidence(fusion, promiscuous));
            }
        }

        for (ActionableFusion actionableFusion : actionableFusions) {
            if (match(fusion, actionableFusion)) {
                evidences.add(evidence(fusion, actionableFusion));
            }
        }
        return evidences;
    }

    @NotNull
    private ProtectEvidence unreportableEvidence(@NotNull LinxFusion fusion, @NotNull ActionableEvent actionable) {
        return personalizedEvidenceFactory.somaticEvidence(actionable)
                .reported(false)
                .gene(geneFromActionable(actionable))
                .event(EventGenerator.fusionEvent(fusion))
                .eventIsHighDriver(EvidenceDriverLikelihood.interpretFusion(fusion.likelihood()))
                .build();
    }

    @NotNull
    private ProtectEvidence evidence(@NotNull LinxFusion fusion, @NotNull ActionableEvent actionable) {
        return personalizedEvidenceFactory.somaticEvidence(actionable)
                .reported(fusion.reported())
                .gene(geneFromActionable(actionable))
                .event(EventGenerator.fusionEvent(fusion))
                .eventIsHighDriver(EvidenceDriverLikelihood.interpretFusion(fusion.likelihood()))
                .build();
    }

    private boolean match(@NotNull LinxFusion fusion, @NotNull ActionableGene actionable) {
        if (fusion.reportedType().equals(KnownFusionType.PROMISCUOUS_3.toString())) {
            return actionable.gene().equals(fusion.geneEnd());
        } else if (fusion.reportedType().equals(KnownFusionType.PROMISCUOUS_5.toString())) {
            return actionable.gene().equals(fusion.geneStart());
        } else {
            return actionable.gene().equals(fusion.geneStart()) || actionable.gene().equals(fusion.geneEnd());
        }
    }

    private static boolean match(@NotNull LinxFusion fusion, @NotNull ActionableFusion actionable) {
        if (fusion.reportedType().equals(KnownFusionType.KNOWN_PAIR.toString()) || fusion.reportedType()
                .equals(KnownFusionType.EXON_DEL_DUP.toString()) || fusion.reportedType()
                .equals(KnownFusionType.IG_KNOWN_PAIR.toString())) {
            if (!actionable.geneDown().equals(fusion.geneEnd())) {
                return false;
            }

            if (!actionable.geneUp().equals(fusion.geneStart())) {
                return false;
            }

            Integer actionableMinExonDown = actionable.minExonDown();
            if (actionableMinExonDown != null && fusion.fusedExonDown() < actionableMinExonDown) {
                return false;
            }

            Integer actionableMaxExonDown = actionable.maxExonDown();
            if (actionableMaxExonDown != null && fusion.fusedExonDown() > actionableMaxExonDown) {
                return false;
            }

            Integer actionableMinExonUp = actionable.minExonUp();
            if (actionableMinExonUp != null && fusion.fusedExonUp() < actionableMinExonUp) {
                return false;
            }

            Integer actionableMaxExonUp = actionable.maxExonUp();
            if (actionableMaxExonUp != null && fusion.fusedExonUp() > actionableMaxExonUp) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Nullable
    private static String geneFromActionable(@NotNull ActionableEvent actionable) {
        if (actionable instanceof ActionableGene) {
            return ((ActionableGene) actionable).gene();
        } else if (actionable instanceof ActionableFusion) {
            return null;
        } else {
            throw new IllegalStateException("Unexpected actionable present in fusion evidence: " + actionable);
        }
    }
}
