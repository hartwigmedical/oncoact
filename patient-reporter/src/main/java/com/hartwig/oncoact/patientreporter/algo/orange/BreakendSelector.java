package com.hartwig.oncoact.patientreporter.algo.orange;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.knownfusion.KnownFusionCache;
import com.hartwig.oncoact.knownfusion.KnownFusionData;
import com.hartwig.oncoact.knownfusion.KnownFusionType;
import com.hartwig.oncoact.orange.linx.LinxBreakend;
import com.hartwig.oncoact.orange.linx.LinxFusion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BreakendSelector {

    static final String DOWNSTREAM_ORIENTATION = "Downstream";
    static final String UPSTREAM_ORIENTATION = "Upstream";

    private BreakendSelector() {
    }

    @NotNull
    public static List<LinxBreakend> selectInterestingUnreportedBreakends(@NotNull Iterable<LinxBreakend> allBreakends,
            @NotNull Iterable<LinxFusion> reportableFusions, @NotNull KnownFusionCache knownFusionCache) {
        List<LinxBreakend> interestingUnreportedBreakends = Lists.newArrayList();
        for (LinxBreakend breakend : allBreakends) {
            if (!breakend.reported() && breakend.disruptive()) {
                if (isUnreportedBreakInPromiscuousExonRange(knownFusionCache, reportableFusions, breakend)) {
                    interestingUnreportedBreakends.add(breakend);
                }
            }
        }
        return interestingUnreportedBreakends;
    }

    private static boolean isUnreportedBreakInPromiscuousExonRange(@NotNull KnownFusionCache knownFusionCache,
            @NotNull Iterable<LinxFusion> reportableFusions, @NotNull LinxBreakend breakend) {
        int nextExon = breakend.nextSpliceExonRank();

        KnownFusionData three =
                findByThreeGene(knownFusionCache.fusionsByType(KnownFusionType.PROMISCUOUS_3), breakend.gene(), breakend.transcriptId());
        if (three != null) {
            boolean hasReportableFusion = hasReportableThreeFusion(reportableFusions, breakend.gene(), nextExon);
            boolean hasDownstreamOrientation = breakend.geneOrientation().equals(DOWNSTREAM_ORIENTATION);
            boolean isWithinExonRange = isWithinExonRange(three.threeGeneExonRange(), nextExon);
            if (isWithinExonRange && hasDownstreamOrientation && !hasReportableFusion) {
                return true;
            }
        }

        KnownFusionData five =
                findByFiveGene(knownFusionCache.fusionsByType(KnownFusionType.PROMISCUOUS_5), breakend.gene(), breakend.transcriptId());
        if (five != null) {
            boolean hasReportableFusion = hasReportableFiveFusion(reportableFusions, breakend.gene(), nextExon);
            boolean hasUpstreamOrientation = breakend.geneOrientation().equals(UPSTREAM_ORIENTATION);
            boolean isWithinExonRange = isWithinExonRange(five.fiveGeneExonRange(), nextExon);

            if (isWithinExonRange && hasUpstreamOrientation && !hasReportableFusion) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWithinExonRange(@NotNull int[] exonRange, int exon) {
        return exon >= exonRange[0] && exon <= exonRange[1];
    }

    private static boolean hasReportableFiveFusion(@NotNull Iterable<LinxFusion> reportableFusions, @NotNull String gene, int exon) {
        for (LinxFusion fusion : reportableFusions) {
            if (fusion.geneStart().equals(gene) && fusion.fusedExonUp() == exon) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasReportableThreeFusion(@NotNull Iterable<LinxFusion> reportableFusions, @NotNull String gene, int exon) {
        for (LinxFusion fusion : reportableFusions) {
            if (fusion.geneEnd().equals(gene) && fusion.fusedExonDown() == exon) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static KnownFusionData findByFiveGene(@NotNull Iterable<KnownFusionData> knownFusions, @NotNull String geneToFind,
            @NotNull String transcriptToFind) {
        for (KnownFusionData knownFusion : knownFusions) {
            if (knownFusion.fiveGene().equals(geneToFind) && knownFusion.specificExonsTransName().equals(transcriptToFind)) {
                return knownFusion;
            }
        }
        return null;
    }

    @Nullable
    private static KnownFusionData findByThreeGene(@NotNull Iterable<KnownFusionData> knownFusions, @NotNull String geneToFind,
            @NotNull String transcriptToFind) {
        for (KnownFusionData knownFusion : knownFusions) {
            if (knownFusion.threeGene().equals(geneToFind) && knownFusion.specificExonsTransName().equals(transcriptToFind)) {
                return knownFusion;
            }
        }
        return null;
    }
}
