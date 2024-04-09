package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.hartwig.oncoact.disruption.GeneDisruption;
import com.hartwig.oncoact.patientreporter.cfreport.data.GeneUtil;
import com.hartwig.oncoact.patientreporter.model.ObservedGeneDisruption;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

class GeneDisruptionCreator {

    static List<ObservedGeneDisruption> createGeneDisruption(
            @NotNull List<GeneDisruption> geneDisruptions,
            boolean hasReliablePurity
    ) {
        List<ObservedGeneDisruption> observedGeneDisruptions = Lists.newArrayList();
        for (GeneDisruption geneDisruption : sort(geneDisruptions)) {
            observedGeneDisruptions.add(ObservedGeneDisruption.builder()
                    .location(geneDisruption.location())
                    .gene(geneDisruption.gene())
                    .disruptedRange(geneDisruption.range())
                    .disruptionType(geneDisruption.type())
                    .clusterId(geneDisruption.clusterId())
                    .disruptedCopies(roundCopyNumber(geneDisruption.junctionCopyNumber(), hasReliablePurity))
                    .undisruptedCopies(roundCopyNumber(geneDisruption.undisruptedCopyNumber(), hasReliablePurity))
                    .build());
        }
        return observedGeneDisruptions;
    }

    @NotNull
    public static List<GeneDisruption> sort(@NotNull List<GeneDisruption> disruptions) {
        return disruptions.stream().sorted((disruption1, disruption2) -> {
            String locationAndGene1 = GeneUtil.zeroPrefixed(disruption1.location()) + disruption1.gene();
            String locationAndGene2 = GeneUtil.zeroPrefixed(disruption2.location()) + disruption2.gene();

            if (locationAndGene1.equals(locationAndGene2)) {
                return disruption1.firstAffectedExon() - disruption2.firstAffectedExon();
            } else {
                return locationAndGene1.compareTo(locationAndGene2);
            }
        }).collect(Collectors.toList());
    }

    @NotNull
    public static String roundCopyNumber(Double copyNumber, boolean hasReliablePurity) {
        return hasReliablePurity && !copyNumber.isNaN() ? String.valueOf(Math.round(copyNumber)) : Formats.NA_STRING;
    }
}