package com.hartwig.oncoact.patientreporter.lama;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.lama.client.model.BiopsySite;
import com.hartwig.lama.client.model.PatientReporterData;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class LamaJsonTest {
    private static final String LAMA_JSON = Resources.getResource("lama/sample.lama.json").getPath();

    private static final double EPSILON = 1.0E-2;

    @NotNull
    public static PatientReporterData canReadLamaJsonEmpty() throws IOException {
        return LamaJson.read(LAMA_JSON);
    }

    @Test
    public void canReadLamaJson() throws IOException {
        PatientReporterData lamaPatientData = canReadLamaJsonEmpty();
        assertEquals(lamaPatientData.getHospitalName(), "hospitalName");
        assertEquals(lamaPatientData.getOfficialHospitalName(), "officialHospitalName");
        assertEquals(lamaPatientData.getHospitalPostalCode(), "hospitalPostalCode");
        assertEquals(lamaPatientData.getStudyPI(), "studyPI");
        assertEquals(lamaPatientData.getHospitalAddress(), "hospitalAddress");
        assertEquals(lamaPatientData.getHospitalCity(), "hospitalCity");
        assertEquals(lamaPatientData.getRequesterName(), "requesterName");
        assertEquals(lamaPatientData.getRequesterEmail(), "requesterEmail");
        assertEquals(lamaPatientData.getReportingId(), "reportingId");
        assertTrue(lamaPatientData.getReportSettings().getReportGermline());
        assertTrue(lamaPatientData.getReportSettings().getReportGermline());
        assertEquals(lamaPatientData.getContractCode(), "contractCode");
        assertEquals(lamaPatientData.getCohort(), "cohort");
        assertEquals(lamaPatientData.getSubmissionNr(), "submissionNr");
        assertEquals(lamaPatientData.getPatientId(), "patientId");
        assertEquals(lamaPatientData.getReferenceSampleBarcode(), "referenceSampleBarcode");
        assertEquals(lamaPatientData.getReferenceIsolationBarcode(), "referenceIsolationBarcode");
        assertEquals(lamaPatientData.getReferenceArrivalDate(), LocalDate.of(2023, 4, 17));
        assertEquals(lamaPatientData.getReferenceSamplingDate(), LocalDate.of(2023, 4, 17));
        assertEquals(lamaPatientData.getTumorSampleId(), "tumorSampleId");
        assertEquals(lamaPatientData.getTumorSampleBarcode(), "tumorSampleBarcode");
        assertEquals(lamaPatientData.getTumorIsolationBarcode(), "tumorIsolationBarcode");
        assertEquals(lamaPatientData.getTumorArrivalDate(), LocalDate.of(2023, 4, 17));
        assertEquals(lamaPatientData.getTumorSamplingDate(), LocalDate.of(2023, 4, 17));
        assertEquals(lamaPatientData.getPathologyNumber(), "pathologyNumber");
        assertEquals(lamaPatientData.getPrimaryTumorType().getId(), "_id");
        assertEquals(lamaPatientData.getPrimaryTumorType().getLocation(), "location");
        assertEquals(lamaPatientData.getPrimaryTumorType().getType(), "type");
        assertEquals(lamaPatientData.getPrimaryTumorType().getExtra(), "extra");
        assertEquals(lamaPatientData.getPrimaryTumorType().getDoids(), List.of("162"));
        assertEquals(lamaPatientData.getBiopsySite().getId(), "_id");
        assertEquals(lamaPatientData.getBiopsySite().getLocation(), "location");
        assertEquals(lamaPatientData.getBiopsySite().getLocationPalgaCode(), "locationPalgaCode");
        assertEquals(lamaPatientData.getBiopsySite().getSubLocation(), "subLocation");
        assertEquals(lamaPatientData.getBiopsySite().getSubLocationPalgaCode(), "subLocationPalgaCode");
        assertEquals(lamaPatientData.getBiopsySite().getLateralisation(), BiopsySite.LateralisationEnum.LEFT);
        assertTrue(lamaPatientData.getBiopsySite().getIsPrimaryTumor());
        assertEquals(lamaPatientData.getSopString(), "sopString");
        assertEquals(lamaPatientData.getShallowPurity(), 0, EPSILON);
        assertEquals(lamaPatientData.getIsStudy(), true);
    }
}