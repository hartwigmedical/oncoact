package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaReporting;
import com.hartwig.oncoact.patientreporter.cfreport.data.HLAAllele;
import com.hartwig.oncoact.patientreporter.model.HlaAllele;
import com.hartwig.oncoact.patientreporter.model.HlaAlleleFail;
import com.hartwig.oncoact.patientreporter.model.HlaAlleleSummary;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class HlaAllelesCreator {
    static List<HlaAlleleSummary> createHlaAllelesSummary(
            @NotNull HlaAllelesReportingData hlaReportingData
    ) {
        return createHlaAllelesListSummary(hlaReportingData);
    }

    static List<HlaAllele> createHlaAlleles(
            @NotNull HlaAllelesReportingData hlaReportingData,
            boolean hasReliablePurity
    ) {
        return sort(createHlaAllelesList(hlaReportingData, hasReliablePurity));
    }

    static List<HlaAlleleFail> createHlaAllelesFailed(
            @NotNull HlaAllelesReportingData hlaReportingData,
            boolean hasReliablePurity
    ) {
        return sortFailed(createHlaAllelesListFail(hlaReportingData, hasReliablePurity));
    }

    @NotNull
    public static List<HlaAllele> sort(@NotNull List<HlaAllele> alleles) {
        return alleles.stream()
                .sorted(Comparator.comparing(HlaAllele::gene)
                        .thenComparing(HlaAllele::germlineAllele))
                .collect(Collectors.toList());
    }

    @NotNull
    public static List<HlaAlleleFail> sortFailed(@NotNull List<HlaAlleleFail> alleles) {
        return alleles.stream()
                .sorted(Comparator.comparing(HlaAlleleFail::gene)
                        .thenComparing(HlaAlleleFail::germlineAllele))
                .collect(Collectors.toList());
    }

    private static List<HlaAlleleSummary> createHlaAllelesListSummary(@NotNull HlaAllelesReportingData hlaReportingData) {
        List<HlaAlleleSummary> hlaAlleleSummaryList = Lists.newArrayList();
        Set<String> genes = Sets.newTreeSet(hlaReportingData.hlaAllelesReporting().keySet());

        for (String gene : genes) {
            List<HlaReporting> allele = hlaReportingData.hlaAllelesReporting().get(gene);

            Set<String> germlineAllele = Sets.newHashSet();

            for (HlaReporting hlaReporting : HLAAllele.sort(allele)) {
                germlineAllele.add(hlaReporting.hlaAllele().germlineAllele());
            }
            hlaAlleleSummaryList.add(HlaAlleleSummary.builder()
                    .gene(gene)
                    .germlineAlleles(germlineAllele)
                    .build());
        }
        return hlaAlleleSummaryList;
    }

    private static List<HlaAllele> createHlaAllelesList(@NotNull HlaAllelesReportingData hlaReportingData, boolean hasReliablePurity) {
        List<HlaAllele> hlaAlleleList = Lists.newArrayList();

        if (hlaReportingData.hlaQC().equals("PASS")) {
            Set<String> genes = Sets.newTreeSet(hlaReportingData.hlaAllelesReporting().keySet());
            for (String gene : genes) {
                List<HlaReporting> allele = hlaReportingData.hlaAllelesReporting().get(gene);

                for (HlaReporting hlaAlleleReporting : allele) {

                    hlaAlleleList.add(HlaAllele.builder()
                            .gene(gene)
                            .germlineAllele(hlaAlleleReporting.hlaAllele().germlineAllele())
                            .germlineCopies(roundCopyNumber(hlaAlleleReporting.germlineCopies(), hasReliablePurity))
                            .tumorCopies(roundCopyNumber(hlaAlleleReporting.tumorCopies(), hasReliablePurity))
                            .numberSomaticMutations(hlaAlleleReporting.somaticMutations())
                            .interpretationPresenceInTumor(hlaAlleleReporting.interpretation())
                            .build());
                }
            }
        }
        return hlaAlleleList;
    }

    private static List<HlaAlleleFail> createHlaAllelesListFail(@NotNull HlaAllelesReportingData hlaReportingData, boolean hasReliablePurity) {
        List<HlaAlleleFail> hlaAlleleList = Lists.newArrayList();

        if (hlaReportingData.hlaQC().equals("PASS")) {
            Set<String> genes = Sets.newTreeSet(hlaReportingData.hlaAllelesReporting().keySet());
            for (String gene : genes) {
                List<HlaReporting> allele = hlaReportingData.hlaAllelesReporting().get(gene);

                for (HlaReporting hlaAlleleReporting : allele) {

                    hlaAlleleList.add(HlaAlleleFail.builder()
                            .gene(gene)
                            .germlineAllele(hlaAlleleReporting.hlaAllele().germlineAllele())
                            .germlineCopies(roundCopyNumber(hlaAlleleReporting.germlineCopies(), hasReliablePurity))
                            .build());
                }
            }
        }
        return hlaAlleleList;
    }

    @NotNull
    public static String roundCopyNumber(Double copyNumber, boolean hasReliablePurity) {
        return hasReliablePurity && !copyNumber.isNaN() ? String.valueOf(Math.round(copyNumber)) : Formats.NA_STRING;
    }
}