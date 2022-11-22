package com.hartwig.oncoact.common.purple;

import com.hartwig.oncoact.common.utils.Doubles;

public enum TumorMutationalStatus
{
    HIGH("High"),
    LOW("Low"),
    UNKNOWN("Unknown");

    public static final double TMB_THRESHOLD = 10;
    public static final int TML_THRESHOLD = 140;

    private final String mDisplay;

    TumorMutationalStatus(final String display)
    {
        mDisplay = display;
    }

    public static TumorMutationalStatus fromBurdenPerMb(double burdenPerMb)
    {
        return Doubles.greaterOrEqual(burdenPerMb, TMB_THRESHOLD) ? HIGH : LOW;
    }

    public static TumorMutationalStatus fromLoad(double load)
    {
        return load >= TML_THRESHOLD ? HIGH : LOW;
    }

    public String display()
    {
        return mDisplay;
    }
}
