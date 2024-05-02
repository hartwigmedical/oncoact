package com.hartwig.oncoact.protect.evidence;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.linx.LinxBreakend;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.linx.LinxHomozygousDisruption;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.wildtype.WildTypeFactory;
import com.hartwig.oncoact.wildtype.WildTypeGene;
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

    public List<ProtectEvidence> evidence(@NotNull Collection<ReportableVariant> reportableGermlineVariants,
            @NotNull Collection<ReportableVariant> reportableSomaticVariants,
            @NotNull Collection<PurpleGainLoss> reportableSomaticGainsLosses, @NotNull Collection<LinxFusion> reportableFusions,
            @NotNull Collection<LinxHomozygousDisruption> homozygousDisruptions, @NotNull Collection<LinxBreakend> reportableBreakends,
            @NotNull Collection<PurpleQCStatus> purpleQCStatus) {
        List<ProtectEvidence> evidences = Lists.newArrayList();
        List<WildTypeGene> wildTypeGenes = WildTypeFactory.determineWildTypeGenes(reportableGermlineVariants,
                reportableSomaticVariants,
                reportableSomaticGainsLosses,
                reportableFusions,
                homozygousDisruptions,
                reportableBreakends,
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