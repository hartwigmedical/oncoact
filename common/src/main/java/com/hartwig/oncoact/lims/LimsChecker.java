package com.hartwig.oncoact.lims;

import com.hartwig.oncoact.lims.cohort.LimsCohortConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LimsChecker {

    private static final Logger LOGGER = LogManager.getLogger(LimsChecker.class);

    private LimsChecker() {
    }

    public static boolean checkGermlineVariants(@Nullable LimsJsonSampleData sampleData, @Nullable LimsCohortConfig cohort,
            @NotNull String sampleId) {
        if (sampleData != null && cohort != null) {
            if (sampleData.reportGermlineVariants()) {
                if (!cohort.reportGermline()) {
                    LOGGER.warn("Consent of report germline variants is true, but must be false for sample '{}'", sampleId);
                }
                return true;
            } else {
                if (cohort.reportGermline()) {
                    LOGGER.warn("Consent of report germline variants is false, but must be true for sample '{}'", sampleId);
                }
                return false;
            }
        } else {
            return false;
        }
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
