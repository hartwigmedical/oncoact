package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.oncoact.patientreporter.model.PharmacogeneticsSource;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

class PharmacogeneticsSourceCreator {

    static PharmacogeneticsSource createPharmacogeneticsSource(
            @NotNull String urlPrescriptionInfo
    ) {
        return PharmacogeneticsSource.builder()
                .name(sourceName(urlPrescriptionInfo))
                .url(url(urlPrescriptionInfo))
                .build();
    }

    @NotNull
    public static String url(@NotNull String urlPrescriptionInfo) {
        String url = extractUrl(urlPrescriptionInfo);
        if (url.startsWith("https://www.pharmgkb.org")) {
            return url;
        } else {
            return Strings.EMPTY;
        }
    }

    @NotNull
    public static String sourceName(@NotNull String urlPrescriptionInfo) {
        String url = extractUrl(urlPrescriptionInfo);
        if (url.startsWith("https://www.pharmgkb.org")) {
            return "PHARMGKB";
        } else {
            return Strings.EMPTY;
        }
    }

    @NotNull
    private static String extractUrl(@NotNull String urlPrescriptionInfo) {
        return urlPrescriptionInfo.split(";")[0];
    }
}