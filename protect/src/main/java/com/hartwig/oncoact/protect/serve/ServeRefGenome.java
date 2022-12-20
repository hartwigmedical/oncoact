package com.hartwig.oncoact.protect.serve;

import com.hartwig.oncoact.orange.OrangeRefGenomeVersion;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;

import org.jetbrains.annotations.NotNull;

public final class ServeRefGenome {

    private ServeRefGenome() {
    }

    @NotNull
    public static RefGenomeVersion toServeRefGenome(@NotNull OrangeRefGenomeVersion refGenomeVersion) {
        switch (refGenomeVersion) {
            case V37: {
                return RefGenomeVersion.V37;
            }
            case V38: {
                return RefGenomeVersion.V38;
            }
        }
        throw new IllegalStateException("Could not convert ref genome version: " + refGenomeVersion);
    }
}
