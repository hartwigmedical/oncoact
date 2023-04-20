package com.hartwig.oncoact.lims;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LimsChecker {

    private static final Logger LOGGER = LogManager.getLogger(LimsChecker.class);

    private LimsChecker() {
    }

    @Nullable
    public static String toHospitalPathologySampleIdForReport(@NotNull String hospitalPathologySampleId, @NotNull String tumorSampleId) {
            if (!hospitalPathologySampleId.equals(Lims.NOT_AVAILABLE_STRING) && !hospitalPathologySampleId.equals(Strings.EMPTY)) {
                return hospitalPathologySampleId;
            } else {

                LOGGER.warn("Missing or invalid hospital pathology sample ID for sample '{}': {}. Please fix!",
                        tumorSampleId,
                        hospitalPathologySampleId);

                return null;
            }
    }
}
