package com.hartwig.oncoact.disruption;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.hmftools.datamodel.linx.ImmutableHomozygousDisruption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReportableGeneDisruptionFactory {

    private ReportableGeneDisruptionFactory() {
    }

    @NotNull
    public static List<HomozygousDisruption> mergeHomozygousDisruptionsLists(@NotNull List<HomozygousDisruption> somaticHomozygousDisruptions,
                                                                             @Nullable List<HomozygousDisruption> germlineHomozygousDisruptions) {
        List<HomozygousDisruption> result = Lists.newArrayList();

        for (HomozygousDisruption somaticHomozygousDisruption : somaticHomozygousDisruptions) {
            result.add(ImmutableHomozygousDisruption.builder()
                    .from(somaticHomozygousDisruption)
                    .build());
        }

        if (germlineHomozygousDisruptions != null) {
            for (HomozygousDisruption germlineHomozygousDisruption : germlineHomozygousDisruptions) {
                result.add(ImmutableHomozygousDisruption.builder()
                        .from(germlineHomozygousDisruption)
                        .build());
            }
        }
        return result;
    }
}