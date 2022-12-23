package com.hartwig.oncoact.patientreporter.cfreport.data;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.util.DataUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GeneUtil {

    private GeneUtil() {
    }

    @NotNull
    public static String copyNumberToString(@Nullable Double copyNumber, boolean hasReliablePurity) {
        if (!hasReliablePurity) {
            return DataUtil.NA_STRING;
        } else {
            return copyNumber != null ? ReportResources.decimalFormat("#.#").format(copyNumber) : DataUtil.NA_STRING;
        }
    }

    @NotNull
    static String zeroPrefixed(@NotNull String location) {
        // First remove q or p arm if present.
        int armStart = location.indexOf("q");
        if (armStart < 0) {
            armStart = location.indexOf("p");
        }

        String chromosome = armStart > 0 ? location.substring(0, armStart) : location;

        try {
            int chromosomeIndex = Integer.parseInt(chromosome);
            if (chromosomeIndex < 10) {
                return "0" + location;
            } else {
                return location;
            }
        } catch (NumberFormatException exception) {
            // No need to prefix Y/X chromosomes
            return location;
        }
    }
}
