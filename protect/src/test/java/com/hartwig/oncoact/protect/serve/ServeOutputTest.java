package com.hartwig.oncoact.protect.serve;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.ActionableEventsLoader;
import com.hartwig.serve.datamodel.RefGenome;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class ServeOutputTest {

    private static final String TEST_ACTIONABILITY_DIR = Resources.getResource("serve").getPath();

    @Test
    public void canLoadFromTestDir() throws IOException {
        assertNotNull(ActionableEventsLoader.readFromDir(TEST_ACTIONABILITY_DIR, RefGenome.V37));
    }
}