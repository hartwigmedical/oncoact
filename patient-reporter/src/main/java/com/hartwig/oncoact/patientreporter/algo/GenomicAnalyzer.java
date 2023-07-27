package com.hartwig.oncoact.patientreporter.algo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.hmftools.datamodel.orange.OrangeRefGenomeVersion;
import com.hartwig.hmftools.datamodel.purple.PurpleGeneCopyNumber;
import com.hartwig.hmftools.datamodel.purple.PurpleRecord;
import com.hartwig.hmftools.datamodel.purple.PurpleVariant;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.copynumber.CnPerChromosomeFactory;
import com.hartwig.oncoact.copynumber.RefGenomeCoordinates;
import com.hartwig.oncoact.disruption.GeneDisruption;
import com.hartwig.oncoact.disruption.GeneDisruptionFactory;
import com.hartwig.oncoact.patientreporter.actionability.ClinicalTrialFactory;
import com.hartwig.oncoact.patientreporter.actionability.ReportableEvidenceItemFactory;
import com.hartwig.oncoact.patientreporter.clinicaltransript.ClinicalTranscriptsModel;
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
    private final ClinicalTranscriptsModel clinicalTranscriptsModel;

    public GenomicAnalyzer(@NotNull final GermlineReportingModel germlineReportingModel,
                           @NotNull final ClinicalTranscriptsModel clinicalTranscriptsModel) {
        this.germlineReportingModel = germlineReportingModel;
        this.clinicalTranscriptsModel = clinicalTranscriptsModel;
    }

    @NotNull
    public GenomicAnalysis run(@NotNull OrangeRecord orange, @NotNull List<ProtectEvidence> reportableEvidences,
                               boolean germlineReportingLevel) {
        List<GeneDisruption> additionalSuspectBreakends =
                GeneDisruptionFactory.convert(orange.linx().additionalSuspectSomaticBreakends(), orange.linx().allSomaticStructuralVariants());

        List<GeneDisruption> reportableGeneDisruptions =
                GeneDisruptionFactory.convert(orange.linx().reportableSomaticBreakends(), orange.linx().allSomaticStructuralVariants());
        reportableGeneDisruptions.addAll(additionalSuspectBreakends);

        List<PurpleGeneCopyNumber> suspectGeneCopyNumbersWithLOH = orange.purple().suspectGeneCopyNumbersWithLOH();
        LOGGER.info(" Found an additional {} suspect gene copy numbers with LOH", suspectGeneCopyNumbersWithLOH.size());

        List<InterpretPurpleGeneCopyNumbers> interpretSuspectGeneCopyNumbersWithLOH = InterpretPurpleGeneCopyNumbersFactory.convert(suspectGeneCopyNumbersWithLOH);

        Set<ReportableVariant> reportableGermlineVariants = createReportableGermlineVariants(orange.purple());
        Set<ReportableVariant> reportableSomaticVariants = createReportableSomaticVariants(orange.purple());
        List<ReportableVariant> reportableVariants =
                ReportableVariantFactory.mergeVariantLists(reportableGermlineVariants, reportableSomaticVariants);

        Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant =
                determineNotify(reportableVariants, germlineReportingModel, germlineReportingLevel);

        List<ProtectEvidence> nonTrialsOnLabel = ReportableEvidenceItemFactory.extractNonTrialsOnLabel(reportableEvidences);
        List<ProtectEvidence> trialsOnLabel = ClinicalTrialFactory.extractOnLabelTrials(reportableEvidences);
        List<ProtectEvidence> nonTrialsOffLabel = ReportableEvidenceItemFactory.extractNonTrialsOffLabel(reportableEvidences);

        RefGenomeCoordinates refGenomeCoordinates =
                orange.refGenomeVersion() == OrangeRefGenomeVersion.V37 ? RefGenomeCoordinates.COORDS_37 : RefGenomeCoordinates.COORDS_38;
        List<CnPerChromosomeArmData> copyNumberPerChromosome =
                CnPerChromosomeFactory.extractCnPerChromosomeArm(orange.purple().allSomaticCopyNumbers(), refGenomeCoordinates);

        return ImmutableGenomicAnalysis.builder()
                .purpleQCStatus(orange.purple().fit().qc().status())
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
                .geneFusions(orange.linx().reportableSomaticFusions())
                .geneDisruptions(reportableGeneDisruptions)
                .homozygousDisruptions(orange.linx().somaticHomozygousDisruptions())
                .reportableViruses(orange.virusInterpreter().reportableViruses())
                .suspectGeneCopyNumbersWithLOH(interpretSuspectGeneCopyNumbersWithLOH)
                .build();
    }

    @NotNull
    private static Set<ReportableVariant> createReportableSomaticVariants(@NotNull PurpleRecord purple) {
        return ReportableVariantFactory.toReportableSomaticVariants(purple.reportableSomaticVariants(), purple.somaticDrivers());
    }

    @NotNull
    private static Set<ReportableVariant> createReportableGermlineVariants(@NotNull PurpleRecord purple) {
        Collection<PurpleVariant> reportableGermlineVariants = purple.reportableGermlineVariants();
        if (reportableGermlineVariants == null) {
            return Sets.newHashSet();
        }

        return ReportableVariantFactory.toReportableGermlineVariants(reportableGermlineVariants, purple.germlineDrivers());
    }

    @NotNull
    private static Map<ReportableVariant, Boolean> determineNotify(@NotNull List<ReportableVariant> reportableVariants,
                                                                   @NotNull GermlineReportingModel germlineReportingModel,
                                                                   boolean germlineReportingLevel) {
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
