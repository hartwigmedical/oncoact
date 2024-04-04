package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.model.Version;
import org.jetbrains.annotations.NotNull;

class VersionCreator {

    static Version createVersion(
            @NotNull String pipelineVersion,
            @NotNull String udiDi,
            boolean hasReliablePurity,
            double impliedPurity
    ) {
        return Version.builder()
                .molecularPipeline(pipelineVersion)
                .reportingPipeline("1.0")
                .udiDi(udiDi)
                .qsFormNumber(determineForNumber(hasReliablePurity, impliedPurity))
                .build();
    }

    @VisibleForTesting
    static QsFormNumber determineForNumber(boolean hasReliablePurity, double purity) {
        return hasReliablePurity && purity > ReportResources.PURITY_CUTOFF
                ? QsFormNumber.FOR_080
                : QsFormNumber.FOR_209;
    }
}