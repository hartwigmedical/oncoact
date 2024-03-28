package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.model.ReportingId;
import com.hartwig.oncoact.patientreporter.model.ReportingIdType;
import org.jetbrains.annotations.NotNull;

class ReportingIdCreator {

    static ReportingId createReportingId(
            @NotNull PatientReporterData lamaPatientData
    ) {
        return ReportingId.builder()
                .value(lamaPatientData.getReportingId())
                .type(lamaPatientData.getIsStudy() ? ReportingIdType.STUDY : ReportingIdType.DIAGNOSTIC)
                .pathologyId(lamaPatientData.getPathologyNumber())
                .label(lamaPatientData.getHospitalSampleLabel())
                .build();
    }
}