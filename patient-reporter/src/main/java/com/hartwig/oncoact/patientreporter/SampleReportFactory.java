package com.hartwig.oncoact.patientreporter;

import java.time.LocalDate;

import com.hartwig.lama.client.model.PatientReporterData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SampleReportFactory {

    private static final Logger LOGGER = LogManager.getLogger(SampleReportFactory.class);

    private SampleReportFactory() {
    }

    @NotNull
    public static SampleReport fromLimsModel(@NotNull SampleMetadata sampleMetadata,
                                             @Nullable PatientReporterData patientReporterData, boolean allowDefaultCohortConfig) {
        String tumorReceivedSampleId = patientReporterData.getTumorSampleBarcode();
        String referenceReceivedSampleId = patientReporterData.getReferenceSampleBarcode();


        return ImmutableSampleReport.builder()
                .sampleMetadata(sampleMetadata)
                .tumorReceivedSampleId(tumorReceivedSampleId)
                .referenceReceivedSampleId(referenceReceivedSampleId)
                .build();
    }
}
