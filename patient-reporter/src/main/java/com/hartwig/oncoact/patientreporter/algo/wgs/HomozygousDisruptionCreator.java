package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneUtil;
import com.hartwig.oncoact.patientreporter.model.ObservedHomozygousDisruption;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

class HomozygousDisruptionCreator {

    static List<ObservedHomozygousDisruption> createHomozygousDisruption(
            @NotNull List<HomozygousDisruption> homozygousDisruptions
    ) {
        List<ObservedHomozygousDisruption> observedHomozygousDisruptions = Lists.newArrayList();
        for (HomozygousDisruption homozygousDisruption : sort(homozygousDisruptions)) {
            observedHomozygousDisruptions.add(ObservedHomozygousDisruption.builder()
                    .chromosome(homozygousDisruption.chromosome())
                    .region(homozygousDisruption.chromosomeBand())
                    .gene(homozygousDisruption.gene())
                    .build());
        }
        return observedHomozygousDisruptions;
    }

    @NotNull
    public static List<HomozygousDisruption> sort(@NotNull List<HomozygousDisruption> homozygousDisruptions) {
        return homozygousDisruptions.stream().sorted((disruption1, disruption2) -> {
            String location1 = GeneUtil.zeroPrefixed(disruption1.chromosome() + disruption1.chromosomeBand());
            String location2 = GeneUtil.zeroPrefixed(disruption2.chromosome() + disruption2.chromosomeBand());

            if (location1.equals(location2)) {
                return disruption1.gene().compareTo(disruption2.gene());
            } else {
                return location1.compareTo(location2);
            }
        }).collect(Collectors.toList());
    }
}