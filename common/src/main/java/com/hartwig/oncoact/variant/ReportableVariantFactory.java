package com.hartwig.oncoact.variant;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.orange.purple.PurpleDriver;
import com.hartwig.oncoact.orange.purple.PurpleDriverType;
import com.hartwig.oncoact.orange.purple.PurpleTranscriptImpact;
import com.hartwig.oncoact.orange.purple.PurpleVariant;
import com.hartwig.oncoact.orange.purple.PurpleVariantEffect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ReportableVariantFactory {

    private static final Logger LOGGER = LogManager.getLogger(ReportableVariantFactory.class);

    private ReportableVariantFactory() {
    }

    @NotNull
    public static Set<ReportableVariant> toReportableGermlineVariants(@NotNull Set<PurpleVariant> germlineVariants,
            @NotNull Set<PurpleDriver> germlineDrivers) {
        List<PurpleDriver> germlineMutationDrivers =
                germlineDrivers.stream().filter(x -> x.type() == PurpleDriverType.GERMLINE_MUTATION).collect(Collectors.toList());
        return toReportableVariants(germlineVariants, germlineMutationDrivers, ReportableVariantSource.GERMLINE);
    }

    @NotNull
    public static Set<ReportableVariant> toReportableSomaticVariants(@NotNull Set<PurpleVariant> somaticVariants,
            @NotNull Set<PurpleDriver> somaticDrivers) {
        List<PurpleDriver> somaticMutationDrivers =
                somaticDrivers.stream().filter(x -> x.type() == PurpleDriverType.MUTATION).collect(Collectors.toList());
        return toReportableVariants(somaticVariants, somaticMutationDrivers, ReportableVariantSource.SOMATIC);
    }

    @NotNull
    private static Set<ReportableVariant> toReportableVariants(@NotNull Iterable<PurpleVariant> variants,
            @NotNull Iterable<PurpleDriver> drivers, @NotNull ReportableVariantSource source) {
        Map<DriverKey, PurpleDriver> driverMap = DriverMap.create(drivers);
        Set<ReportableVariant> reportableVariants = Sets.newHashSet();

        for (PurpleVariant variant : variants) {
            if (variant.reported()) {
                ImmutableReportableVariant.Builder builder = fromVariant(variant, source);

                PurpleDriver canonicalDriver = findCanonicalEntryForVariant(driverMap, variant);
                if (canonicalDriver != null) {
                    reportableVariants.add(builder.driverLikelihood(canonicalDriver.driverLikelihood())
                            .transcript(canonicalDriver.transcript())
                            .isCanonical(true)
                            .build());
                }

                PurpleDriver nonCanonicalDriver = findNonCanonicalEntryForVariant(driverMap, variant);
                if (nonCanonicalDriver != null) {
                    PurpleTranscriptImpact firstOtherImpact = variant.otherImpacts().iterator().next();
                    reportableVariants.add(builder.driverLikelihood(nonCanonicalDriver.driverLikelihood())
                            .transcript(nonCanonicalDriver.transcript())
                            .isCanonical(false)
                            .canonicalHgvsCodingImpact(firstOtherImpact.hgvsCodingImpact())
                            .canonicalHgvsProteinImpact(firstOtherImpact.hgvsProteinImpact())
                            .build());
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

        if (variant.otherImpacts().isEmpty()) {
            return null;
        }

        String nonCanonicalTranscript = variant.otherImpacts().iterator().next().transcript();
        for (PurpleDriver driver : entries.values()) {
            if (variant.gene().equals(driver.gene()) && driver.transcript().equals(nonCanonicalTranscript)) {
                return entries.get(DriverKey.create(variant.gene(), nonCanonicalTranscript));
            }
        }

        return null;
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
                .ref(variant.ref())
                .alt(variant.alt())
                .otherReportedEffects(AltTranscriptReportableInfoFactory.serialize(variant.otherImpacts()))
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
