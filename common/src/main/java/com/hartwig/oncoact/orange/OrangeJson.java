package com.hartwig.oncoact.orange;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.hartwig.hmftools.datamodel.orange.ImmutableOrangePlots;
import com.hartwig.hmftools.datamodel.orange.ImmutableOrangeRecord;
import com.hartwig.hmftools.datamodel.orange.OrangePlots;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;

import org.jetbrains.annotations.NotNull;

public final class OrangeJson {

    private OrangeJson() {
    }

    @NotNull
    public static OrangeRecord read(@NotNull String orangeJsonPathName) throws IOException {
        Path orangePath = new File(orangeJsonPathName).toPath();
        OrangeRecord orange = new Gson().fromJson(Files.readString(orangePath), OrangeRecord.class);
        return fixPlotPaths(orange, orangePath.getParent().toString());
    }

    @NotNull
    private static OrangeRecord fixPlotPaths(@NotNull OrangeRecord orange, @NotNull String orangeBasePath) {
        // All ORANGE plots are relative to the base path of ORANGE JSON.
        OrangePlots fixedPlots = ImmutableOrangePlots.builder()
                .purpleFinalCircosPlot(orangeBasePath + File.separator + orange.plots().purpleFinalCircosPlot())
                .build();
        return ImmutableOrangeRecord.builder().from(orange).plots(fixedPlots).build();
    }
}
