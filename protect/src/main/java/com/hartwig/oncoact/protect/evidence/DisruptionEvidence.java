package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.orange.linx.LinxHomozygousDisruption;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;

import org.jetbrains.annotations.NotNull;

public class DisruptionEvidence {

    static final String HOMOZYGOUS_DISRUPTION_EVENT = "homozygous disruption";

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableGene> actionableGenes;

    public DisruptionEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
            @NotNull final List<ActionableGene> actionableGenes) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.actionableGenes = actionableGenes.stream()
                .filter(x -> x.event() == GeneEvent.ANY_MUTATION || x.event() == GeneEvent.INACTIVATION
                        || x.event() == GeneEvent.DELETION || x.event() == GeneEvent.UNDEREXPRESSION)
                .collect(Collectors.toList());
    }

    @NotNull
    public List<ProtectEvidence> evidence(@NotNull Set<LinxHomozygousDisruption> homozygousDisruptions) {
        List<ProtectEvidence> result = Lists.newArrayList();
        for (LinxHomozygousDisruption homozygousDisruption : homozygousDisruptions) {
            result.addAll(evidence(homozygousDisruption));
        }
        return result;
    }

    @NotNull
    private List<ProtectEvidence> evidence(@NotNull LinxHomozygousDisruption homozygousDisruption) {
        List<ProtectEvidence> result = Lists.newArrayList();
        for (ActionableGene actionable : actionableGenes) {
            if (actionable.gene().equals(homozygousDisruption.gene())) {
                ProtectEvidence evidence = personalizedEvidenceFactory.somaticReportableEvidence(actionable)
                        .gene(homozygousDisruption.gene())
                        .transcript(homozygousDisruption.transcript())
                        .isCanonical(homozygousDisruption.isCanonical())
                        .event(HOMOZYGOUS_DISRUPTION_EVENT)
                        .eventIsHighDriver(true)
                        .build();
                result.add(evidence);
            }
        }

        return result;
    }
}
