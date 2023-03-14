package com.hartwig.oncoact.protect.serve;

import com.hartwig.oncoact.orange.OrangeRefGenomeVersion;
import com.hartwig.serve.datamodel.RefGenome;

import org.jetbrains.annotations.NotNull;

public final class ServeRefGenome {

    private ServeRefGenome() {
    }

    @NotNull
    public static RefGenome toServeRefGenome(@NotNull OrangeRefGenomeVersion refGenomeVersion) {
        switch (refGenomeVersion) {
            case V37: {
                return RefGenome.V37;
            }
            case V38: {
                return RefGenome.V38;
            }
        }
        throw new IllegalStateException("Could not convert ref genome version: " + refGenomeVersion);
    }
}