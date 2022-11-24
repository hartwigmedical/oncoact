package com.hartwig.oncoact.protect.serve;

import com.hartwig.oncoact.common.genome.refgenome.RefGenomeVersion;

import org.jetbrains.annotations.NotNull;

public final class ServeRefGenome {

    private ServeRefGenome() {
    }

    @NotNull
    public static com.hartwig.serve.datamodel.refgenome.RefGenomeVersion toServeRefGenome(@NotNull RefGenomeVersion refGenomeVersion) {
        switch (refGenomeVersion) {
            case V37: {
                return com.hartwig.serve.datamodel.refgenome.RefGenomeVersion.V37;
            }
            case V38: {
                return com.hartwig.serve.datamodel.refgenome.RefGenomeVersion.V38;
            }
        }
        throw new IllegalStateException("Could not convert ref genome version: " + refGenomeVersion);
    }
}
