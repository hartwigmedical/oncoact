package com.hartwig.oncoact.protect.evidence;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.common.hla.ImmutableLilacSummaryData;
import com.hartwig.oncoact.common.hla.LilacAllele;
import com.hartwig.oncoact.common.hla.LilacSummaryData;
import com.hartwig.oncoact.common.lilac.LilacTestFactory;
import com.hartwig.oncoact.common.protect.ProtectEvidence;
import com.hartwig.oncoact.protect.ServeTestFactory;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ImmutableActionableHLA;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class HlaEvidenceTest {

    @Test
    public void canDetermineEvidenceForHLA() {
        ActionableHLA hla = ImmutableActionableHLA.builder()
                .from(ServeTestFactory.createTestActionableHLA())
                .hlaAllele("Allele 1")
                .build();

        HlaEvidence hlaEvidence =
                new HlaEvidence(EvidenceTestFactory.create(), Lists.newArrayList(hla));

        LilacSummaryData lilacDataActionable = createTestLilacData("Allele 1");
        List<ProtectEvidence> evidenceActionable = hlaEvidence.evidence(lilacDataActionable);
        assertEquals(1, evidenceActionable.size());

        LilacSummaryData lilacDataNonActionable = createTestLilacData("Allele 2");
        List<ProtectEvidence> evidenceNonActionable = hlaEvidence.evidence(lilacDataNonActionable);
        assertEquals(0, evidenceNonActionable.size());
    }

    @NotNull
    private static LilacSummaryData createTestLilacData(@NotNull String hlaType) {
        List<LilacAllele> alleles = Lists.newArrayList();
        alleles.add(LilacTestFactory.builder().allele(hlaType).build());

        return ImmutableLilacSummaryData.builder().qc("PASS").alleles(alleles).build();
    }
}