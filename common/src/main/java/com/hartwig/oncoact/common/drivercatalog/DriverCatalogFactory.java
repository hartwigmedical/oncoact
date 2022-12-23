package com.hartwig.oncoact.common.drivercatalog;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hartwig.oncoact.common.drivercatalog.dnds.DndsDriverImpactLikelihood;
import com.hartwig.oncoact.common.purple.GeneCopyNumber;
import com.hartwig.oncoact.common.variant.SomaticVariant;
import com.hartwig.oncoact.common.variant.VariantType;
import com.hartwig.oncoact.util.Doubles;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.jetbrains.annotations.NotNull;

public final class DriverCatalogFactory
{
    private DriverCatalogFactory() {}

    @NotNull
    public static <T extends SomaticVariant> Map<DriverImpact,Integer> driverImpactCount(@NotNull final List<T> variants)
    {
        return variants.stream().collect(Collectors.groupingBy(DriverImpact::select, Collectors.counting()))
                .entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().intValue()));
    }

    @NotNull
    static <T extends SomaticVariant> Map<VariantType,Integer> variantTypeCount(@NotNull final List<T> variants)
    {
        return variantTypeCount(t -> true, variants);
    }

    @NotNull
    static <T extends SomaticVariant> Map<VariantType,Integer> variantTypeCount(final Predicate<T> predicate, final List<T> variants)
    {
        return variants.stream().filter(predicate)
                .collect(Collectors.groupingBy(SomaticVariant::type, Collectors.counting()))
                .entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().intValue()));
    }

    public static double probabilityDriverVariant(long sampleSNVCount, @NotNull final DndsDriverImpactLikelihood likelihood)
    {
        return probabilityDriverVariantSameImpact(0, sampleSNVCount, likelihood);
    }

    private static double probabilityDriverVariantSameImpact(int count, long sampleSNVCount, final DndsDriverImpactLikelihood likelihood)
    {
        double lambda = sampleSNVCount * likelihood.passengersPerMutation();
        if(Doubles.isZero(lambda))
            return 0.0;

        PoissonDistribution poissonDistribution = new PoissonDistribution(lambda);

        double pVariantNonDriver = 1 - poissonDistribution.cumulativeProbability(count);
        return likelihood.driversPerSample() / (likelihood.driversPerSample() + pVariantNonDriver * (1 - likelihood.driversPerSample()));
    }

    public static double probabilityDriverVariant(long firstVariantTypeCount, long secondVariantTypeCount,
            @NotNull final DndsDriverImpactLikelihood firstLikelihood, @NotNull final DndsDriverImpactLikelihood secondLikelihood)
    {
        if(firstLikelihood.equals(secondLikelihood))
        {
            return probabilityDriverVariantSameImpact(1, firstVariantTypeCount, firstLikelihood);
        }

        double lambda1 = firstVariantTypeCount * firstLikelihood.passengersPerMutation();
        double lambda2 = secondVariantTypeCount * secondLikelihood.passengersPerMutation();
        if(Doubles.isZero(lambda1) || Doubles.isZero(lambda2))
        {
            return Math.max(probabilityDriverVariant(firstVariantTypeCount, firstLikelihood),
                    probabilityDriverVariant(secondVariantTypeCount, secondLikelihood));
        }

        final double pDriver = Math.max(firstLikelihood.driversPerSample(), secondLikelihood.driversPerSample());
        final double pVariantNonDriver1 = 1 - new PoissonDistribution(lambda1).cumulativeProbability(0);
        final double pVariantNonDriver2 = 1 - new PoissonDistribution(lambda2).cumulativeProbability(0);
        final double pVariantNonDriver = pVariantNonDriver1 * pVariantNonDriver2;

        return pDriver / (pDriver + pVariantNonDriver * (1 - pDriver));
    }

    public static DriverCatalog createCopyNumberDriver(
            DriverCategory category, DriverType driver, final LikelihoodMethod likelihoodMethod, final boolean biallelic,
            final GeneCopyNumber geneCopyNumber)
    {
        return ImmutableDriverCatalog.builder()
                .chromosome(geneCopyNumber.chromosome())
                .chromosomeBand(geneCopyNumber.chromosomeBand())
                .gene(geneCopyNumber.geneName())
                .transcript(geneCopyNumber.transName())
                .isCanonical(geneCopyNumber.isCanonical())
                .missense(0)
                .nonsense(0)
                .inframe(0)
                .frameshift(0)
                .splice(0)
                .driverLikelihood(1)
                .driver(driver)
                .likelihoodMethod(likelihoodMethod)
                .category(category)
                .biallelic(biallelic)
                .minCopyNumber(geneCopyNumber.minCopyNumber())
                .maxCopyNumber(geneCopyNumber.maxCopyNumber())
                .build();
    }

}
