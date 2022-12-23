package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.hartwig.oncoact.orange.linx.LinxHomozygousDisruption;
import com.hartwig.oncoact.patientreporter.algo.CurationFunction;

import org.jetbrains.annotations.NotNull;

public final class HomozygousDisruptions {

    private HomozygousDisruptions() {
    }

    @NotNull
    public static List<LinxHomozygousDisruption> sort(@NotNull List<LinxHomozygousDisruption> homozygousDisruptions) {
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

    @NotNull
    public static Set<String> disruptedGenes(@NotNull List<LinxHomozygousDisruption> homozygousDisruptions) {
        Set<String> genes = new TreeSet<String>();
        for (LinxHomozygousDisruption disruption : homozygousDisruptions) {
            genes.add(CurationFunction.curateGeneNamePdf(disruption.gene()));
        }
        return genes;
    }
}
