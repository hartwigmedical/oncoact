package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.model.Genomic;
import com.hartwig.oncoact.variant.ReportableVariant;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.hartwig.oncoact.patientreporter.algo.wgs.FusionCreator.createFusion;
import static com.hartwig.oncoact.patientreporter.algo.wgs.GainsLossesCreator.createGainsLosses;
import static com.hartwig.oncoact.patientreporter.algo.wgs.GeneDisruptionCreator.createGeneDisruption;
import static com.hartwig.oncoact.patientreporter.algo.wgs.GenomicProfilesCreator.createGenomicProfiles;
import static com.hartwig.oncoact.patientreporter.algo.wgs.HlaAllelesCreator.createHlaAlleles;
import static com.hartwig.oncoact.patientreporter.algo.wgs.HomozygousDisruptionCreator.createHomozygousDisruption;
import static com.hartwig.oncoact.patientreporter.algo.wgs.LohCreator.createLohEventHrd;
import static com.hartwig.oncoact.patientreporter.algo.wgs.LohCreator.createLohEventMSI;
import static com.hartwig.oncoact.patientreporter.algo.wgs.PharmacogeneticsCreator.createPharmacogeneticsGenotype;
import static com.hartwig.oncoact.patientreporter.algo.wgs.VariantCreator.createObservedVariant;
import static com.hartwig.oncoact.patientreporter.algo.wgs.ViralInsertionCreator.createViralInsertion;

class GenomicCreator {
    static Genomic createGenomic(
            @NotNull GenomicAnalysis analysis,
            @NotNull Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant,
            @NotNull Map<String, List<PeachGenotype>> pharmacogeneticsGenotypesMap,
            @NotNull HlaAllelesReportingData hlaReportingData) {

        return Genomic.builder()
                .purity(analysis.impliedPurity())
                .averagePloidy(analysis.averageTumorPloidy())
                .variants(createObservedVariant(analysis.reportableVariants(), analysis.hasReliablePurity(), notifyGermlineStatusPerVariant))
                .gainsLosses(createGainsLosses(analysis.gainsAndLosses(), analysis.cnPerChromosome(), analysis.hasReliablePurity()))
                .geneFusions(createFusion(analysis.geneFusions(), analysis.hasReliablePurity()))
                .homozygousDisruptions(createHomozygousDisruption(analysis.homozygousDisruptions()))
                .lohEventsHrd(createLohEventHrd(analysis.suspectGeneCopyNumbersWithLOH(), analysis.hrdStatus()))
                .lohEventsMsi(createLohEventMSI(analysis.suspectGeneCopyNumbersWithLOH(), analysis.microsatelliteStatus()))
                .geneDisruptions(createGeneDisruption(analysis.geneDisruptions(), analysis.hasReliablePurity()))
                .viralInsertions(createViralInsertion(analysis.reportableViruses()))
                .pharmacogenetics(createPharmacogeneticsGenotype(pharmacogeneticsGenotypesMap))
                .hlaAlleles(createHlaAlleles(hlaReportingData, analysis.hasReliablePurity()))
                .profiles(createGenomicProfiles(analysis.hrdValue(), analysis.hrdStatus(), analysis.microsatelliteIndelsPerMb(),
                        analysis.microsatelliteStatus(), analysis.tumorMutationalBurden(), analysis.tumorMutationalBurdenStatus(),
                        analysis.tumorMutationalLoad(), analysis.hasReliablePurity()))
                .build();
    }
}