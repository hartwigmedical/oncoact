package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.model.Genomic;
import com.hartwig.oncoact.variant.ReportableVariant;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.hartwig.oncoact.patientreporter.algo.wgs.FusionCreator.createFusion;
import static com.hartwig.oncoact.patientreporter.algo.wgs.GainsLossesCreator.createGainsLosses;
import static com.hartwig.oncoact.patientreporter.algo.wgs.HomozygousDisruptionCreator.createHomozygousDisruption;
import static com.hartwig.oncoact.patientreporter.algo.wgs.LohCreator.createLohEventHrd;
import static com.hartwig.oncoact.patientreporter.algo.wgs.LohCreator.createLohEventMSI;
import static com.hartwig.oncoact.patientreporter.algo.wgs.VariantCreator.createObservedVariant;

class GenomicCreator {
    static Genomic createGenomic(
            @NotNull GenomicAnalysis analysis,
            @NotNull Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant) {

        return Genomic.builder()
                .purity(analysis.impliedPurity())
                .averagePloidy(analysis.averageTumorPloidy())
                .variants(createObservedVariant(analysis.reportableVariants(), analysis.hasReliablePurity(), notifyGermlineStatusPerVariant))
                .gainsLosses(createGainsLosses(analysis.gainsAndLosses(), analysis.cnPerChromosome(), analysis.hasReliablePurity()))
                .geneFusions(createFusion(analysis.geneFusions(), analysis.hasReliablePurity()))
                .homozygousDisruptions(createHomozygousDisruption(analysis.homozygousDisruptions()))
                .lohEventsHrd(createLohEventHrd(analysis.suspectGeneCopyNumbersWithLOH(), analysis.hrdStatus()))
                .lohEventsMsi(createLohEventMSI(analysis.suspectGeneCopyNumbersWithLOH(), analysis.microsatelliteStatus()))
//                .geneDisruptions()
//                .viralInsertions()
//                .pharmacogenetics()
//                .hlaAlleles()
//                .profiles()
                .build();
    }


}
