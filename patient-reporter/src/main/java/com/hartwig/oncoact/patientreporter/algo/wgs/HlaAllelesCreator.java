package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaReporting;
import com.hartwig.oncoact.patientreporter.cfreport.data.HLAAllele;
import com.hartwig.oncoact.patientreporter.model.HlaAlleleSummary;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

class HlaAllelesCreator {
    static List<HlaAlleleSummary> createHlaAlleles(
            @NotNull HlaAllelesReportingData hlaReportingData
    ) {
        return createHlaAllelesList(hlaReportingData);
    }

    private static List<HlaAlleleSummary> createHlaAllelesList(@NotNull HlaAllelesReportingData hlaReportingData) {
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
}