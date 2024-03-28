package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.lama.client.model.TumorType;
import com.hartwig.oncoact.patientreporter.model.PrimaryTumor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

class PrimaryTumorCreator {

    static PrimaryTumor createPrimaryTumor(
            @Nullable TumorType tumorType
    ) {

        return PrimaryTumor.builder()
                .location(createTumorLocation(tumorType))
                .type(createTumorType(tumorType))
                .build();
    }

    private static String createTumorLocation(@Nullable TumorType tumorType) {
        return Optional.ofNullable(tumorType).map(TumorType::getLocation).orElse("");
    }

    private static String createTumorType(@Nullable TumorType tumorType) {
        String type = Optional.ofNullable(tumorType).map(TumorType::getType).orElse("");
        String extra = Optional.ofNullable(tumorType).map(TumorType::getExtra).orElse("");
        return extra.isEmpty() ? type : type + " \n " + extra;
    }
}