package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleLossOfHeterozygosity;
import com.hartwig.oncoact.copynumber.CopyNumberInterpretation;
import com.hartwig.oncoact.copynumber.PurpleGainLossData;
import com.hartwig.oncoact.copynumber.ReportablePurpleGainLoss;
import com.hartwig.oncoact.protect.EventGenerator;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.util.ListUtil;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CopyNumberEvidence {

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableGene> actionableGenes;

    public CopyNumberEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
            @NotNull final List<ActionableGene> actionableGenes) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.actionableGenes = actionableGenes.stream()
                .filter(x -> x.event() == GeneEvent.INACTIVATION || x.event() == GeneEvent.AMPLIFICATION
                        || x.event() == GeneEvent.OVEREXPRESSION || x.event() == GeneEvent.DELETION
                        || x.event() == GeneEvent.UNDEREXPRESSION || x.event() == GeneEvent.ANY_MUTATION)
                .collect(Collectors.toList());
    }

    @NotNull
    public List<ProtectEvidence> evidence(@NotNull List<PurpleGainLoss> reportableSomaticGainLosses,
            @NotNull List<PurpleGainLoss> allSomaticGainLosses, @Nullable List<PurpleGainLoss> reportableGermlineLosses,
            @Nullable List<PurpleGainLoss> allGermlineLosses, @Nullable List<PurpleLossOfHeterozygosity> germlineLossOfHeterozygosity,
            @Nullable List<PurpleLossOfHeterozygosity> allGermlineLossOfHeterozygosities) {
        List<ProtectEvidence> result = Lists.newArrayList();

        List<PurpleGainLossData> allReportableGainsLosses =
                ListUtil.mergeLists(ReportablePurpleGainLoss.toReportableGainLoss(reportableSomaticGainLosses),
                        ReportablePurpleGainLoss.toReportableGainLoss(reportableGermlineLosses),
                        ReportablePurpleGainLoss.toReportableGainLossLOH(germlineLossOfHeterozygosity));

        for (PurpleGainLossData reportableGainLoss : allReportableGainsLosses) {
            result.addAll(evidence(reportableGainLoss, true));
        }

        List<PurpleGainLossData> allGainsLosses = ListUtil.mergeLists(ReportablePurpleGainLoss.toReportableGainLoss(allSomaticGainLosses),
                ReportablePurpleGainLoss.toReportableGainLoss(allGermlineLosses),
                ReportablePurpleGainLoss.toReportableGainLossLOH(allGermlineLossOfHeterozygosities));

        for (PurpleGainLossData gainLoss : allGainsLosses) {
            if (!ReportablePurpleGainLoss.toReportableGainLoss(reportableSomaticGainLosses).contains(gainLoss)) {
                result.addAll(evidence(gainLoss, false));
            }
        }

        return result;
    }

    @NotNull
    private List<ProtectEvidence> evidence(@NotNull PurpleGainLossData gainLoss, boolean report) {
        List<ProtectEvidence> result = Lists.newArrayList();
        for (ActionableGene actionable : actionableGenes) {
            if (actionable.gene().equals(gainLoss.gene()) && isTypeMatch(actionable, gainLoss)) {
                ProtectEvidence evidence = personalizedEvidenceFactory.somaticEvidence(actionable)
                        .reported(report)
                        .gene(gainLoss.gene())
                        .transcript(gainLoss.transcript())
                        .isCanonical(gainLoss.isCanonical())
                        .event(EventGenerator.gainLossEvent(gainLoss))
                        .eventIsHighDriver(EvidenceDriverLikelihood.interpretGainLoss())
                        .build();
                result.add(evidence);
            }
        }

        return result;
    }

    private static boolean isTypeMatch(@NotNull ActionableGene actionable, @NotNull PurpleGainLossData reportable) {
        switch (actionable.event()) {
            case AMPLIFICATION:
            case OVEREXPRESSION:
                return reportable.interpretation() == CopyNumberInterpretation.FULL_GAIN
                        || reportable.interpretation() == CopyNumberInterpretation.PARTIAL_GAIN;
            case INACTIVATION:
            case DELETION:
            case UNDEREXPRESSION:
            case ANY_MUTATION:
                return reportable.interpretation() == CopyNumberInterpretation.FULL_LOSS
                        || reportable.interpretation() == CopyNumberInterpretation.PARTIAL_LOSS;
            default:
                throw new IllegalStateException(
                        "Actionable event found in copy number evidence that should not exist: " + actionable.event());
        }
    }
}
