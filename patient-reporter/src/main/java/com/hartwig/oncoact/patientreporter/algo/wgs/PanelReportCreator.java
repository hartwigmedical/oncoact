package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.oncoact.patientreporter.PanelReporterConfig;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.lama.LamaInterpretation;
import com.hartwig.oncoact.patientreporter.model.PanelReportData;
import com.hartwig.oncoact.patientreporter.panel.QCFailPanelReportData;
import com.hartwig.oncoact.util.Formats;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.hartwig.oncoact.patientreporter.algo.wgs.SampleCreator.createSample;
import static com.hartwig.oncoact.patientreporter.algo.wgs.VersionCreator.createVersion;

public class PanelReportCreator {

    private final QCFailPanelReportData reportData;

    public PanelReportCreator(@NotNull final QCFailPanelReportData reportData) {
        this.reportData = reportData;
    }

    @NotNull
    public PanelReportData run(@NotNull PanelReporterConfig config) throws IOException {
        return PanelReportData.builder()
                .reportDate(getReportDate())
                .receiver(getReceiver())
                .tumorSample(TumorSampleCreator.createTumorSample(reportData.lamaPatientData(), reportData.diagnosticSiloPatientData()))
                .referenceSample(createSample(reportData.lamaPatientData().getTumorSampleBarcode(), reportData.lamaPatientData().getTumorArrivalDate()))
                .vcfFileName(config.panelVCFname())
                .version(createVersion(config.pipelineVersion(), null, QsFormNumber.FOR_344))
                .comments(reportData.comments())
                .user(getUser())
                .build();
    }

    @NotNull
    private String getReceiver() {
        return LamaInterpretation.hospitalContactReport(reportData.lamaPatientData());
    }

    @NotNull
    private String getReportDate() {
        return Formats.formatDate(reportData.reportTime().toLocalDate());
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
}