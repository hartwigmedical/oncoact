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
        assertEquals(patientReporterData.getHospitalName(), "string");
        assertEquals(patientReporterData.getHospitalPostalCode(), "string");
        assertEquals(patientReporterData.getStudyPI(), "string");
        assertEquals(patientReporterData.getHospitalAddress(), "string");
        assertEquals(patientReporterData.getHospitalCity(), "string");
        assertEquals(patientReporterData.getRequesterName(), "string");
        assertEquals(patientReporterData.getRequesterEmail(), "string");
        assertEquals(patientReporterData.getReportingId(), "string");
        assertTrue(patientReporterData.getReportSettings().getReportGermline());
        assertTrue(patientReporterData.getReportSettings().getReportGermline());
        assertEquals(patientReporterData.getContractCode(), "string");
        assertEquals(patientReporterData.getCohort(), "string");
        assertEquals(patientReporterData.getSubmissionNr(), "string");
        assertEquals(patientReporterData.getPatientId(), "string");
        assertEquals(patientReporterData.getReferenceSampleBarcode(), "string");
        assertEquals(patientReporterData.getReferenceIsolationBarcode(), "string");
        assertEquals(patientReporterData.getReferenceArrivalDate(), LocalDate.of(2023, 4, 17));
        assertEquals(patientReporterData.getReferenceSamplingDate(), LocalDate.of(2023, 4, 17));
        assertEquals(patientReporterData.getTumorSampleId(), "string");
        assertEquals(patientReporterData.getTumorSampleBarcode(), "string");
        assertEquals(patientReporterData.getTumorIsolationBarcode(), "string");
        assertEquals(patientReporterData.getTumorArrivalDate(), LocalDate.of(2023, 4, 17));
        assertEquals(patientReporterData.getTumorSamplingDate(), LocalDate.of(2023, 4, 17));
        assertEquals(patientReporterData.getPathologyNumber(), "string");
        assertEquals(patientReporterData.getPrimaryTumorType().getId(), "string");
        assertEquals(patientReporterData.getPrimaryTumorType().getLocation(), "string");
        assertEquals(patientReporterData.getPrimaryTumorType().getType(), "string");
        assertEquals(patientReporterData.getPrimaryTumorType().getExtra(), "string");
        assertEquals(patientReporterData.getPrimaryTumorType().getDoids(), List.of("string"));
        assertEquals(patientReporterData.getBiopsySite().getId(), "string");
        assertEquals(patientReporterData.getBiopsySite().getLocation(), "string");
        assertEquals(patientReporterData.getBiopsySite().getLocationPalgaCode(), "string");
        assertEquals(patientReporterData.getBiopsySite().getSubLocation(), "string");
        assertEquals(patientReporterData.getBiopsySite().getSubLocationPalgaCode(), "string");
        assertEquals(patientReporterData.getBiopsySite().getLateralisation(), BiopsySite.LateralisationEnum.LEFT);
        assertTrue(patientReporterData.getBiopsySite().getIsPrimaryTumor());
        assertEquals(patientReporterData.getSopString(), "string");
        assertEquals(patientReporterData.getShallowPurity(), 0, EPSILON);
    }

}