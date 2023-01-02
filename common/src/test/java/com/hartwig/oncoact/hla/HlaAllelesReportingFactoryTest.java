package com.hartwig.oncoact.hla;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.orange.lilac.ImmutableLilacRecord;
import com.hartwig.oncoact.orange.lilac.LilacHlaAllele;
import com.hartwig.oncoact.orange.lilac.LilacRecord;
import com.hartwig.oncoact.orange.lilac.TestLilacFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class HlaAllelesReportingFactoryTest {

    private static final double EPSILON = 1.0E-10;

    @Test
    public void testGene() {
        Map<String, List<LilacHlaAllele>> mapLilac = HlaAllelesReportingFactory.generateLilacMap(createTestLilacRecord());
        assertEquals(HlaAllelesReportingFactory.extractHLAGene(mapLilac.get("A*03:01").get(0)), "HLA-A");
        assertEquals(HlaAllelesReportingFactory.extractHLAGene(mapLilac.get("B*18:02").get(0)), "HLA-B");
        assertEquals(HlaAllelesReportingFactory.extractHLAGene(mapLilac.get("B*35:02").get(0)), "HLA-B");
        assertEquals(HlaAllelesReportingFactory.extractHLAGene(mapLilac.get("C*10:12").get(0)), "HLA-C");
        assertEquals(HlaAllelesReportingFactory.extractHLAGene(mapLilac.get("C*16:02").get(0)), "HLA-C");
    }

    @Test
    public void testMutationString() {
        Map<String, List<LilacHlaAllele>> mapLilac = HlaAllelesReportingFactory.generateLilacMap(createTestLilacRecord());
        assertEquals(HlaAllelesReportingFactory.mutationString(mapLilac.get("A*03:01").get(0)), "2 missense");
        assertEquals(HlaAllelesReportingFactory.mutationString(mapLilac.get("B*18:02").get(0)), "1 nonsense or frameshift, 1 splice");
        assertEquals(HlaAllelesReportingFactory.mutationString(mapLilac.get("B*35:02").get(0)), "No");
        assertEquals(HlaAllelesReportingFactory.mutationString(mapLilac.get("C*10:12").get(0)), "No");
        assertEquals(HlaAllelesReportingFactory.mutationString(mapLilac.get("C*16:02").get(0)), "1 missense");
    }

    @Test
    public void testReliableInterpretation() {
        Map<String, List<LilacHlaAllele>> mapLilac = HlaAllelesReportingFactory.generateLilacMap(createTestLilacRecord());
        assertEquals(HlaAllelesReportingFactory.HLAPresenceInTumor(mapLilac.get("A*03:01").get(0),
                HlaAllelesReportingFactory.mutationString(mapLilac.get("A*03:01").get(0)),
                true), "Yes, but mutation(s) detected");
        assertEquals(HlaAllelesReportingFactory.HLAPresenceInTumor(mapLilac.get("B*18:02").get(0),
                HlaAllelesReportingFactory.mutationString(mapLilac.get("B*18:02").get(0)),
                true), "Yes, but mutation(s) detected");
        assertEquals(HlaAllelesReportingFactory.HLAPresenceInTumor(mapLilac.get("B*35:02").get(0),
                HlaAllelesReportingFactory.mutationString(mapLilac.get("B*35:02").get(0)),
                true), "Yes");
        assertEquals(HlaAllelesReportingFactory.HLAPresenceInTumor(mapLilac.get("C*10:12").get(0),
                HlaAllelesReportingFactory.mutationString(mapLilac.get("C*10:12").get(0)),
                true), "No");
        assertEquals(HlaAllelesReportingFactory.HLAPresenceInTumor(mapLilac.get("C*16:02").get(0),
                HlaAllelesReportingFactory.mutationString(mapLilac.get("C*16:02").get(0)),
                true), "Unknown");
    }

    @Test
    public void testUnreliableInterpretation() {
        Map<String, List<LilacHlaAllele>> mapLilac = HlaAllelesReportingFactory.generateLilacMap(createTestLilacRecord());
        assertEquals(HlaAllelesReportingFactory.HLAPresenceInTumor(mapLilac.get("A*03:01").get(0),
                HlaAllelesReportingFactory.mutationString(mapLilac.get("A*03:01").get(0)),
                false), "Unknown");
        assertEquals(HlaAllelesReportingFactory.HLAPresenceInTumor(mapLilac.get("B*18:02").get(0),
                HlaAllelesReportingFactory.mutationString(mapLilac.get("B*18:02").get(0)),
                false), "Unknown");
        assertEquals(HlaAllelesReportingFactory.HLAPresenceInTumor(mapLilac.get("B*35:02").get(0),
                HlaAllelesReportingFactory.mutationString(mapLilac.get("B*35:02").get(0)),
                false), "Unknown");
        assertEquals(HlaAllelesReportingFactory.HLAPresenceInTumor(mapLilac.get("C*10:12").get(0),
                HlaAllelesReportingFactory.mutationString(mapLilac.get("C*10:12").get(0)),
                false), "Unknown");
        assertEquals(HlaAllelesReportingFactory.HLAPresenceInTumor(mapLilac.get("C*16:02").get(0),
                HlaAllelesReportingFactory.mutationString(mapLilac.get("C*16:02").get(0)),
                false), "Unknown");
    }

    @Test
    public void testConvertDataReliable() {
        HlaAllelesReportingData lilacReportingData = HlaAllelesReportingFactory.convertToReportData(createTestLilacRecord(), true);
        Map<String, List<HlaReporting>> lilacReporting = lilacReportingData.hlaAllelesReporting();

        HlaReporting lilacReporting1 = extractHlaReporting("A*03:01", lilacReporting.get("HLA-A"));
        assertEquals(lilacReporting1.hlaAllele().gene(), "HLA-A");
        assertEquals(lilacReporting1.hlaAllele().germlineAllele(), "A*03:01");
        assertEquals(lilacReporting1.somaticMutations(), "2 missense");
        assertEquals(lilacReporting1.interpretation(), "Yes, but mutation(s) detected");
        assertEquals(lilacReporting1.tumorCopies(), 6, 2);
        assertEquals(lilacReporting1.germlineCopies(), 2D, EPSILON);

        HlaReporting lilacReporting2 = extractHlaReporting("B*18:02", lilacReporting.get("HLA-B"));
        assertEquals(lilacReporting2.hlaAllele().gene(), "HLA-B");
        assertEquals(lilacReporting2.hlaAllele().germlineAllele(), "B*18:02");
        assertEquals(lilacReporting2.somaticMutations(), "1 nonsense or frameshift, 1 splice");
        assertEquals(lilacReporting2.interpretation(), "Yes, but mutation(s) detected");
        assertEquals(lilacReporting2.tumorCopies(), 1.2, EPSILON);
        assertEquals(lilacReporting2.germlineCopies(), 1D, EPSILON);

        HlaReporting lilacReporting3 = extractHlaReporting("B*35:02", lilacReporting.get("HLA-B"));
        assertEquals(lilacReporting3.hlaAllele().gene(), "HLA-B");
        assertEquals(lilacReporting3.hlaAllele().germlineAllele(), "B*35:02");
        assertEquals(lilacReporting3.somaticMutations(), "No");
        assertEquals(lilacReporting3.interpretation(), "Yes");
        assertEquals(lilacReporting3.tumorCopies(), 1.1, EPSILON);
        assertEquals(lilacReporting3.germlineCopies(), 1D, EPSILON);

        HlaReporting lilacReporting4 = extractHlaReporting("C*10:12", lilacReporting.get("HLA-C"));
        assertEquals(lilacReporting4.hlaAllele().gene(), "HLA-C");
        assertEquals(lilacReporting4.hlaAllele().germlineAllele(), "C*10:12");
        assertEquals(lilacReporting4.somaticMutations(), "No");
        assertEquals(lilacReporting4.interpretation(), "No");
        assertEquals(lilacReporting4.tumorCopies(), 0D, EPSILON);
        assertEquals(lilacReporting4.germlineCopies(), 1D, EPSILON);

        HlaReporting lilacReporting5 = extractHlaReporting("C*16:02", lilacReporting.get("HLA-C"));
        assertEquals(lilacReporting5.hlaAllele().gene(), "HLA-C");
        assertEquals(lilacReporting5.hlaAllele().germlineAllele(), "C*16:02");
        assertEquals(lilacReporting5.somaticMutations(), "1 missense");
        assertEquals(lilacReporting5.interpretation(), "Unknown");
        assertEquals(lilacReporting5.tumorCopies(), 0D, EPSILON);
        assertEquals(lilacReporting5.germlineCopies(), 1D, EPSILON);
    }

    @NotNull
    private static HlaReporting extractHlaReporting(@NotNull String germlineAllele, @NotNull List<HlaReporting> lilacReportingData) {
        for (HlaReporting hlaReporting : lilacReportingData) {
            if (hlaReporting.hlaAllele().germlineAllele().equals(germlineAllele)) {
                return hlaReporting;
            }
        }
        throw new IllegalStateException("Could not find lilac reporting: " + germlineAllele);
    }

    @NotNull
    private static LilacRecord createTestLilacRecord() {
        List<LilacHlaAllele> alleles = Lists.newArrayList();
        alleles.add(TestLilacFactory.builder().allele("A*03:01").somaticMissense(2D).tumorCopyNumber(4.7).build());
        alleles.add(TestLilacFactory.builder().allele("A*03:01").somaticMissense(0D).tumorCopyNumber(1.5).build());
        alleles.add(TestLilacFactory.builder()
                .allele("B*18:02")
                .somaticSplice(1D)
                .tumorCopyNumber(1.2)
                .somaticNonsenseOrFrameshift(1D)
                .build());
        alleles.add(TestLilacFactory.builder().allele("B*35:02").tumorCopyNumber(1.1).build());
        alleles.add(TestLilacFactory.builder().allele("C*10:12").somaticSynonymous(1D).tumorCopyNumber(0).build());
        alleles.add(TestLilacFactory.builder().allele("C*16:02").tumorCopyNumber(0).somaticMissense(1D).build());

        return ImmutableLilacRecord.builder().qc("PASS").alleles(alleles).build();
    }
}