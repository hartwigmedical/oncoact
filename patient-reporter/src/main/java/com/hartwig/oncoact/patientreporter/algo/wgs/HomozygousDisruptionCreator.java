package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.oncoact.patientreporter.model.ObservedHomozygousDisruption;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class HomozygousDisruptionCreator {

    static List<ObservedHomozygousDisruption> createHomozygousDisruption(
            @NotNull List<HomozygousDisruption> homozygousDisruptions
    ) {
        List<ObservedHomozygousDisruption> observedHomozygousDisruptions = Lists.newArrayList();
        for (HomozygousDisruption homozygousDisruption : homozygousDisruptions) {
            observedHomozygousDisruptions.add(ObservedHomozygousDisruption.builder()
                    .chromosome(homozygousDisruption.chromosome())
                    .region(homozygousDisruption.chromosomeBand())
                    .gene(homozygousDisruption.gene())
                    .build());
        }
        return observedHomozygousDisruptions;
    }
}