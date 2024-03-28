package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.model.TumorSample;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.hartwig.oncoact.patientreporter.algo.wgs.BiopsyCreator.createBiopsy;
import static com.hartwig.oncoact.patientreporter.algo.wgs.HospitalCreator.createHospital;
import static com.hartwig.oncoact.patientreporter.algo.wgs.PatientCreater.createPatient;
import static com.hartwig.oncoact.patientreporter.algo.wgs.PrimaryTumorCreator.createPrimaryTumor;
import static com.hartwig.oncoact.patientreporter.algo.wgs.ReportingIdCreator.createReportingId;

class TumorSampleCreator {

    static TumorSample createTumorSample(
            @NotNull PatientReporterData lamaPatientData,
            @Nullable PatientInformationResponse diagnosticSiloPatientData
    ) {
        return TumorSample.builder()
                .sampleBarcode(lamaPatientData.getTumorSampleBarcode())
                .arrivalDate(lamaPatientData.getTumorArrivalDate())
                .primaryTumor(createPrimaryTumor(lamaPatientData.getPrimaryTumorType()))
                .reportingId(createReportingId(lamaPatientData))
                .cohort(lamaPatientData.getCohort())
                .patient(diagnosticSiloPatientData == null ? null : createPatient(diagnosticSiloPatientData))
                .hospital(createHospital(lamaPatientData))
                .biopsy(createBiopsy(lamaPatientData.getBiopsySite()))
                .sop(lamaPatientData.getSopString())
                .build();
    }
}