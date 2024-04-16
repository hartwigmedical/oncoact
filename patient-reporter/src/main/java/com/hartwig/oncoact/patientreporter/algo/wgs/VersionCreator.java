package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.model.Version;
import org.jetbrains.annotations.Nullable;

class VersionCreator {

    static Version createVersion(
            @Nullable String pipelineVersion,
            @Nullable String udiDi,
            QsFormNumber qsFormNumber
    ) {
        return Version.builder()
                .molecularPipeline(pipelineVersion)
                .reportingPipeline("1.0")
                .udiDi(udiDi)
                .qsFormNumber(qsFormNumber)
                .build();
    }
}