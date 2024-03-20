package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.model.HomologousRecombinationDeficiencyStatus;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import static com.hartwig.oncoact.patientreporter.algo.wgs.DoubleFormatter.formatSingleDecimal;
import static com.hartwig.oncoact.patientreporter.model.HomologousRecombinationDeficiencyStatus.*;

class HomologousRecombinationDeficiencyCreator {

    static String createHomologousRecombinationDeficiency(@NotNull GenomicAnalysis analysis) {
        boolean reliablePurity = analysis.hasReliablePurity();
        HomologousRecombinationDeficiencyStatus status = getHomologousRecombinationDeficiencyStatus(analysis.hrdStatus());

        if (reliablePurity && isReportedStatus(status)) {
            return status.label + " (" + formatSingleDecimal(analysis.hrdValue()) + ")";
        } else {
            return Formats.NA_STRING;
        }
    }

    private static boolean isReportedStatus(HomologousRecombinationDeficiencyStatus status) {
        return status == HR_DEFICIENT || status == HR_PROFICIENT;
    }

    private static HomologousRecombinationDeficiencyStatus getHomologousRecombinationDeficiencyStatus(ChordStatus status) {
        switch (status) {
            case HR_DEFICIENT:
                return HR_DEFICIENT;
            case HR_PROFICIENT:
                return HR_PROFICIENT;
            case CANNOT_BE_DETERMINED:
                return CANNOT_BE_DETERMINED;
            case UNKNOWN:
                return UNKNOWN;
            default:
                throw new IllegalStateException("Unknown homologous recombination deficiency status: " + status);
        }
    }
}
