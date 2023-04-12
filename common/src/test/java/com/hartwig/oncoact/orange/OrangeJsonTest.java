package com.hartwig.oncoact.orange;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.hmftools.datamodel.orange.OrangePlots;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class OrangeJsonTest {
    private static final String MINIMALLY_POPULATED_ORANGE_JSON = Resources.getResource("orange/minimally.populated.orange.json").getPath();

    @Test
    public void canReadMinimallyPopulatedOrangeRecordJson() throws IOException {
        OrangeRecord record = OrangeJson.read(MINIMALLY_POPULATED_ORANGE_JSON);
        assertPlots(record.plots());
    }

    private static void assertPlots(@NotNull OrangePlots plots) {
        assertTrue(new File(plots.purpleFinalCircosPlot()).exists());
    }
}