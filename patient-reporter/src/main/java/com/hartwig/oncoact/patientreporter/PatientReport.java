package com.hartwig.oncoact.patientreporter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.orange.peach.PeachEntry;

import org.jetbrains.annotations.NotNull;

public interface PatientReport {

    @NotNull
    SampleReport sampleReport();

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
                userName = "Lieke Schoenmaker";
                combinedUserName = userName + trainedEmployee;
                break;
            case "sandra":
            case "sandravandenbroek":
            case "sandravdbroek":
            case "s_vandenbroek":
            case "svandenbroek":
                userName = "Sandra van den Broek";
                combinedUserName = userName + trainedEmployee;
                break;
            case "daphne":
            case "d_vanbeek":
            case "daphnevanbeek":
            case "dvanbeek":
                userName = "Daphne van Beek";
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
    Map<String, List<PeachEntry>> pharmacogeneticsGenotypes();

    @NotNull
    HlaAllelesReportingData hlaAllelesReportingData();

    @NotNull
    String reportDate();

    boolean isWGSReport();
}
