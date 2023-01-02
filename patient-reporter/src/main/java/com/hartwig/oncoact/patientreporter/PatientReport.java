package com.hartwig.oncoact.patientreporter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        if (systemUser.equals("lieke") || systemUser.equals("liekeschoenmaker") || systemUser.equals("lschoenmaker")) {
            userName = "Lieke Schoenmaker";
            combinedUserName = userName + trainedEmployee;
        } else if (systemUser.equals("sandra") || systemUser.equals("sandravandenbroek") || systemUser.equals("sandravdbroek")
                || systemUser.equals("s_vandenbroek") || systemUser.equals("svandenbroek")) {
            userName = "Sandra van den Broek";
            combinedUserName = userName + trainedEmployee;
        } else if (systemUser.equals("daphne") || systemUser.equals("d_vanbeek") || systemUser.equals("daphnevanbeek")
                || systemUser.equals("dvanbeek")) {
            userName = "Daphne van Beek";
            combinedUserName = userName + trainedEmployee;
        } else if (systemUser.equals("root")) {
            combinedUserName = "automatically";
        } else {
            userName = systemUser;
            combinedUserName = userName + trainedEmployee;
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
    String reportDate();

    boolean isWGSreport();
}
