package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.List;
import java.util.stream.Collectors;

import com.hartwig.oncoact.common.peach.PeachGenotype;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class Pharmacogenetics {

    private Pharmacogenetics() {
    }

    @NotNull
    public static List<PeachGenotype> sort(@NotNull List<PeachGenotype> peachGenotypes) {
        return peachGenotypes.stream().sorted((genotype1, genotype2) -> {
            if (genotype1.gene().equals(genotype2.gene())) {
                return genotype1.haplotype().compareTo(genotype2.haplotype());
            } else {
                return genotype1.gene().compareTo(genotype2.gene());
            }

        }).collect(Collectors.toList());
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

    public static int countPhenotypes(@NotNull List<PeachGenotype> peachGenotypes) {
        return peachGenotypes.size();
    }
}
