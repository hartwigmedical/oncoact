package com.hartwig.oncoact.protect.serve;

import static org.junit.Assert.assertNotNull;

import com.hartwig.oncoact.common.orange.datamodel.OrangeRefGenomeVersion;

import org.junit.Test;

public class ServeRefGenomeTest {

    @Test
    public void canConvertAllRefGenomes() {
        for (OrangeRefGenomeVersion refGenomeVersion : OrangeRefGenomeVersion.values()) {
            assertNotNull(ServeRefGenome.toServeRefGenome(refGenomeVersion));
        }
    }
}