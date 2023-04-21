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

    @Nullable
    public static String interpretRefBarcode(@Nullable String refSampleBarcode) {
        String interpretRefSampleBarcode;
        if (refSampleBarcode != null) {
            if (refSampleBarcode.contains("-")) {
                interpretRefSampleBarcode = refSampleBarcode.split("-")[0];
            } else if (refSampleBarcode.contains("_")) {
                interpretRefSampleBarcode = refSampleBarcode.split("_")[0];
            } else {
                interpretRefSampleBarcode = refSampleBarcode;
            }
        } else {
            interpretRefSampleBarcode = refSampleBarcode;
        }
        return interpretRefSampleBarcode;
    }

    @NotNull
    public static SampleReport fromLimsModel(@NotNull SampleMetadata sampleMetadata,
                                             @Nullable PatientReporterData patientReporterData, boolean allowDefaultCohortConfig) {
        String interpretRefSampleBarcode = interpretRefBarcode(sampleMetadata.refSampleBarcode());
        String refSampleId = sampleMetadata.refSampleId();
        String tumorSampleBarcode = sampleMetadata.tumorSampleBarcode();
        String tumorSampleId = sampleMetadata.tumorSampleId();
        String tumorReceivedSampleId = patientReporterData.getTumorSampleBarcode();
        String referenceReceivedSampleId = patientReporterData.getReferenceSampleBarcode();

        LocalDate arrivalDateRefSample = null;

        if (interpretRefSampleBarcode != null && refSampleId != null) {
            if (!interpretRefSampleBarcode.equals(refSampleId) || !tumorSampleBarcode.equals(tumorSampleId)) {
                // Don't need to check for anonymized runs
            }

            arrivalDateRefSample = patientReporterData.getReferenceArrivalDate();
            if (arrivalDateRefSample == null) {
                LOGGER.warn("Could not find arrival date for ref sample: {}", refSampleId);
            }
        }

        LocalDate arrivalDateTumorSample = patientReporterData.getTumorArrivalDate();
        if (arrivalDateTumorSample == null) {
            LOGGER.warn("Could not find arrival date for tumor sample: {}", tumorSampleId);
        }

        String hospitalPathologySampleId = patientReporterData.getPathologyId();

        String hospitalPatientId = patientReporterData.getPatientId();
        String biopsyLocation = patientReporterData.getBiopsySite().getBiopsyLocation();

        return ImmutableSampleReport.builder()
                .sampleMetadata(sampleMetadata)
                .tumorReceivedSampleId(tumorReceivedSampleId)
                .referenceReceivedSampleId(referenceReceivedSampleId)
                .tumorType(patientReporterData.getPrimaryTumorType())
                .biopsyLocation(biopsyLocation)
                .germlineReportingLevel(true)
                .reportViralPresence(true)
                .reportPharmogenetics(true)
                .refArrivalDate(arrivalDateRefSample)
                .tumorArrivalDate(arrivalDateTumorSample)
                .shallowSeqPurityString(Integer.toString(patientReporterData.getShallowPurity()))
                .labProcedures(patientReporterData.getSopString())
                .projectName(patientReporterData.getSubmissionNr())
                .submissionId(patientReporterData.getSubmissionNr())
                .hospitalPatientId(hospitalPatientId)
                .hospitalPathologySampleId(hospitalPathologySampleId)
                .build();
    }
}
