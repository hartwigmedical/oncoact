package com.hartwig.oncoact.patientreporter.algo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.linx.LinxBreakend;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.linx.LinxSvAnnotation;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.hmftools.datamodel.orange.OrangeRefGenomeVersion;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleGeneCopyNumber;
import com.hartwig.hmftools.datamodel.purple.PurpleTumorMutationalStatus;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.copynumber.CnPerChromosomeFactory;
import com.hartwig.oncoact.copynumber.RefGenomeCoordinates;
import com.hartwig.oncoact.disruption.GeneDisruption;
import com.hartwig.oncoact.disruption.GeneDisruptionFactory;
import com.hartwig.oncoact.patientreporter.actionability.ClinicalTrialFactory;
import com.hartwig.oncoact.patientreporter.actionability.ReportableEvidenceItemFactory;
import com.hartwig.oncoact.patientreporter.cfreport.data.MutationalBurden;
import com.hartwig.oncoact.patientreporter.germline.GermlineReportingModel;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.util.ListUtil;
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

    public GenomicAnalyzer(@NotNull final GermlineReportingModel germlineReportingModel) {
        this.germlineReportingModel = germlineReportingModel;
    }

    @NotNull
    public GenomicAnalysis run(@NotNull OrangeRecord orange, @NotNull List<ProtectEvidence> reportableEvidences,
            boolean germlineReportingLevel) {

        // variants
        Set<ReportableVariant> reportableGermlineVariants = ReportableVariantFactory.createReportableGermlineVariants(orange.purple());
        Set<ReportableVariant> reportableSomaticVariants = ReportableVariantFactory.createReportableSomaticVariants(orange.purple());
        List<ReportableVariant> reportableVariants =
                ReportableVariantFactory.mergeVariantLists(reportableGermlineVariants, reportableSomaticVariants);
        Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant =
                determineNotify(reportableVariants, germlineReportingModel, germlineReportingLevel);

        // gains & losses
        List<PurpleGainLoss> somaticGainsLosses = orange.purple().reportableSomaticGainsLosses();
        List<PurpleGainLoss> germlineLosses = orange.purple().reportableGermlineFullLosses();
        List<PurpleGainLoss> reportableGainsLosses = ListUtil.mergeLists(somaticGainsLosses, germlineLosses);

        // Determine chromosome copy number arm
        RefGenomeCoordinates refGenomeCoordinates =
                orange.refGenomeVersion() == OrangeRefGenomeVersion.V37 ? RefGenomeCoordinates.COORDS_37 : RefGenomeCoordinates.COORDS_38;
        List<CnPerChromosomeArmData> copyNumberPerChromosome =
                CnPerChromosomeFactory.extractCnPerChromosomeArm(orange.purple().allSomaticCopyNumbers(), refGenomeCoordinates);

        // fusions
        List<LinxFusion> geneFusions = orange.linx().reportableSomaticFusions();

        // homozygous disruptions
        List<HomozygousDisruption> somaticHomozygousDisruptions = orange.linx().somaticHomozygousDisruptions();
        List<HomozygousDisruption> germlineHomozygousDisruptions = orange.linx().germlineHomozygousDisruptions();
        List<HomozygousDisruption> homozygousDisruptions = ListUtil.mergeLists(somaticHomozygousDisruptions, germlineHomozygousDisruptions);

        //disruptions
        List<GeneDisruption> additionalSuspectBreakends = GeneDisruptionFactory.convert(orange.linx().additionalSuspectSomaticBreakends(),
                orange.linx().allSomaticStructuralVariants());

        List<GeneDisruption> reportableSomaticGeneDisruptions =
                GeneDisruptionFactory.convert(orange.linx().reportableSomaticBreakends(), orange.linx().allSomaticStructuralVariants());

        List<GeneDisruption> reportableGermlineGeneDisruptions = Lists.newArrayList();
        List<LinxBreakend> reportableGermlineBreakends = orange.linx().reportableGermlineBreakends();
        List<LinxSvAnnotation> allGermlineStructuralVariants = orange.linx().allGermlineStructuralVariants();
        if (reportableGermlineBreakends != null && allGermlineStructuralVariants != null) {
            reportableGermlineGeneDisruptions = GeneDisruptionFactory.convert(reportableGermlineBreakends, allGermlineStructuralVariants);
        }

        List<GeneDisruption> geneDisruptions = ListUtil.mergeLists(reportableSomaticGeneDisruptions, reportableGermlineGeneDisruptions);
        geneDisruptions.addAll(additionalSuspectBreakends);

        List<PurpleGeneCopyNumber> suspectGeneCopyNumbersWithLOH = orange.purple().suspectGeneCopyNumbersWithLOH();
        LOGGER.info(" Found an additional {} suspect gene copy numbers with LOH", suspectGeneCopyNumbersWithLOH.size());

        List<InterpretPurpleGeneCopyNumbers> interpretSuspectGeneCopyNumbersWithLOH =
                InterpretPurpleGeneCopyNumbersFactory.convert(suspectGeneCopyNumbersWithLOH);

        List<ProtectEvidence> nonTrialsOnLabel = ReportableEvidenceItemFactory.extractNonTrialsOnLabel(reportableEvidences);
        List<ProtectEvidence> trialsOnLabel = ClinicalTrialFactory.extractOnLabelTrials(reportableEvidences);
        List<ProtectEvidence> nonTrialsOffLabel = ReportableEvidenceItemFactory.extractNonTrialsOffLabel(reportableEvidences);

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
                .tumorMutationalBurden(orange.purple().characteristics().tumorMutationalBurdenPerMb())
                .tumorMutationalBurdenStatus(orange.purple().characteristics().tumorMutationalBurdenPerMb() >= MutationalBurden.THRESHOLD
                        ? PurpleTumorMutationalStatus.HIGH
                        : PurpleTumorMutationalStatus.LOW)
                .hrdValue(orange.chord().hrdValue())
                .hrdStatus(orange.chord().hrStatus())
                .gainsAndLosses(reportableGainsLosses)
                .cnPerChromosome(copyNumberPerChromosome)
                .geneFusions(geneFusions)
                .geneDisruptions(geneDisruptions)
                .homozygousDisruptions(homozygousDisruptions)
                .reportableViruses(orange.virusInterpreter().reportableViruses())
                .suspectGeneCopyNumbersWithLOH(interpretSuspectGeneCopyNumbersWithLOH)
                .build();
    }

    @NotNull
    private static Map<ReportableVariant, Boolean> determineNotify(@NotNull List<ReportableVariant> reportableVariants,
            @NotNull GermlineReportingModel germlineReportingModel, boolean germlineReportingLevel) {
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
