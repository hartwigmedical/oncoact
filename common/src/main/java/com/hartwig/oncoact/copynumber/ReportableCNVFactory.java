package com.hartwig.oncoact.copynumber;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.ImmutablePurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReportableCNVFactory {

    private ReportableCNVFactory() {
    }

    @NotNull
    public static List<PurpleGainLoss> mergeCNVLists(@NotNull List<PurpleGainLoss> somaticGainsLosses,
                                                     @NotNull List<PurpleGainLoss> germlineLosses) {

        List<PurpleGainLoss> result = Lists.newArrayList();

        for (PurpleGainLoss somaticCNV : somaticGainsLosses) {
            result.add(ImmutablePurpleGainLoss.builder()
                    .from(somaticCNV)
                    .build());
        }

        for (PurpleGainLoss germlineCNV : germlineLosses) {
            result.add(ImmutablePurpleGainLoss.builder()
                    .from(germlineCNV)
                    .build());
        }

        return result;
    }
}