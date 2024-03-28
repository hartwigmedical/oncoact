package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.purple.CopyNumberInterpretation;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.oncoact.patientreporter.algo.CurationFunctions;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.model.GenomicAlterations;
import com.hartwig.oncoact.patientreporter.model.HomologousRecombinationDeficiencyStatus;
import com.hartwig.oncoact.patientreporter.model.MicrosatelliteStatus;
import com.hartwig.oncoact.patientreporter.util.Genes;
import com.hartwig.oncoact.util.Formats;
import com.hartwig.oncoact.variant.DriverInterpretation;
import com.hartwig.oncoact.variant.ReportableVariant;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.hartwig.oncoact.patientreporter.algo.wgs.HomologousRecombinationDeficiencyCreator.getHomologousRecombinationDeficiencyStatus;
import static com.hartwig.oncoact.patientreporter.algo.wgs.MicrosatelliteCreator.getMicrosatalliteStatus;

class GenomicAlterationsInCancerGenesCreator {

    static GenomicAlterations createGenomicAlterationsInCancerGenesCreator(
            @NotNull GenomicAnalysis analysis
    ) {
        return GenomicAlterations.builder()
                .genesWithDriverMutation(createGenesWithDriver(analysis))
                .amplifiedGenes(createAmplifiedGenes(analysis))
                .deletedGenes(createDeletedGenes(analysis))
                .homozygouslyDisruptedGenes(createHomozygousDisruptedGenes(analysis))
                .geneFusions(createGeneFusions(analysis))
                .potentialHrdGenes(createPotentialHRDGenes(analysis))
                .potentialMsiGenes(createPotentialMSIGenes(analysis))
                .build();
    }

    private static String createGenesWithDriver(GenomicAnalysis analysis) {
        Set<String> genes = Sets.newTreeSet();
        for (ReportableVariant variant : analysis.reportableVariants()) {
            if (DriverInterpretation.interpret(variant.driverLikelihood()) == DriverInterpretation.HIGH) {
                genes.add(CurationFunctions.curateGeneNamePdf(variant.gene()));
            }
        }
        return genes.size() > 0 ? String.join(", ", genes) : Formats.NONE_STRING;
    }

    private static String createAmplifiedGenes(GenomicAnalysis analysis) {
        Set<String> genes = Sets.newTreeSet();
        for (PurpleGainLoss gainLoss : analysis.gainsAndLosses()) {
            if (gainLoss.interpretation() == CopyNumberInterpretation.FULL_GAIN
                    || gainLoss.interpretation() == CopyNumberInterpretation.PARTIAL_GAIN) {
                genes.add(CurationFunctions.curateGeneNamePdf(gainLoss.gene()));
            }
        }
        return genes.size() > 0 ? String.join(", ", genes) : Formats.NONE_STRING;
    }

    private static String createDeletedGenes(GenomicAnalysis analysis) {
        Set<String> genes = Sets.newTreeSet();
        for (PurpleGainLoss gainLoss : analysis.gainsAndLosses()) {
            if (gainLoss.interpretation() == CopyNumberInterpretation.FULL_LOSS
                    || gainLoss.interpretation() == CopyNumberInterpretation.PARTIAL_LOSS) {
                genes.add(CurationFunctions.curateGeneNamePdf(gainLoss.gene()));
            }
        }
        return genes.size() > 0 ? String.join(", ", genes) : Formats.NONE_STRING;
    }

    private static String createHomozygousDisruptedGenes(GenomicAnalysis analysis) {
        Set<String> genes = Sets.newTreeSet();
        for (HomozygousDisruption disruption : analysis.homozygousDisruptions()) {
            genes.add(CurationFunctions.curateGeneNamePdf(disruption.gene()));
        }
        return genes.size() > 0 ? String.join(", ", genes) : Formats.NONE_STRING;
    }

    private static String createGeneFusions(GenomicAnalysis analysis) {
        Set<String> genes = Sets.newTreeSet();
        for (LinxFusion fusion : analysis.geneFusions()) {
            genes.add(CurationFunctions.curateGeneNamePdf(fusion.geneStart()) + " - " + CurationFunctions.curateGeneNamePdf(fusion.geneEnd()));
        }
        return genes.size() > 0 ? String.join(", ", genes) : Formats.NONE_STRING;
    }

    private static String createPotentialHRDGenes(GenomicAnalysis analysis) {
        boolean reliablePurity = analysis.hasReliablePurity();
        HomologousRecombinationDeficiencyStatus hrdStatus = reliablePurity ?
                getHomologousRecombinationDeficiencyStatus(analysis.hrdStatus()) : HomologousRecombinationDeficiencyStatus.UNKNOWN;

        Set<String> genesDisplay = Sets.newTreeSet();
        if (hrdStatus == HomologousRecombinationDeficiencyStatus.HR_DEFICIENT) {
            genesDisplay = createPotentialGenes(analysis, Genes.HRD_GENES);
        }
        return genesDisplay.size() > 0 ? String.join(", ", genesDisplay) : Formats.NONE_STRING;
    }

    private static String createPotentialMSIGenes(GenomicAnalysis analysis) {
        boolean reliablePurity = analysis.hasReliablePurity();
        MicrosatelliteStatus msiStatus = reliablePurity ?
                getMicrosatalliteStatus(analysis.microsatelliteStatus()) : MicrosatelliteStatus.UNKNOWN;

        Set<String> genesDisplay = Sets.newTreeSet();
        if (msiStatus == MicrosatelliteStatus.MSI) {
            genesDisplay = createPotentialGenes(analysis, Genes.MSI_GENES);
        }
        return genesDisplay.size() > 0 ? String.join(", ", genesDisplay) : Formats.NONE_STRING;
    }

    private static Set<String> createPotentialGenes(GenomicAnalysis analysis, Set<String> signatureGenes) {
        Set<String> genesDisplay = Sets.newTreeSet();
        for (ReportableVariant variant : analysis.reportableVariants()) {
            if (signatureGenes.contains(variant.gene())) {
                genesDisplay.add(CurationFunctions.curateGeneNamePdf(variant.gene()));
            }
        }

        for (PurpleGainLoss gainLoss : analysis.gainsAndLosses()) {
            if (signatureGenes.contains(gainLoss.gene()) && (gainLoss.interpretation() == CopyNumberInterpretation.PARTIAL_LOSS
                    || gainLoss.interpretation() == CopyNumberInterpretation.FULL_LOSS)) {
                genesDisplay.add(CurationFunctions.curateGeneNamePdf(gainLoss.gene()));
            }
        }

        for (HomozygousDisruption homozygousDisruption : analysis.homozygousDisruptions()) {
            if (signatureGenes.contains(homozygousDisruption.gene())) {
                genesDisplay.add(CurationFunctions.curateGeneNamePdf(homozygousDisruption.gene()));
            }
        }
        return genesDisplay;
    }
}