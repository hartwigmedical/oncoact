package com.hartwig.oncoact.common.variant.msi;

import com.hartwig.oncoact.util.Doubles;

import org.jetbrains.annotations.NotNull;

public enum MicrosatelliteStatus
{
    MSI("Unstable"),
    MSS("Stable"),
    UNKNOWN("Unknown");

    public static final double MSI_THRESHOLD = 4.0;

    private final String display;

    MicrosatelliteStatus(final String display)
    {
        this.display = display;
    }

    @NotNull
    public static MicrosatelliteStatus fromIndelsPerMb(double microsatelliteIndelsPerMb)
    {
        return Doubles.greaterOrEqual(microsatelliteIndelsPerMb, MSI_THRESHOLD) ? MSI : MSS;
    }

    @NotNull
    public String display()
    {
        return display;
    }
}
