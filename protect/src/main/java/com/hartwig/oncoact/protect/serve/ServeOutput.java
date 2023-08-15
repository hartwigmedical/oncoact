package com.hartwig.oncoact.protect.serve;

import java.io.IOException;

import com.hartwig.hmftools.datamodel.orange.OrangeRefGenomeVersion;
import com.hartwig.oncoact.protect.ProtectConfig;
import com.hartwig.serve.datamodel.ActionableEvents;
import com.hartwig.serve.datamodel.ActionableEventsLoader;

import org.jetbrains.annotations.NotNull;

public final class ServeOutput {

    private ServeOutput() {
    }

    @NotNull
    public static ActionableEvents loadServeData(@NotNull ProtectConfig config, @NotNull OrangeRefGenomeVersion refGenomeVersion)
            throws IOException {
        return ActionableEventsLoader.readFromDir(config.serveActionabilityDir(), ServeRefGenome.toServeRefGenome(refGenomeVersion));
    }
}