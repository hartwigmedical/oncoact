package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.common.drivercatalog.panel.DriverGene;
import com.hartwig.oncoact.common.linx.GeneDisruption;
import com.hartwig.oncoact.common.linx.HomozygousDisruption;
import com.hartwig.oncoact.common.linx.LinxFusion;
import com.hartwig.oncoact.common.protect.ProtectEvidence;
import com.hartwig.oncoact.common.purple.PurpleQCStatus;
import com.hartwig.oncoact.common.purple.loader.GainLoss;
import com.hartwig.oncoact.common.variant.ReportableVariant;
import com.hartwig.oncoact.common.wildtype.WildTypeFactory;
import com.hartwig.oncoact.common.wildtype.WildTypeGene;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;

import org.jetbrains.annotations.NotNull;

public class WildTypeEvidence {

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableGene> actionableGenes;
    @NotNull
    private final List<DriverGene> driverGenes;

    public WildTypeEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
            @NotNull final List<ActionableGene> actionableGenes, @NotNull final List<DriverGene> driverGenes) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.actionableGenes = actionableGenes.stream().filter(x -> x.event() == GeneEvent.WILD_TYPE).collect(Collectors.toList());
        this.driverGenes = driverGenes;
    }

    public List<ProtectEvidence> evidence(@NotNull List<ReportableVariant> reportableGermlineVariant,
            @NotNull List<ReportableVariant> reportableSomaticVariant, @NotNull List<GainLoss> reportableSomaticGainsLosses,
            @NotNull List<LinxFusion> reportableFusions, @NotNull List<HomozygousDisruption> homozygousDisruptions,
            @NotNull List<GeneDisruption> geneDisruptions, @NotNull Set<PurpleQCStatus> purpleQCStatus) {
        List<ProtectEvidence> evidences = Lists.newArrayList();
        List<WildTypeGene> wildTypeGenes = WildTypeFactory.determineWildTypeGenes(reportableGermlineVariant,
                reportableSomaticVariant,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                geneDisruptions,
                driverGenes);

        List<WildTypeGene> wildTypeGenesFilter = WildTypeFactory.filterQCWildTypes(purpleQCStatus, wildTypeGenes);

        for (ActionableGene actionable : actionableGenes) {
            for (WildTypeGene wildType : wildTypeGenesFilter) {
                if (wildType.gene().equals(actionable.gene())) {
                    evidences.add(evidence(actionable));
                }
            }
        }
        return evidences;
    }

    @NotNull
    private ProtectEvidence evidence(@NotNull ActionableGene actionable) {
        return personalizedEvidenceFactory.somaticEvidence(actionable)
                .reported(false)
                .gene(actionable.gene())
                .event(actionable.gene() + " wild type")
                .eventIsHighDriver(EvidenceDriverLikelihood.interpretWildType())
                .build();
    }
}