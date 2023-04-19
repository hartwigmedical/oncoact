package com.hartwig.oncoact.patientreporter;

import java.time.LocalDate;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.lims.Lims;
import com.hartwig.oncoact.lims.LimsChecker;
import com.hartwig.oncoact.lims.cohort.ImmutableLimsCohortConfig;
import com.hartwig.oncoact.lims.cohort.LimsCohortConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
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
    public static SampleReport fromLimsModel(@NotNull SampleMetadata sampleMetadata, @NotNull Lims lims,
                                             @Nullable PatientReporterData patientReporterData, boolean allowDefaultCohortConfig) {
        String interpretRefSampleBarcode = interpretRefBarcode(sampleMetadata.refSampleBarcode());
        String refSampleId = sampleMetadata.refSampleId();
        String tumorSampleBarcode = sampleMetadata.tumorSampleBarcode();
        String tumorSampleId = sampleMetadata.tumorSampleId();
        String tumorReceivedSampleId = lims.tumorReceivedSampleId(tumorSampleBarcode);
        String referenceReceivedSampleId =
                interpretRefSampleBarcode != null ? lims.referenceTumorSampleId(interpretRefSampleBarcode) : null;

        LocalDate arrivalDateRefSample = null;

        if (interpretRefSampleBarcode != null && refSampleId != null) {
            if (!interpretRefSampleBarcode.equals(refSampleId) || !tumorSampleBarcode.equals(tumorSampleId)) {
                // Don't need to check for anonymized runs
                lims.validateSampleBarcodeCombination(interpretRefSampleBarcode, refSampleId, tumorSampleBarcode, tumorSampleId);
            }

            arrivalDateRefSample = lims.arrivalDate(interpretRefSampleBarcode, refSampleId);
            if (arrivalDateRefSample == null) {
                LOGGER.warn("Could not find arrival date for ref sample: {}", refSampleId);
            }
        }

        LocalDate arrivalDateTumorSample = lims.arrivalDate(tumorSampleBarcode, tumorSampleId);
        if (arrivalDateTumorSample == null) {
            LOGGER.warn("Could not find arrival date for tumor sample: {}", tumorSampleId);
        }

        String hospitalPathologySampleId = lims.hospitalPathologySampleId(tumorSampleBarcode);

        LimsCohortConfig cohortConfig = lims.cohortConfig(tumorSampleBarcode);
        if (cohortConfig == null) {
            if (allowDefaultCohortConfig) {
                LOGGER.warn("Using DEFAULT cohort config for tumor sample (non-production only!): {}", tumorSampleId);
                cohortConfig = buildDefaultCohortConfig();
            } else {
                throw new IllegalStateException(
                        "Cohort not configured in LIMS for sample '" + tumorSampleId + "' with barcode " + tumorSampleBarcode);
            }
        }

        String hospitalPatientId = lims.hospitalPatientId(tumorSampleBarcode);
        LimsChecker.checkHospitalPatientId(hospitalPatientId, tumorSampleId, cohortConfig);
        String biopsyLocation = patientReporterData.getBiopsySite().getBiopsyLocation();

        return ImmutableSampleReport.builder()
                .sampleMetadata(sampleMetadata)
                .tumorReceivedSampleId(tumorReceivedSampleId)
                .referenceReceivedSampleId(referenceReceivedSampleId)
                .tumorType(patientReporterData.getPrimaryTumorType())
                .biopsyLocation(biopsyLocation)
                .germlineReportingLevel(lims.germlineReportingChoice(tumorSampleBarcode, allowDefaultCohortConfig))
                .reportViralPresence(allowDefaultCohortConfig || lims.reportViralPresence(tumorSampleBarcode))
                .reportPharmogenetics(allowDefaultCohortConfig || lims.reportPgx(tumorSampleBarcode))
                .refArrivalDate(arrivalDateRefSample)
                .tumorArrivalDate(arrivalDateTumorSample)
                .shallowSeqPurityString(lims.purityShallowSeq(tumorSampleBarcode))
                .labProcedures(lims.labProcedures(tumorSampleBarcode))
                .cohort(cohortConfig)
                .projectName(lims.projectName(tumorSampleBarcode))
                .submissionId(lims.submissionId(tumorSampleBarcode))
                .hospitalContactData(lims.hospitalContactData(tumorSampleBarcode))
                .hospitalPatientId(hospitalPatientId)
                .hospitalPathologySampleId(LimsChecker.toHospitalPathologySampleIdForReport(hospitalPathologySampleId,
                        tumorSampleId,
                        cohortConfig))
                .build();
    }

    @NotNull
    private static LimsCohortConfig buildDefaultCohortConfig() {
        return ImmutableLimsCohortConfig.builder()
                .cohortId("DEFAULT")
                .sampleContainsHospitalCenterId(false)
                .reportGermline(true)
                .reportGermlineFlag(false)
                .reportConclusion(false)
                .reportViral(true)
                .reportPeach(true)
                .requireHospitalId(false)
                .requireHospitalPAId(false)
                .requireHospitalPersonsStudy(false)
                .requireHospitalPersonsRequester(false)
                .requireAdditionalInformationForSidePanel(false)
                .build();
    }
}
