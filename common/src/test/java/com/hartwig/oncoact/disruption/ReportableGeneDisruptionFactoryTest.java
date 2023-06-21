package com.hartwig.oncoact.disruption;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.linx.HomozygousDisruption;
import com.hartwig.oncoact.orange.linx.TestLinxFactory;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReportableGeneDisruptionFactoryTest {

    @Test
    public void canTestSomaticHomozygousDisruption() {
        HomozygousDisruption somaticHomozygousDisruption = TestLinxFactory.homozygousDisruptionBuilder().gene("TP53").build();
        List<HomozygousDisruption> homozygousDisruption = Lists.newArrayList(somaticHomozygousDisruption);
        assertEquals(1, homozygousDisruption.size());
    }

    @Test
    public void canTestGermlineSomaticHomozygousDisruption() {
        HomozygousDisruption somaticHomozygousDisruption = TestLinxFactory.homozygousDisruptionBuilder().gene("BRAF").build();
        HomozygousDisruption germlineHomozygousDisruption = TestLinxFactory.homozygousDisruptionBuilder().gene("KRAS").build();
        List<HomozygousDisruption> homozygousDisruption = Lists.newArrayList(somaticHomozygousDisruption, germlineHomozygousDisruption);
        assertEquals(2, homozygousDisruption.size());
    }

}