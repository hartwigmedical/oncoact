package com.hartwig.oncoact.common.purple;

public class PurpleTestUtils
{

    public static ImmutablePurpleCopyNumber.Builder createCopyNumber(
            final String chromosome, final int start, final int end, final double copyNumber)
    {
        return ImmutablePurpleCopyNumber.builder()
                .chromosome(chromosome)
                .start(start)
                .end(end)
                .averageTumorCopyNumber(copyNumber)
                .segmentStartSupport(SegmentSupport.NONE)
                .segmentEndSupport(SegmentSupport.NONE)
                .method(CopyNumberMethod.UNKNOWN)
                .bafCount(0)
                .depthWindowCount(1)
                .gcContent(0)
                .minStart(start)
                .maxStart(start)
                .averageObservedBAF(0.5)
                .averageActualBAF(0.5);
    }
}
