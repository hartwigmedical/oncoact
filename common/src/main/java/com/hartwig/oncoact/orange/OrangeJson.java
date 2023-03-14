package com.hartwig.oncoact.orange;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ServiceLoader;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
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
        var gsonBuilder = new GsonBuilder();
        for (TypeAdapterFactory factory : ServiceLoader.load(TypeAdapterFactory.class)) {
            gsonBuilder.registerTypeAdapterFactory(factory);
        }
        var gson = gsonBuilder.create();
        OrangeRecord orange = gson.fromJson(Files.readString(orangePath), OrangeRecord.class);
        return fixPlotPaths(orange, orangePath.getParent().toString());
    }

    @NotNull
    private static OrangeRecord fixPlotPaths(@NotNull OrangeRecord orange, @NotNull String orangeBasePath) {
        // All ORANGE plots are relative to the base path of ORANGE JSON.
        OrangePlots fixedPlots = ImmutableOrangePlots.builder()
                .purpleFinalCircosPlot(getPlotPath(orangeBasePath, orange.plots().purpleFinalCircosPlot()))
                .sageTumorBQRPlot(getPlotPath(orangeBasePath, orange.plots().sageTumorBQRPlot()))
                .purpleInputPlot(getPlotPath(orangeBasePath, orange.plots().purpleInputPlot()))
                .purpleClonalityPlot(getPlotPath(orangeBasePath, orange.plots().purpleClonalityPlot()))
                .purpleCopyNumberPlot(getPlotPath(orangeBasePath, orange.plots().purpleCopyNumberPlot()))
                .purpleVariantCopyNumberPlot(getPlotPath(orangeBasePath, orange.plots().purpleVariantCopyNumberPlot()))
                .purplePurityRangePlot(getPlotPath(orangeBasePath, orange.plots().purplePurityRangePlot()))
                .build();
        return ImmutableOrangeRecord.builder().from(orange).plots(fixedPlots).build();
    }

    @NotNull
    private static String getPlotPath(final @NotNull String orangeBasePath, final @NotNull String plotPath) {
        return orangeBasePath + File.separator + plotPath;
    }
}
