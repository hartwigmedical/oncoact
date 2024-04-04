package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.hartwig.oncoact.patientreporter.model.ImmutableTherapy;
import com.hartwig.oncoact.patientreporter.model.Therapy;

class TherapyCreator {

    static Therapy createTherapy() {
        return ImmutableTherapy.builder()
                .highLevelEvidence(Lists.newArrayList()) //TODO implementation
                .clinicalStudies(Lists.newArrayList()) //TODO implementation
                .build();
    }
}