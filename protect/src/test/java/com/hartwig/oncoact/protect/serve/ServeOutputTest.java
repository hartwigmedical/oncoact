package com.hartwig.oncoact.protect.serve;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.ActionableEventsLoader;
import com.hartwig.serve.datamodel.RefGenome;

import org.junit.Test;

public class ServeOutputTest {

    private static final String TEST_ACTIONABILITY_DIR = Resources.getResource("serve").getPath();

    @Test
    public void canLoadFromTestDir() throws IOException {
        assertNotNull(ActionableEventsLoader.readFromDir(TEST_ACTIONABILITY_DIR, RefGenome.V37));
    }
}