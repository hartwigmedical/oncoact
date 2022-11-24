package com.hartwig.oncoact.protect.serve;

import static org.junit.Assert.assertNotNull;

import com.hartwig.oncoact.common.genome.refgenome.RefGenomeVersion;

import org.junit.Test;

public class ServeRefGenomeTest {

    @Test
    public void canConvertAllRefGenomes() {
        for (RefGenomeVersion refGenomeVersion : RefGenomeVersion.values()) {
            assertNotNull(ServeRefGenome.toServeRefGenome(refGenomeVersion));
        }
    }
}