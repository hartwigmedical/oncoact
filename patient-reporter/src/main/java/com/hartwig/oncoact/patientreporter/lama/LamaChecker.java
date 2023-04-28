package com.hartwig.oncoact.patientreporter.lama;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

public class LamaChecker {

    private static final Logger LOGGER = LogManager.getLogger(LamaChecker.class);

    private LamaChecker() {
    }

    public static void lamaCheck(@Nullable LocalDate refArrivalDate, @Nullable LocalDate tumorArrivalDate) {

        LocalDate refDate = checkArrivalRef(refArrivalDate);
        LocalDate tumorDate = checkArrivalTumor(tumorArrivalDate);

        if (refDate == null) {
            LOGGER.warn("Could not find arrival date for ref sample: {}", "refSampleId");
        }

        if (tumorDate == null) {
            LOGGER.warn("Could not find arrival date for tumor sample: {}", "tumorSampleId");
        }

    }

    public static LocalDate checkArrivalRef(@Nullable LocalDate refArrivalDate) {
        if (refArrivalDate == null) {
            return null;
        } else {
            return refArrivalDate;
        }
    }

    public static LocalDate checkArrivalTumor(@Nullable LocalDate tumorArrivalDate) {
        if (tumorArrivalDate == null) {
            return null;
        } else {
            return tumorArrivalDate;
        }
    }
}
