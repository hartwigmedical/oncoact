package com.hartwig.oncoact.copynumber;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.ImmutablePurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ReportableCNVFactory {

    private ReportableCNVFactory() {
    }

    @NotNull
    public static List<PurpleGainLoss> mergeCNVLists(@NotNull Iterable<PurpleGainLoss> somaticGainsLosses,
                                                     @Nullable Iterable<PurpleGainLoss> germlineLosses) {

        List<PurpleGainLoss> result = Lists.newArrayList();

        for (PurpleGainLoss somaticCNV : somaticGainsLosses) {
            result.add(ImmutablePurpleGainLoss.copyOf(somaticCNV));
        }

        if (germlineLosses != null) {
            for (PurpleGainLoss germlineCNV : germlineLosses) {
                result.add(ImmutablePurpleGainLoss.copyOf(germlineCNV));
            }
        }
        return result;
    }
}