package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.oncoact.patientreporter.PatientReporterConfig;
import com.hartwig.oncoact.patientreporter.correction.Correction;
import com.hartwig.oncoact.patientreporter.failedreasondb.FailedReason;
import com.hartwig.oncoact.patientreporter.lama.LamaInterpretation;
import com.hartwig.oncoact.patientreporter.model.WgsReportFailed;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReason;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReportData;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

import static com.hartwig.oncoact.patientreporter.algo.wgs.FailGenomicCreator.createFailGenomic;
import static com.hartwig.oncoact.patientreporter.algo.wgs.SampleCreator.createSample;
import static com.hartwig.oncoact.patientreporter.algo.wgs.VersionCreator.createVersion;

public class WgsReportCreatorFailed {

    private final QCFailReportData reportData;

    public WgsReportCreatorFailed(QCFailReportData reportData) {
        this.reportData = reportData;
    }

    @NotNull
    public WgsReportFailed run(@NotNull PatientReporterConfig config) throws IOException {

        QCFailReason reason = config.qcFailReason();
        assert reason != null;

        FailedReason failedDatabase = FailedReason.builder()
                .reportReason(reason.reportReason())
                .reportExplanation(reason.reportExplanation())
                .sampleFailReasonComment(config.sampleFailReasonComment())
                .build();

        String pipelineVersion = null;
        if (reason.isDeepWGSDataAvailable()) {
            pipelineVersion = config.pipelineVersion();

        }

        return WgsReportFailed.builder()
                .reportDate(getReportDate())
                .receiver(getReceiver())
                .tumorSample(TumorSampleCreator.createTumorSample(reportData.lamaPatientData(), reportData.diagnosticSiloPatientData()))
                .referenceSample(createSample(reportData.lamaPatientData().getTumorSampleBarcode(), reportData.lamaPatientData().getTumorArrivalDate()))
                .failedDatabase(failedDatabase)
                .failGenomic(reason.isDeepWGSDataAvailable() ? createFailGenomic(reason, config.orangeJson()) : null)
                .version(createVersion(pipelineVersion, reportData.udiDi(), reason.qcFormNumber()))
                .comments(Optional.ofNullable(reportData.correction()).map(Correction::comments))
                .user(getUser())
                .build();
    }

    @NotNull
    private String getUser() {
        String systemUser = System.getProperty("user.name");
        String userName;
        String trainedEmployee = " (trained IT employee)";
        String combinedUserName;

        switch (systemUser) {
            case "lieke":
            case "liekeschoenmaker":
            case "lschoenmaker":
                userName = "LS";
                combinedUserName = userName + trainedEmployee;
                break;
            case "sandra":
            case "sandravandenbroek":
            case "sandravdbroek":
            case "s_vandenbroek":
            case "svandenbroek":
                userName = "SvdB";
                combinedUserName = userName + trainedEmployee;
                break;
            case "ybijl":
                userName = "YB";
                combinedUserName = userName + trainedEmployee;
                break;
            case "root":
                combinedUserName = "automatically";
                break;
            default:
                userName = systemUser;
                combinedUserName = userName + trainedEmployee;
                break;
        }

        if (combinedUserName.endsWith(trainedEmployee)) {
            combinedUserName = "by " + combinedUserName;
        }

        return combinedUserName + " and checked by a trained Clinical Molecular Biologist in Pathology (KMBP)";
    }

    @NotNull
    private String getReceiver() {
        return LamaInterpretation.hospitalContactReport(reportData.lamaPatientData());
    }

    @NotNull
    private String getReportDate() {
        return Formats.formatDate(reportData.reportTime().toLocalDate());
    }
}