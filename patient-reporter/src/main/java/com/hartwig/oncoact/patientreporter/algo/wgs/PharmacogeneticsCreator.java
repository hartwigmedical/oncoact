package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.oncoact.patientreporter.model.PharmacogeneticsGenotype;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

class PharmacogeneticsCreator {

    static List<PharmacogeneticsGenotype> createPharmacogeneticsGenotype(
            @NotNull Map<String, List<PeachGenotype>> pharmacogeneticsGenotypesMap
    ) {
        return createPgxList(pharmacogeneticsGenotypesMap);
    }

    private static List<PharmacogeneticsGenotype> createPgxList(Map<String, List<PeachGenotype>> pharmacogeneticsGenotypesMap) {
        List<PharmacogeneticsGenotype> pharmacogeneticsGenotypeList = Lists.newArrayList();
        Set<String> genes = Sets.newTreeSet(pharmacogeneticsGenotypesMap.keySet());
        for (String gene : genes) {
            List<PeachGenotype> pgxList = pharmacogeneticsGenotypesMap.get(gene);

            Set<String> function = Sets.newHashSet();

            for (PeachGenotype pgx : pgxList) {
                function.add(pgx.function());
            }
            pharmacogeneticsGenotypeList.add(PharmacogeneticsGenotype.builder()
                    .gene(gene)
                    .functions(function)
                    .build());
        }
        return pharmacogeneticsGenotypeList;
    }
}