package com.hartwig.oncoact.copynumber;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ReportableCNVFactory {

    private ReportableCNVFactory() {
    }

    @NotNull
    public static List<PurpleGainLoss> mergeCNVLists(@NotNull List<PurpleGainLoss> somaticGainsLosses,
                                                     @Nullable List<PurpleGainLoss> germlineLosses) {

        List<PurpleGainLoss> allGainsLosses = Lists.newArrayList();
        allGainsLosses.addAll(somaticGainsLosses);
        if (germlineLosses != null) {
            allGainsLosses.addAll(germlineLosses);
        }

        return allGainsLosses;
    }
}