package com.hartwig.oncoact.patientreporter.algo.orange;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.orange.chord.ChordStatus;
import com.hartwig.oncoact.orange.purple.PurpleGeneCopyNumber;
import com.hartwig.oncoact.orange.purple.PurpleMicrosatelliteStatus;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;
import com.hartwig.oncoact.patientreporter.algo.LohGenesReporting;
import com.hartwig.oncoact.patientreporter.util.Genes;

import org.junit.Test;

public class LossOfHeterozygositySelectorTest {

    private static final double EPSILON = 1.0E-10;

    @Test
    public void canSelectGenesForLOH() {
        String hrdGene = Genes.HRD_GENES.iterator().next();
        PurpleGeneCopyNumber hrdGeneWithLOH =
                TestPurpleFactory.geneCopyNumberBuilder().gene(hrdGene).minMinorAlleleCopyNumber(0D).minCopyNumber(1D).build();
        PurpleGeneCopyNumber hrdGeneWithoutLOH =
                TestPurpleFactory.geneCopyNumberBuilder().gene(hrdGene).minMinorAlleleCopyNumber(1D).minCopyNumber(2D).build();

        String msiGene = Genes.MSI_GENES.iterator().next();
        PurpleGeneCopyNumber msiGeneWithLOH =
                TestPurpleFactory.geneCopyNumberBuilder().gene(msiGene).minMinorAlleleCopyNumber(0D).minCopyNumber(1D).build();
        PurpleGeneCopyNumber msiGeneWithoutLOH =
                TestPurpleFactory.geneCopyNumberBuilder().gene(msiGene).minMinorAlleleCopyNumber(1D).minCopyNumber(2D).build();

        PurpleGeneCopyNumber otherGeneWithLOH =
                TestPurpleFactory.geneCopyNumberBuilder().gene("other").minMinorAlleleCopyNumber(0D).minCopyNumber(1D).build();

        List<PurpleGeneCopyNumber> allGeneCopyNumbers =
                Lists.newArrayList(hrdGeneWithLOH, hrdGeneWithoutLOH, msiGeneWithLOH, msiGeneWithoutLOH, otherGeneWithLOH);

        List<LohGenesReporting> msiOnly =
                LossOfHeterozygositySelector.selectMSIGenesWithLOH(allGeneCopyNumbers, PurpleMicrosatelliteStatus.MSI);
        assertEquals(1, msiOnly.size());
        assertEquals(msiGene, msiOnly.get(0).gene());
        assertEquals(0D, msiOnly.get(0).minorAlleleCopies(), EPSILON);
        assertEquals(1D, msiOnly.get(0).tumorCopies(), EPSILON);

        List<LohGenesReporting> hrdOnly = LossOfHeterozygositySelector.selectHRDGenesWithLOH(allGeneCopyNumbers, ChordStatus.HR_DEFICIENT);
        assertEquals(1, hrdOnly.size());
        assertEquals(hrdGene, hrdOnly.get(0).gene());
        assertEquals(0D, msiOnly.get(0).minorAlleleCopies(), EPSILON);
        assertEquals(1D, msiOnly.get(0).tumorCopies(), EPSILON);

        List<LohGenesReporting> noneHRD = LossOfHeterozygositySelector.selectHRDGenesWithLOH(allGeneCopyNumbers, ChordStatus.HR_PROFICIENT);
        assertEquals(0, noneHRD.size());

        List<LohGenesReporting> noneMSI =
                LossOfHeterozygositySelector.selectMSIGenesWithLOH(allGeneCopyNumbers, PurpleMicrosatelliteStatus.MSS);
        assertEquals(0, noneMSI.size());
    }
}