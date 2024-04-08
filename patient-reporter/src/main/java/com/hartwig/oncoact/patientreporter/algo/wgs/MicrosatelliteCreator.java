package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.model.Microsatellite;
import com.hartwig.oncoact.patientreporter.model.MicrosatelliteStatus;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import static com.hartwig.oncoact.patientreporter.algo.wgs.DoubleFormatter.formatSingleDecimal;

class MicrosatelliteCreator {

    static final DecimalFormat SINGLE_DECIMAL_FORMAT = ReportResources.decimalFormat("#.#");

    static String createMicrosatallite(@NotNull GenomicAnalysis analysis) {
        if (!analysis.hasReliablePurity()) {
            return Formats.NA_STRING;
        }

        String label = getMicrosatalliteStatus(analysis.microsatelliteStatus()).label;
        double value = analysis.microsatelliteIndelsPerMb();

        return label + " (" + formatSingleDecimal(value) + ")";
    }

    static Microsatellite createMicrosatalliteExtend(
            double microsatelliteIndelsPerMb,
            @NotNull PurpleMicrosatelliteStatus microsatelliteStatus,
            boolean hasReliablePurity) {
        return Microsatellite.builder()
                .value(microsatelliteIndelsPerMb)
                .status(getMicrosatalliteStatus(microsatelliteStatus))
                .label(getLabel(hasReliablePurity, getMicrosatalliteStatus(microsatelliteStatus), microsatelliteIndelsPerMb))
                .build();
    }

    @NotNull
    private static String getLabel(boolean hasReliablePurity, MicrosatelliteStatus status, double microsatelliteIndelsPerMb) {
        return hasReliablePurity ? status + " " + SINGLE_DECIMAL_FORMAT.format(microsatelliteIndelsPerMb) : Formats.NA_STRING;
    }

    static MicrosatelliteStatus getMicrosatalliteStatus(PurpleMicrosatelliteStatus status) {
        switch (status) {
            case MSI:
                return MicrosatelliteStatus.MSI;
            case MSS:
                return MicrosatelliteStatus.MSS;
            case UNKNOWN:
                return MicrosatelliteStatus.UNKNOWN;
            default:
                throw new IllegalStateException("Unknown microsatellite status: " + status);
        }
    }
}