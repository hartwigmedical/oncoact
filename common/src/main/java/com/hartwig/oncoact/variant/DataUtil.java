package com.hartwig.oncoact.variant;

import java.text.DecimalFormat;

import org.jetbrains.annotations.NotNull;

public final class DataUtil {

    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("#'%'");

    private DataUtil() {
    }

    @NotNull
    public static String formatPercentage(double percentage) {
        return PERCENTAGE_FORMAT.format(percentage);
    }
}
