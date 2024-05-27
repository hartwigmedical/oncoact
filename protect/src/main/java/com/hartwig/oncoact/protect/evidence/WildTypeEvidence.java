package com.hartwig.oncoact.protect.evidence;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.linx.LinxBreakend;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.wildtype.WildTypeFactory;
import com.hartwig.oncoact.wildtype.WildTypeGene;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
                                          @NotNull Collection<HomozygousDisruption> homozygousDisruptions, @NotNull Collection<LinxBreakend> reportableBreakends,
                                          @NotNull Collection<PurpleQCStatus> purpleQCStatus, @Nullable PatientInformationResponse diagnosticPatientData) {
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
                    evidences.add(evidence(actionable, diagnosticPatientData));
                }
            }
        }
        return evidences;
    }

    @NotNull
    private ProtectEvidence evidence(@NotNull ActionableGene actionable, @Nullable PatientInformationResponse diagnosticPatientData) {
        return personalizedEvidenceFactory.somaticEvidence(actionable, diagnosticPatientData, false)
                .gene(actionable.gene())
                .event(actionable.gene() + " wild type")
                .eventIsHighDriver(EvidenceDriverLikelihood.interpretWildType())
                .build();
    }
}