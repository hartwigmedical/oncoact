package com.hartwig.oncoact.patientreporter.algo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.copynumber.CnPerChromosomeFactory;
import com.hartwig.oncoact.copynumber.RefGenomeCoordinates;
import com.hartwig.oncoact.disruption.GeneDisruption;
import com.hartwig.oncoact.disruption.GeneDisruptionFactory;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaAllelesReportingFactory;
import com.hartwig.oncoact.knownfusion.KnownFusionCache;
import com.hartwig.oncoact.lims.LimsGermlineReportingLevel;
import com.hartwig.oncoact.orange.OrangeRecord;
import com.hartwig.oncoact.orange.OrangeRefGenomeVersion;
import com.hartwig.oncoact.orange.purple.PurpleRecord;
import com.hartwig.oncoact.orange.purple.PurpleVariant;
import com.hartwig.oncoact.patientreporter.actionability.ClinicalTrialFactory;
import com.hartwig.oncoact.patientreporter.actionability.ReportableEvidenceItemFactory;
import com.hartwig.oncoact.patientreporter.algo.orange.BreakendSelector;
import com.hartwig.oncoact.patientreporter.algo.orange.LossOfHeterozygositySelector;
import com.hartwig.oncoact.patientreporter.germline.GermlineReportingModel;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantFactory;
import com.hartwig.oncoact.variant.ReportableVariantSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class GenomicAnalyzer {

    private static final Logger LOGGER = LogManager.getLogger(GenomicAnalyzer.class);

    @NotNull
    private final GermlineReportingModel germlineReportingModel;
    @NotNull
    private final KnownFusionCache knownFusionCache;

    public GenomicAnalyzer(@NotNull final GermlineReportingModel germlineReportingModel, @NotNull final KnownFusionCache knownFusionCache) {
        this.germlineReportingModel = germlineReportingModel;
        this.knownFusionCache = knownFusionCache;
    }

    @NotNull
    public GenomicAnalysis run(@NotNull OrangeRecord orange, @NotNull List<ProtectEvidence> reportableEvidences,
            @NotNull LimsGermlineReportingLevel germlineReportingLevel) {
        List<GeneDisruption> additionalSuspectBreakends =
                GeneDisruptionFactory.convert(BreakendSelector.selectInterestingUnreportedBreakends(orange.linx().allBreakends(),
                        orange.linx().reportableFusions(),
                        knownFusionCache), orange.linx().structuralVariants());

        List<GeneDisruption> reportableGeneDisruptions =
                GeneDisruptionFactory.convert(orange.linx().reportableBreakends(), orange.linx().structuralVariants());
        reportableGeneDisruptions.addAll(additionalSuspectBreakends);

        List<LohGenesReporting> suspectGeneCopyNumbersHRDWithLOH =
                LossOfHeterozygositySelector.selectHRDGenesWithLOH(orange.purple().allSomaticGeneCopyNumbers(), orange.chord().hrStatus());
        LOGGER.info(" Found an additional {} suspect gene copy numbers HRD with LOH", suspectGeneCopyNumbersHRDWithLOH.size());

        List<LohGenesReporting> suspectGeneCopyNumbersMSIWithLOH =
                LossOfHeterozygositySelector.selectMSIGenesWithLOH(orange.purple().allSomaticGeneCopyNumbers(),
                        orange.purple().characteristics().microsatelliteStatus());
        LOGGER.info(" Found an additional {} suspect gene copy numbers MSI with LOH", suspectGeneCopyNumbersMSIWithLOH.size());

        Set<ReportableVariant> reportableGermlineVariants = createReportableGermlineVariants(orange.purple());
        Set<ReportableVariant> reportableSomaticVariants = createReportableSomaticVariants(orange.purple());
        List<ReportableVariant> reportableVariants =
                ReportableVariantFactory.mergeVariantLists(reportableGermlineVariants, reportableSomaticVariants);

        Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant =
                determineNotify(reportableVariants, germlineReportingModel, germlineReportingLevel);

        HlaAllelesReportingData hlaReportingData =
                HlaAllelesReportingFactory.convertToReportData(orange.lilac(), orange.purple().fit().containsTumorCells());

        List<ProtectEvidence> nonTrialsOnLabel = ReportableEvidenceItemFactory.extractNonTrialsOnLabel(reportableEvidences);
        List<ProtectEvidence> trialsOnLabel = ClinicalTrialFactory.extractOnLabelTrials(reportableEvidences);
        List<ProtectEvidence> nonTrialsOffLabel = ReportableEvidenceItemFactory.extractNonTrialsOffLabel(reportableEvidences);

        RefGenomeCoordinates refGenomeCoordinates =
                orange.refGenomeVersion() == OrangeRefGenomeVersion.V37 ? RefGenomeCoordinates.COORDS_37 : RefGenomeCoordinates.COORDS_38;
        List<CnPerChromosomeArmData> copyNumberPerChromosome =
                CnPerChromosomeFactory.extractCnPerChromosomeArm(orange.purple().allSomaticCopyNumbers(), refGenomeCoordinates);

        return ImmutableGenomicAnalysis.builder()
                .purpleQCStatus(orange.purple().fit().qcStatus())
                .impliedPurity(orange.purple().fit().purity())
                .hasReliablePurity(orange.purple().fit().containsTumorCells())
                .hasReliableQuality(orange.purple().fit().hasSufficientQuality())
                .averageTumorPloidy(orange.purple().fit().ploidy())
                .tumorSpecificEvidence(nonTrialsOnLabel)
                .clinicalTrials(trialsOnLabel)
                .offLabelEvidence(nonTrialsOffLabel)
                .reportableVariants(reportableVariants)
                .notifyGermlineStatusPerVariant(notifyGermlineStatusPerVariant)
                .microsatelliteIndelsPerMb(orange.purple().characteristics().microsatelliteIndelsPerMb())
                .microsatelliteStatus(orange.purple().characteristics().microsatelliteStatus())
                .tumorMutationalLoad(orange.purple().characteristics().tumorMutationalLoad())
                .tumorMutationalLoadStatus(orange.purple().characteristics().tumorMutationalLoadStatus())
                .tumorMutationalBurden(orange.purple().characteristics().tumorMutationalBurdenPerMb())
                .hrdValue(orange.chord().hrdValue())
                .hrdStatus(orange.chord().hrStatus())
                .gainsAndLosses(orange.purple().reportableSomaticGainsLosses())
                .cnPerChromosome(copyNumberPerChromosome)
                .geneFusions(orange.linx().reportableFusions())
                .geneDisruptions(reportableGeneDisruptions)
                .homozygousDisruptions(orange.linx().homozygousDisruptions())
                .reportableViruses(orange.virusInterpreter().reportableViruses())
                .hlaAlleles(hlaReportingData)
                .suspectGeneCopyNumbersHRDWithLOH(suspectGeneCopyNumbersHRDWithLOH)
                .suspectGeneCopyNumbersMSIWithLOH(suspectGeneCopyNumbersMSIWithLOH)
                .build();
    }

    @NotNull
    private static Set<ReportableVariant> createReportableSomaticVariants(@NotNull PurpleRecord purple) {
        return ReportableVariantFactory.toReportableSomaticVariants(purple.reportableSomaticVariants(), purple.somaticDrivers());
    }

    @NotNull
    private static Set<ReportableVariant> createReportableGermlineVariants(@NotNull PurpleRecord purple) {
        Set<PurpleVariant> reportableGermlineVariants = purple.reportableGermlineVariants();
        if (reportableGermlineVariants == null) {
            return Sets.newHashSet();
        }

        return ReportableVariantFactory.toReportableGermlineVariants(reportableGermlineVariants, purple.germlineDrivers());
    }

    @NotNull
    private static Map<ReportableVariant, Boolean> determineNotify(@NotNull List<ReportableVariant> reportableVariants,
            @NotNull GermlineReportingModel germlineReportingModel, @NotNull LimsGermlineReportingLevel germlineReportingLevel) {
        Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant = Maps.newHashMap();

        Set<String> germlineGenesWithIndependentHits = Sets.newHashSet();
        for (ReportableVariant variant : reportableVariants) {
            if (variant.source() == ReportableVariantSource.GERMLINE && hasOtherGermlineVariantWithDifferentPhaseSet(reportableVariants,
                    variant)) {
                germlineGenesWithIndependentHits.add(variant.gene());
            }
        }

        for (ReportableVariant variant : reportableVariants) {
            boolean notify = false;
            if (variant.source() == ReportableVariantSource.GERMLINE) {
                notify = germlineReportingModel.notifyGermlineVariant(variant, germlineReportingLevel, germlineGenesWithIndependentHits);
            }
            notifyGermlineStatusPerVariant.put(variant, notify);
        }

        return notifyGermlineStatusPerVariant;
    }

    @VisibleForTesting
    static boolean hasOtherGermlineVariantWithDifferentPhaseSet(@NotNull List<ReportableVariant> variants,
            @NotNull ReportableVariant variantToCompareWith) {
        Integer phaseSetToCompareWith = variantToCompareWith.localPhaseSet();
        for (ReportableVariant variant : variants) {
            if (!variant.equals(variantToCompareWith) && variant.gene().equals(variantToCompareWith.gene())
                    && variant.source() == ReportableVariantSource.GERMLINE && (phaseSetToCompareWith == null
                    || !phaseSetToCompareWith.equals(variant.localPhaseSet()))) {
                return true;
            }
        }

        return false;
    }
}
