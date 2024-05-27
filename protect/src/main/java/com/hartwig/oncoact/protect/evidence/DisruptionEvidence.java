package com.hartwig.oncoact.protect.evidence;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.util.ListUtil;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

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
                .filter(x -> x.event() == GeneEvent.ANY_MUTATION || x.event() == GeneEvent.INACTIVATION || x.event() == GeneEvent.DELETION
                        || x.event() == GeneEvent.UNDEREXPRESSION)
                .collect(Collectors.toList());
    }

    @NotNull
    public List<ProtectEvidence> evidence(@NotNull List<HomozygousDisruption> somaticHomozygousDisruptions,
                                          @Nullable List<HomozygousDisruption> germlineHomozygousDisruptions, @Nullable PatientInformationResponse diagnosticPatientData) {
        List<ProtectEvidence> result = Lists.newArrayList();
        for (HomozygousDisruption homozygousDisruption : ListUtil.mergeListsDistinct(somaticHomozygousDisruptions,
                germlineHomozygousDisruptions)) {
            result.addAll(evidence(homozygousDisruption, diagnosticPatientData));
        }
        return result;
    }

    @NotNull
    private List<ProtectEvidence> evidence(@NotNull HomozygousDisruption homozygousDisruption, @Nullable PatientInformationResponse diagnosticPatientData) {
        List<ProtectEvidence> result = Lists.newArrayList();
        for (ActionableGene actionable : actionableGenes) {
            if (actionable.gene().equals(homozygousDisruption.gene())) {
                ProtectEvidence evidence = personalizedEvidenceFactory.somaticReportableEvidence(actionable, diagnosticPatientData, true)
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
