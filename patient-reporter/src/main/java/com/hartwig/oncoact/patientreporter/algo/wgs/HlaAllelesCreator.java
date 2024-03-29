package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaReporting;
import com.hartwig.oncoact.patientreporter.cfreport.data.HLAAllele;
import com.hartwig.oncoact.patientreporter.model.HlaAllele;
import com.hartwig.oncoact.patientreporter.model.HlaAlleleSummary;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

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
        return createHlaAllelesList(hlaReportingData, hasReliablePurity);
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
        return hlaAlleleList;
    }

    @NotNull
    public static String roundCopyNumber(Double copyNumber, boolean hasReliablePurity) {
        return hasReliablePurity && !copyNumber.isNaN() ? String.valueOf(Math.round(copyNumber)) : Formats.NA_STRING;
    }
}