package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.lama.client.model.BiopsySite;
import com.hartwig.oncoact.patientreporter.model.Biopsy;
import com.hartwig.oncoact.patientreporter.model.Lateralisation;
import org.jetbrains.annotations.Nullable;

class BiopsyCreator {

    static Biopsy createBiopsy(
            @Nullable BiopsySite biopsySite
    ) {

        String biopsyLocation = "-";
        String biopsySubLocation = "-";
        Lateralisation biopsyLateralisation = Lateralisation.NULL;
        String isPrimaryTumor = "-";
        if (biopsySite != null) {
            biopsyLocation = biopsySite.getLocation();
            biopsySubLocation = biopsySite.getSubLocation();
            biopsyLateralisation =
                    biopsySite.getLateralisation() != null ? getLateralisation(biopsySite.getLateralisation()) : biopsyLateralisation;
            isPrimaryTumor = biopsySite.getIsPrimaryTumor() != null ? (biopsySite.getIsPrimaryTumor() ? "yes" : "no") : "-";
            return Biopsy.builder()
                    .location(biopsyLocation)
                    .subLocation(biopsySubLocation)
                    .lateralisation(biopsyLateralisation)
                    .isPrimaryTumor(isPrimaryTumor)
                    .build();
        } else {
            return Biopsy.builder()
                    .location(biopsyLocation)
                    .subLocation(biopsySubLocation)
                    .lateralisation(biopsyLateralisation)
                    .isPrimaryTumor(isPrimaryTumor)
                    .build();
        }
    }

    static Lateralisation getLateralisation(BiopsySite.LateralisationEnum lateralisationEnum) {
        if (lateralisationEnum == null) {
            return Lateralisation.NULL;
        } else {
            switch (lateralisationEnum) {
                case LEFT:
                    return Lateralisation.LEFT;
                case RIGHT:
                    return Lateralisation.RIGHT;
                case UNKNOWN:
                    return Lateralisation.UNKNOWN;
                default:
                    throw new IllegalStateException("Unknown lateralisation: " + lateralisationEnum);
            }
        }
    }
}