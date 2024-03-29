package com.hartwig.oncoact.wildtype;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.hmftools.datamodel.linx.LinxBreakend;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.variant.ReportableVariant;

import org.jetbrains.annotations.NotNull;

public final class WildTypeFactory {

    private WildTypeFactory() {
    }

    @NotNull
    public static List<WildTypeGene> filterQCWildTypes(@NotNull Collection<PurpleQCStatus> purpleQCStatus,
            @NotNull List<WildTypeGene> wildTypeGenes) {
        if (!purpleQCStatus.contains(PurpleQCStatus.FAIL_NO_TUMOR) && !purpleQCStatus.contains(PurpleQCStatus.WARN_LOW_PURITY)) {
            return wildTypeGenes;
        }
        return Lists.newArrayList();
    }

    @NotNull
    public static List<WildTypeGene> determineWildTypeGenes(@NotNull Collection<ReportableVariant> reportableGermlineVariants,
            @NotNull Collection<ReportableVariant> reportableSomaticVariants, @NotNull Collection<PurpleGainLoss> reportableSomaticGainsLosses,
            @NotNull Collection<LinxFusion> reportableFusions, @NotNull Collection<HomozygousDisruption> homozygousDisruptions,
            @NotNull Collection<LinxBreakend> reportableBreakends, @NotNull Collection<DriverGene> driverGenes) {
        List<WildTypeGene> wildTypeGenes = Lists.newArrayList();

        for (DriverGene driverGene : driverGenes) {
            boolean hasSomaticVariant = false;
            for (ReportableVariant somaticVariant : reportableSomaticVariants) {
                if (driverGene.gene().equals(somaticVariant.gene())) {
                    hasSomaticVariant = true;
                }
            }

            boolean hasGermlineVariant = false;
            for (ReportableVariant germlineVariant : reportableGermlineVariants) {
                if (driverGene.gene().equals(germlineVariant.gene())) {
                    hasGermlineVariant = true;
                }
            }

            boolean hasSomaticGainLoss = false;
            for (PurpleGainLoss gainLoss : reportableSomaticGainsLosses) {
                if (driverGene.gene().equals(gainLoss.gene())) {
                    hasSomaticGainLoss = true;
                }
            }

            boolean hasFusion = false;
            for (LinxFusion fusion : reportableFusions) {
                if (driverGene.gene().equals(fusion.geneStart()) || driverGene.gene().equals(fusion.geneEnd())) {
                    hasFusion = true;
                }
            }

            boolean hasHomozygousDisruption = false;
            for (HomozygousDisruption homozygousDisruption : homozygousDisruptions) {
                if (driverGene.gene().equals(homozygousDisruption.gene())) {
                    hasFusion = true;
                }
            }

            boolean hasBreakend = false;
            for (LinxBreakend breakend : reportableBreakends) {
                if (driverGene.gene().equals(breakend.gene())) {
                    hasBreakend = true;
                }
            }

            if (!hasSomaticVariant && !hasGermlineVariant && !hasSomaticGainLoss && !hasFusion && !hasHomozygousDisruption
                    && !hasBreakend) {
                wildTypeGenes.add(ImmutableWildTypeGene.builder().gene(driverGene.gene()).build());
            }
        }

        return wildTypeGenes;
    }
}