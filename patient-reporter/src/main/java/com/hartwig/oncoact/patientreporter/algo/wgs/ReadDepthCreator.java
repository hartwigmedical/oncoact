package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.oncoact.patientreporter.model.ReadDepth;

class ReadDepthCreator {

    static ReadDepth createReadDepth(
            int alleleReadCount, int totalReadcount
    ) {
        return ReadDepth.builder()
                .alleleReadCount(alleleReadCount)
                .totalReadCount(totalReadcount)
                .build();
    }
}