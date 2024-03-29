package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.hartwig.oncoact.disruption.GeneDisruption;
import com.hartwig.oncoact.patientreporter.model.ObservedGeneDisruption;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class GeneDisruptionCreator {

    static List<ObservedGeneDisruption> createGeneDisruption(
            @NotNull List<GeneDisruption> geneDisruptions,
            boolean hasReliablePurity
    ) {
        List<ObservedGeneDisruption> observedGeneDisruptions = Lists.newArrayList();
        for (GeneDisruption geneDisruption : geneDisruptions) {
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
    public static String roundCopyNumber(Double copyNumber, boolean hasReliablePurity) {
        return hasReliablePurity && !copyNumber.isNaN() ? String.valueOf(Math.round(copyNumber)) : Formats.NA_STRING;
    }
}