package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.hmftools.datamodel.purple.PurpleTumorMutationalStatus;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.model.TumorMutationalBurden;
import com.hartwig.oncoact.patientreporter.model.TumorMutationalStatus;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import static com.hartwig.oncoact.patientreporter.algo.wgs.DoubleFormatter.formatSingleDecimal;
import static com.hartwig.oncoact.patientreporter.algo.wgs.MicrosatelliteCreator.SINGLE_DECIMAL_FORMAT;

class TumorMutationalBurdenCreator {

    static String createTumorMutationalBurden(@NotNull GenomicAnalysis analysis) {
        if (!analysis.hasReliablePurity()) {
            return Formats.NA_STRING;
        }

        String status = getTumorMutationalStatus(analysis.tumorMutationalBurdenStatus()).label;
        double value = analysis.tumorMutationalBurden();

        return status + " (" + formatSingleDecimal(value) + ")";
    }

    static TumorMutationalBurden createTumorMutationalBurdenExtend(
            double tumorMutationalBurden,
            @NotNull PurpleTumorMutationalStatus tumorMutationalBurdenStatus,
            boolean hasReliablePurity) {
        return TumorMutationalBurden.builder()
                .value(tumorMutationalBurden)
                .status(getTumorMutationalStatus(tumorMutationalBurdenStatus))
                .label(getLabel(hasReliablePurity, getTumorMutationalStatus(tumorMutationalBurdenStatus), tumorMutationalBurden))
                .build();
    }

    @NotNull
    private static String getLabel(boolean hasReliablePurity, TumorMutationalStatus status, double tumorMutationalBurden) {
        return hasReliablePurity ? status + " " + SINGLE_DECIMAL_FORMAT.format(tumorMutationalBurden) : Formats.NA_STRING;
    }

    private static TumorMutationalStatus getTumorMutationalStatus(PurpleTumorMutationalStatus status) {
        switch (status) {
            case HIGH:
                return TumorMutationalStatus.HIGH;
            case LOW:
                return TumorMutationalStatus.LOW;
            case UNKNOWN:
                return TumorMutationalStatus.UNKNOWN;
            default:
                throw new IllegalStateException("Unknown tumor mutational status: " + status);
        }
    }
}