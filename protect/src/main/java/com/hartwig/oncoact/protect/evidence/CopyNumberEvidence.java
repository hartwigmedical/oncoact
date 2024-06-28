package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.CopyNumberInterpretation;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleLossOfHeterozygosity;
import com.hartwig.oncoact.copynumber.ReportablePurpleGainLoss;
import com.hartwig.oncoact.protect.EventGenerator;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.util.Genes;
import com.hartwig.oncoact.util.ListUtil;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CopyNumberEvidence {
    private static final Logger LOGGER = LogManager.getLogger(CopyNumberEvidence.class);

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableGene> actionableGenes;

    public CopyNumberEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
            @NotNull final List<ActionableGene> actionableGenes) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.actionableGenes = actionableGenes.stream()
                .filter(x -> x.event() == GeneEvent.INACTIVATION || x.event() == GeneEvent.AMPLIFICATION
                        || x.event() == GeneEvent.OVEREXPRESSION || x.event() == GeneEvent.PRESENCE_OF_PROTEIN
                        || x.event() == GeneEvent.DELETION || x.event() == GeneEvent.UNDEREXPRESSION
                        || x.event() == GeneEvent.ABSENCE_OF_PROTEIN || x.event() == GeneEvent.ANY_MUTATION)
                .collect(Collectors.toList());
    }

    @NotNull
    public List<ProtectEvidence> evidence(@NotNull List<PurpleGainLoss> reportableSomaticGainLosses,
            @NotNull List<PurpleGainLoss> allSomaticGainLosses, @Nullable List<PurpleGainLoss> reportableGermlineLosses,
            @Nullable List<PurpleGainLoss> allGermlineLosses, @Nullable List<PurpleLossOfHeterozygosity> reportableGermlineLOHs,
            @Nullable List<PurpleLossOfHeterozygosity> allGermlineLossOfHeterozygosities,
            @Nullable PatientInformationResponse diagnosticPatientData) {
        List<ProtectEvidence> result = Lists.newArrayList();

        List<PurpleGainLoss> allReportableGainsLosses = ListUtil.mergeListsDistinct(reportableSomaticGainLosses,
                reportableGermlineLosses,
                ReportablePurpleGainLoss.toReportableGainLossLOH(reportableGermlineLOHs));

        for (PurpleGainLoss reportableGainLoss : allReportableGainsLosses) {
            result.addAll(evidence(reportableGainLoss, true, diagnosticPatientData));
        }

        List<PurpleGainLoss> allGainsLosses = ListUtil.mergeListsDistinct(allSomaticGainLosses,
                allGermlineLosses,
                ReportablePurpleGainLoss.toReportableGainLossLOH(allGermlineLossOfHeterozygosities));

        for (PurpleGainLoss gainLoss : allGainsLosses) {
            if (!reportableSomaticGainLosses.contains(gainLoss)) {
                result.addAll(evidence(gainLoss, false, diagnosticPatientData));
            }
        }
        return result;
    }

    @NotNull
    private List<ProtectEvidence> evidence(@NotNull PurpleGainLoss gainLoss, boolean report,
            @Nullable PatientInformationResponse diagnosticPatientData) {
        List<ProtectEvidence> result = Lists.newArrayList();
        for (ActionableGene actionable : actionableGenes) {
            if (actionable.gene().equals(gainLoss.gene()) && isTypeMatch(actionable, gainLoss)) {
                boolean reportInterpretation;
                EvidenceType type = PersonalizedEvidenceFactory.determineEvidenceType(actionable, null);
                if (type.equals(EvidenceType.ABSENCE_OF_PROTEIN)) {
                    reportInterpretation = Genes.MSI_GENES.contains(actionable.gene());
                } else if (type.equals(EvidenceType.PRESENCE_OF_PROTEIN)) {
                    reportInterpretation = false;
                } else {
                    reportInterpretation = report;
                }
                ProtectEvidence evidence =
                        personalizedEvidenceFactory.somaticEvidence(actionable, diagnosticPatientData, reportInterpretation)
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

    private static boolean isTypeMatch(@NotNull ActionableGene actionable, @NotNull PurpleGainLoss reportable) {
        switch (actionable.event()) {
            case AMPLIFICATION:
            case OVEREXPRESSION:
            case PRESENCE_OF_PROTEIN:
                return reportable.interpretation() == CopyNumberInterpretation.FULL_GAIN
                        || reportable.interpretation() == CopyNumberInterpretation.PARTIAL_GAIN;
            case INACTIVATION:
            case DELETION:
            case ABSENCE_OF_PROTEIN:
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
