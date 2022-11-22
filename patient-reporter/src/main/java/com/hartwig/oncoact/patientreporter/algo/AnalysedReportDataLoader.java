package com.hartwig.oncoact.patientreporter.algo;

import java.io.IOException;

import com.hartwig.oncoact.common.fusion.KnownFusionCache;
import com.hartwig.oncoact.patientreporter.ReportData;
import com.hartwig.oncoact.patientreporter.germline.GermlineReportingFile;
import com.hartwig.oncoact.patientreporter.germline.GermlineReportingModel;
import com.hartwig.oncoact.patientreporter.remarks.SpecialRemarkFile;
import com.hartwig.oncoact.patientreporter.remarks.SpecialRemarkModel;

import org.jetbrains.annotations.NotNull;

public final class AnalysedReportDataLoader {

    private AnalysedReportDataLoader() {
    }

    @NotNull
    public static AnalysedReportData buildFromFiles(@NotNull ReportData reportData, @NotNull String germlineReportingTsv,
             @NotNull String sampleSpecialRemarkTsv, @NotNull String knownFusionFile) throws IOException {
        GermlineReportingModel germlineReportingModel = GermlineReportingFile.buildFromTsv(germlineReportingTsv);
        SpecialRemarkModel specialRemarkModel = SpecialRemarkFile.buildFromTsv(sampleSpecialRemarkTsv);

        KnownFusionCache knownFusionCache = new KnownFusionCache();
        if (!knownFusionCache.loadFile(knownFusionFile)) {
            throw new IOException("Could not load known fusions from " + knownFusionFile);
        }

        return ImmutableAnalysedReportData.builder()
                .from(reportData)
                .germlineReportingModel(germlineReportingModel)
                .specialRemarkModel(specialRemarkModel)
                .knownFusionCache(knownFusionCache)
                .build();
    }
}
