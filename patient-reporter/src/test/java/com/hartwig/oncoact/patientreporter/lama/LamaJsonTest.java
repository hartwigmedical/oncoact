package com.hartwig.oncoact.patientreporter.lama;

import com.google.common.io.Resources;
import com.hartwig.lama.client.model.BiopsySite;
import com.hartwig.lama.client.model.PatientReporterData;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LamaJsonTest {
    private static final String RUN_DIRECTORY = Resources.getResource("test_run").getPath();
    private static final String LAMA_JSON = RUN_DIRECTORY + "/lama/sample.lama.json";
    private static final double EPSILON = 1.0E-2;

    @NotNull
    public static PatientReporterData canReadLamaJsonEmpty() throws IOException {
        return LamaJson.read(LAMA_JSON);
    }

    @Test
    public void canReadLamaJson() throws IOException {
        PatientReporterData patientReporterData = canReadLamaJsonEmpty();
        assertEquals(patientReporterData.getHospitalName(), "hospitalName");
        assertEquals(patientReporterData.getHospitalPostalCode(), "hospitalPostalCode");
        assertEquals(patientReporterData.getStudyPI(), "studyPI");
        assertEquals(patientReporterData.getHospitalAddress(), "hospitalAddress");
        assertEquals(patientReporterData.getHospitalCity(), "hospitalCity");
        assertEquals(patientReporterData.getRequesterName(), "requesterName");
        assertEquals(patientReporterData.getRequesterEmail(), "requesterEmail");
        assertEquals(patientReporterData.getReportingId(), "reportingId");
        assertTrue(patientReporterData.getReportSettings().getReportGermline());
        assertTrue(patientReporterData.getReportSettings().getReportGermline());
        assertEquals(patientReporterData.getContractCode(), "contractCode");
        assertEquals(patientReporterData.getCohort(), "cohort");
        assertEquals(patientReporterData.getSubmissionNr(), "submissionNr");
        assertEquals(patientReporterData.getPatientId(), "patientId");
        assertEquals(patientReporterData.getReferenceSampleBarcode(), "referenceSampleBarcode");
        assertEquals(patientReporterData.getReferenceIsolationBarcode(), "referenceIsolationBarcode");
        assertEquals(patientReporterData.getReferenceArrivalDate(), LocalDate.of(2023, 4, 17));
        assertEquals(patientReporterData.getReferenceSamplingDate(), LocalDate.of(2023, 4, 17));
        assertEquals(patientReporterData.getTumorSampleId(), "tumorSampleId");
        assertEquals(patientReporterData.getTumorSampleBarcode(), "tumorSampleBarcode");
        assertEquals(patientReporterData.getTumorIsolationBarcode(), "tumorIsolationBarcode");
        assertEquals(patientReporterData.getTumorArrivalDate(), LocalDate.of(2023, 4, 17));
        assertEquals(patientReporterData.getTumorSamplingDate(), LocalDate.of(2023, 4, 17));
        assertEquals(patientReporterData.getPathologyNumber(), "pathologyNumber");
        assertEquals(patientReporterData.getPrimaryTumorType().getId(), "_id");
        assertEquals(patientReporterData.getPrimaryTumorType().getLocation(), "location");
        assertEquals(patientReporterData.getPrimaryTumorType().getType(), "type");
        assertEquals(patientReporterData.getPrimaryTumorType().getExtra(), "extra");
        assertEquals(patientReporterData.getPrimaryTumorType().getDoids(), List.of("162"));
        assertEquals(patientReporterData.getBiopsySite().getId(), "_id");
        assertEquals(patientReporterData.getBiopsySite().getLocation(), "location");
        assertEquals(patientReporterData.getBiopsySite().getLocationPalgaCode(), "locationPalgaCode");
        assertEquals(patientReporterData.getBiopsySite().getSubLocation(), "subLocation");
        assertEquals(patientReporterData.getBiopsySite().getSubLocationPalgaCode(), "subLocationPalgaCode");
        assertEquals(patientReporterData.getBiopsySite().getLateralisation(), BiopsySite.LateralisationEnum.LEFT);
        assertTrue(patientReporterData.getBiopsySite().getIsPrimaryTumor());
        assertEquals(patientReporterData.getSopString(), "sopString");
        assertEquals(patientReporterData.getShallowPurity(), 0, EPSILON);
    }

}