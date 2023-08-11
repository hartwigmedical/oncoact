package com.hartwig.oncoact.disruption;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReportableGeneDisruptionFactory {

    private ReportableGeneDisruptionFactory() {
    }

    @NotNull
    public static List<HomozygousDisruption> mergeHomozygousDisruptionsLists(
            @NotNull List<HomozygousDisruption> somaticHomozygousDisruptions,
            @Nullable List<HomozygousDisruption> germlineHomozygousDisruptions) {
        List<HomozygousDisruption> allHomozygousDisruption = Lists.newArrayList();

        allHomozygousDisruption.addAll(somaticHomozygousDisruptions);
        if (germlineHomozygousDisruptions != null) {
            allHomozygousDisruption.addAll(germlineHomozygousDisruptions);
        }

        return allHomozygousDisruption;
    }

    @NotNull
    public static List<GeneDisruption> mergeGeneDisruptionsLists(@NotNull List<GeneDisruption> somaticGeneDisruptions,
            @Nullable List<GeneDisruption> germlineGeneDisruptions) {
        List<GeneDisruption> allDisruptions = Lists.newArrayList();
        allDisruptions.addAll(somaticGeneDisruptions);
        if (germlineGeneDisruptions != null) {
            allDisruptions.addAll(germlineGeneDisruptions);
        }

        return allDisruptions;
    }
}