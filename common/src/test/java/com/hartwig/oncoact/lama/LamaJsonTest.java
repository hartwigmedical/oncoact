package com.hartwig.oncoact.lama;

import com.google.common.io.Resources;
import com.hartwig.lama.client.model.BiopsySite;
import com.hartwig.lama.client.model.PatientReporterData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class LamaJsonTest {

    private static final String LAMA_JSON = Resources.getResource("lama/lama.sample.json").getPath();
    private static final double EPSILON = 1.0E-2;
    @Test
    public void canReadLamaJson() throws IOException {
       PatientReporterData patientReporterData = LamaJson.read(LAMA_JSON);
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
        assertEquals(patientReporterData.getReferenceArrivalDate(), LocalDate.of(2023,4,17));
        assertEquals(patientReporterData.getReferenceSamplingDate(), LocalDate.of(2023,4,17));
        assertEquals(patientReporterData.getTumorSampleId(), "string");
        assertEquals(patientReporterData.getTumorSampleBarcode(), "string");
        assertEquals(patientReporterData.getTumorIsolationBarcode(), "string");
        assertEquals(patientReporterData.getTumorArrivalDate(), LocalDate.of(2023,4,17));
        assertEquals(patientReporterData.getTumorSamplingDate(), LocalDate.of(2023,4,17));
        assertEquals(patientReporterData.getPathologyId(), "string");
        assertEquals(patientReporterData.getPrimaryTumorType().getId(), "string");
        assertEquals(patientReporterData.getPrimaryTumorType().getLocation(), "string");
        assertEquals(patientReporterData.getPrimaryTumorType().getType(), "string");
        assertEquals(patientReporterData.getPrimaryTumorType().getExtra(), "string");
        assertEquals(patientReporterData.getPrimaryTumorType().getDoids(), List.of("string"));
        assertEquals(patientReporterData.getBiopsySite().getId(), "string");
        assertEquals(patientReporterData.getBiopsySite().getBiopsyLocation(), "string");
        assertEquals(patientReporterData.getBiopsySite().getBiopsyLocationPalgaCode(), "string");
        assertEquals(patientReporterData.getBiopsySite().getBiopsySubLocation(), "string");
        assertEquals(patientReporterData.getBiopsySite().getBiopsySubLocationPalgaCode(), "string");
        assertEquals(patientReporterData.getBiopsySite().getLateralisation(), BiopsySite.LateralisationEnum.LEFT);
        assertTrue(patientReporterData.getBiopsySite().getIsPrimaryTumor());
        assertEquals(patientReporterData.getSopString(), "string");
        assertEquals(patientReporterData.getShallowPurity(), 0, EPSILON);
    }
}