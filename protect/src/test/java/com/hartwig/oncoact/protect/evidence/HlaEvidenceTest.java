package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.orange.datamodel.lilac.ImmutableLilacRecord;
import com.hartwig.oncoact.orange.datamodel.lilac.LilacHlaAllele;
import com.hartwig.oncoact.orange.datamodel.lilac.LilacRecord;
import com.hartwig.oncoact.orange.datamodel.lilac.TestLilacFactory;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.TestServeFactory;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ImmutableActionableHLA;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class HlaEvidenceTest {

    @Test
    public void canDetermineEvidenceForHLA() {
        ActionableHLA hla = ImmutableActionableHLA.builder()
                .from(TestServeFactory.createTestActionableHLA())
                .hlaAllele("Allele 1")
                .build();

        HlaEvidence hlaEvidence =
                new HlaEvidence(TestPersonalizedEvidenceFactory.create(), Lists.newArrayList(hla));

        LilacRecord lilacRecordActionable = createTestLilacRecord("Allele 1");
        List<ProtectEvidence> evidenceActionable = hlaEvidence.evidence(lilacRecordActionable);
        assertEquals(1, evidenceActionable.size());

        LilacRecord lilacRecordNonActionable = createTestLilacRecord("Allele 2");
        List<ProtectEvidence> evidenceNonActionable = hlaEvidence.evidence(lilacRecordNonActionable);
        assertEquals(0, evidenceNonActionable.size());
    }

    @NotNull
    private static LilacRecord createTestLilacRecord(@NotNull String hlaType) {
        List<LilacHlaAllele> alleles = Lists.newArrayList();
        alleles.add(TestLilacFactory.builder().allele(hlaType).build());

        return ImmutableLilacRecord.builder().qc("PASS").alleles(alleles).build();
    }
}