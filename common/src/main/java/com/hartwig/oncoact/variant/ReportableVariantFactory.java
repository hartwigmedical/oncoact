package com.hartwig.oncoact.variant;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.purple.ImmutablePurpleVariant;
import com.hartwig.hmftools.datamodel.purple.PurpleDriver;
import com.hartwig.hmftools.datamodel.purple.PurpleDriverType;
import com.hartwig.hmftools.datamodel.purple.PurpleRecord;
import com.hartwig.hmftools.datamodel.purple.PurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.PurpleVariant;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;
import com.hartwig.oncoact.clinicaltransript.ClinicalTranscriptsModel;
import com.hartwig.oncoact.protect.EventGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ReportableVariantFactory {

    private static final Logger LOGGER = LogManager.getLogger(ReportableVariantFactory.class);
    private static final Set<PurpleDriverType> MUTATION_DRIVER_TYPES =
            Sets.newHashSet(PurpleDriverType.MUTATION, PurpleDriverType.GERMLINE_MUTATION);

    private ReportableVariantFactory() {
    }

    @NotNull
    public static Set<ReportableVariant> createReportableSomaticVariants(@NotNull PurpleRecord purple,
            @NotNull ClinicalTranscriptsModel clinicalTranscriptsModel) {
        return ReportableVariantFactory.toReportableSomaticVariants(purple.reportableSomaticVariants(),
                purple.somaticDrivers(),
                clinicalTranscriptsModel);
    }

    @NotNull
    public static Set<ReportableVariant> createReportableGermlineVariants(@NotNull PurpleRecord purple,
            @NotNull ClinicalTranscriptsModel clinicalTranscriptsModel) {
        Collection<PurpleVariant> reportableGermlineVariants = purple.reportableGermlineVariants();
        Collection<PurpleDriver> germlineDrivers = purple.germlineDrivers();

        if (reportableGermlineVariants == null || germlineDrivers == null) {
            return Sets.newHashSet();
        }

        return ReportableVariantFactory.toReportableGermlineVariants(reportableGermlineVariants, germlineDrivers, clinicalTranscriptsModel);
    }

    @NotNull
    public static Set<ReportableVariant> toReportableGermlineVariants(@NotNull Collection<PurpleVariant> germlineVariants,
            @NotNull Collection<PurpleDriver> germlineDrivers, @NotNull ClinicalTranscriptsModel clinicalTranscriptsModel) {
        List<PurpleDriver> germlineMutationDrivers =
                germlineDrivers.stream().filter(x -> x.driver() == PurpleDriverType.GERMLINE_MUTATION).collect(Collectors.toList());
        return toReportableVariants(germlineVariants, germlineMutationDrivers, ReportableVariantSource.GERMLINE, clinicalTranscriptsModel);
    }

    @NotNull
    public static Set<ReportableVariant> toReportableSomaticVariants(@NotNull Collection<PurpleVariant> somaticVariants,
            @NotNull Collection<PurpleDriver> somaticDrivers, @NotNull ClinicalTranscriptsModel clinicalTranscriptsModel) {
        List<PurpleDriver> somaticMutationDrivers =
                somaticDrivers.stream().filter(x -> x.driver() == PurpleDriverType.MUTATION).collect(Collectors.toList());
        return toReportableVariants(somaticVariants, somaticMutationDrivers, ReportableVariantSource.SOMATIC, clinicalTranscriptsModel);
    }

    @NotNull
    private static Set<ReportableVariant> toReportableVariants(@NotNull Iterable<PurpleVariant> variants,
            @NotNull Iterable<PurpleDriver> drivers, @NotNull ReportableVariantSource source,
            @NotNull ClinicalTranscriptsModel clinicalTranscriptsModel) {
        Map<DriverKey, PurpleDriver> driverMap = DriverMap.create(drivers);
        Set<ReportableVariant> reportableVariants = Sets.newHashSet();

        for (PurpleVariant variant : variants) {

            if (variant.reported()) {

                PurpleTranscriptImpact purpleTranscriptImpact = variant.otherImpacts()
                        .stream()
                        .filter(x -> x.transcript().equals(clinicalTranscriptsModel.findCanonicalTranscriptForGene(variant.gene())))
                        .findFirst()
                        .orElse(null);
                ImmutableReportableVariant.Builder builder = fromVariant(variant, source);

                PurpleDriver canonicalDriver = findCanonicalEntryForVariant(driverMap, variant);
                if (canonicalDriver != null) {
                    reportableVariants.add(builder.driverLikelihood(canonicalDriver.driverLikelihood())
                            .transcript(canonicalDriver.transcript())
                            .isCanonical(true)
                            .otherImpactClinical(purpleTranscriptImpact)
                            .build());
                }

                PurpleDriver nonCanonicalDriver = findNonCanonicalEntryForVariant(driverMap, variant);
                if (nonCanonicalDriver != null) {
                    for (PurpleTranscriptImpact transcriptImpact : variant.otherImpacts()) {
                        if (transcriptImpact.transcript().equals(nonCanonicalDriver.transcript())) {
                            reportableVariants.add(builder.driverLikelihood(nonCanonicalDriver.driverLikelihood())
                                    .transcript(nonCanonicalDriver.transcript())
                                    .isCanonical(false)
                                    .otherImpactClinical(null)
                                    .canonicalHgvsCodingImpact(transcriptImpact.hgvsCodingImpact())
                                    .canonicalHgvsProteinImpact(transcriptImpact.hgvsProteinImpact())
                                    .canonicalEffect(EventGenerator.concat(transcriptImpact.effects()))
                                    .canonicalCodingEffect(transcriptImpact.codingEffect())
                                    .build());
                        }
                    }
                }
            }
        }
        return reportableVariants;
    }

    @Nullable
    private static PurpleDriver findCanonicalEntryForVariant(@NotNull Map<DriverKey, PurpleDriver> entries,
            @NotNull PurpleVariant variant) {
        assert variant.reported();

        String canonicalTranscript = variant.canonicalImpact().transcript();
        for (PurpleDriver driver : entries.values()) {
            if (variant.gene().equals(driver.gene()) && driver.transcript().equals(canonicalTranscript)) {
                return entries.get(DriverKey.create(variant.gene(), canonicalTranscript));
            }
        }

        LOGGER.warn("No canonical entry found in driver catalog for gene {}", variant.gene());
        return null;
    }

    @Nullable
    private static PurpleDriver findNonCanonicalEntryForVariant(@NotNull Map<DriverKey, PurpleDriver> entries,
            @NotNull PurpleVariant variant) {
        assert variant.reported();

        for (PurpleDriver driver : entries.values()) {
            if (MUTATION_DRIVER_TYPES.contains(driver.driver()) && !driver.isCanonical()) {
                return entries.get(DriverKey.create(variant.gene(), driver.transcript()));
            }
        }

        return null;
    }

    @NotNull
    public static List<PurpleVariant> mergeAllVariantLists(@NotNull Iterable<PurpleVariant> list1,
            @Nullable Iterable<PurpleVariant> list2) {

        List<PurpleVariant> result = Lists.newArrayList();

        for (PurpleVariant variant : list1) {
            result.add(ImmutablePurpleVariant.builder().from(variant).build());
        }

        if (list2 != null) {
            for (PurpleVariant variant : list2) {
                result.add(ImmutablePurpleVariant.builder().from(variant).build());
            }
        }

        return result;
    }

    @NotNull
    public static List<ReportableVariant> mergeVariantLists(@NotNull Iterable<ReportableVariant> list1,
            @NotNull Iterable<ReportableVariant> list2) {
        List<ReportableVariant> result = Lists.newArrayList();

        Map<String, Double> maxLikelihoodPerGene = Maps.newHashMap();
        for (ReportableVariant variant : list1) {
            maxLikelihoodPerGene.merge(variant.gene(), variant.driverLikelihood(), Math::max);
        }

        for (ReportableVariant variant : list2) {
            maxLikelihoodPerGene.merge(variant.gene(), variant.driverLikelihood(), Math::max);
        }

        for (ReportableVariant variant : list1) {
            result.add(ImmutableReportableVariant.builder()
                    .from(variant)
                    .driverLikelihood(maxLikelihoodPerGene.get(variant.gene()))
                    .build());
        }

        for (ReportableVariant variant : list2) {
            result.add(ImmutableReportableVariant.builder()
                    .from(variant)
                    .driverLikelihood(maxLikelihoodPerGene.get(variant.gene()))
                    .build());
        }

        return result;
    }

    @NotNull
    public static ImmutableReportableVariant.Builder fromVariant(@NotNull PurpleVariant variant, @NotNull ReportableVariantSource source) {
        return ImmutableReportableVariant.builder()
                .type(variant.type())
                .source(source)
                .gene(variant.gene())
                .chromosome(variant.chromosome())
                .position(variant.position())
                .affectedCodon(variant.canonicalImpact().affectedCodon())
                .affectedExon(variant.canonicalImpact().affectedExon())
                .ref(variant.ref())
                .alt(variant.alt())
                .canonicalTranscript(variant.canonicalImpact().transcript())
                .canonicalEffect(concatEffects(variant.canonicalImpact().effects()))
                .canonicalCodingEffect(variant.canonicalImpact().codingEffect())
                .canonicalHgvsCodingImpact(variant.canonicalImpact().hgvsCodingImpact())
                .canonicalHgvsProteinImpact(variant.canonicalImpact().hgvsProteinImpact())
                .totalReadCount(variant.tumorDepth().totalReadCount())
                .alleleReadCount(variant.tumorDepth().alleleReadCount())
                .totalCopyNumber(variant.adjustedCopyNumber())
                .minorAlleleCopyNumber(variant.minorAlleleCopyNumber())
                .alleleCopyNumber(variant.variantCopyNumber())
                .hotspot(variant.hotspot())
                .clonalLikelihood(1 - variant.subclonalLikelihood())
                .biallelic(variant.biallelic())
                .genotypeStatus(variant.genotypeStatus())
                .localPhaseSet(toLocalPhaseSet(variant.localPhaseSets()));
    }

    @NotNull
    private static String concatEffects(@NotNull Iterable<PurpleVariantEffect> effects) {
        StringJoiner joiner = new StringJoiner("&");
        for (PurpleVariantEffect effect : effects) {
            joiner.add(effect.toString().toLowerCase());
        }
        return joiner.toString();
    }

    @Nullable
    private static Integer toLocalPhaseSet(@Nullable List<Integer> localPhaseSets) {
        if (localPhaseSets == null || localPhaseSets.isEmpty()) {
            return null;
        }
        return localPhaseSets.get(0);
    }
}
