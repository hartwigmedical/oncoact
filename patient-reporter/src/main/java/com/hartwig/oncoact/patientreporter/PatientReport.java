package com.hartwig.oncoact.patientreporter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PatientReport {

    @NotNull
    PatientReporterData lamaPatientData();

    @Nullable
    PatientInformationResponse diagnosticSiloPatientData();

    @NotNull
    default String user() {
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
    String qsFormNumber();

    @NotNull
    Optional<String> comments();

    boolean isCorrectedReport();

    boolean isCorrectedReportExtern();

    @NotNull
    String signaturePath();

    @NotNull
    String logoRVAPath();

    @NotNull
    String logoCompanyPath();

    @NotNull
    String udiDi();

    @NotNull
    Map<String, List<PeachGenotype>> pharmacogeneticsGenotypes();

    @Nullable
    HlaAllelesReportingData hlaAllelesReportingData();

    @NotNull
    String reportDate();

    boolean isWGSReport();
}
