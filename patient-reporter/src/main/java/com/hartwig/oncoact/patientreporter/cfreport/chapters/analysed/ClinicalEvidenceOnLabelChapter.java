package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import java.util.List;
import java.util.Map;

import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.itextpdf.layout.Document;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class ClinicalEvidenceOnLabelChapter implements ReportChapter {

    @Override
    @NotNull
    public String name() {
        return "Therapy details (Tumor type specific)";
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @NotNull
    private final AnalysedPatientReport report;

    public ClinicalEvidenceOnLabelChapter(@NotNull final AnalysedPatientReport report) {
        this.report = report;
    }

    @Override
    public void render(@NotNull final Document document) {

        GenomicAnalysis analysis = report.genomicAnalysis();
        List<ProtectEvidence> reportedOnLabel = analysis.tumorSpecificEvidence();
        addTreatmentSection(document, "Tumor type specific evidence", reportedOnLabel);

        List<ProtectEvidence> reportedStudies = analysis.clinicalTrials();
        addTrialSection(document, "Tumor type specific clinical trials (NL)", reportedStudies);
        document.add(ClinicalEvidenceFunctions.note("Potential eligibility for DRUP is dependent on tumor type details therefore "
                + "certain tumor types may not be eligible for the DRUP.\n"));
        document.add(ClinicalEvidenceFunctions.note("The iClusion knowledgebase is used to annotate DNA aberrations for potential "
                + "clinical study eligibility. Please note clinical study eligibility depends on multiple patient and tumor "
                + "characteristics of which only the DNA aberrations are considered in this report. \n"));
        document.add(ClinicalEvidenceFunctions.noteEvidence());
        document.add(ClinicalEvidenceFunctions.noteGlossaryTerms());
        document.add(ClinicalEvidenceFunctions.noteEvidenceMatching());
    }

    private void addTreatmentSection(@NotNull Document document, @NotNull String header, @NotNull List<ProtectEvidence> evidences) {
        boolean requireOnLabel = true;
        boolean flagGermline = report.patientReporterData().getReportSettings().getFlagGermlineOnReport() != null ? report.patientReporterData().getReportSettings().getFlagGermlineOnReport() : false;

        Map<String, List<ProtectEvidence>> onLabelTreatments =
                ClinicalEvidenceFunctions.buildTreatmentMap(evidences, flagGermline, requireOnLabel);
        document.add(ClinicalEvidenceFunctions.createTreatmentTable(header, onLabelTreatments, contentWidth()));
    }

    private void addTrialSection(@NotNull Document document, @NotNull String header, @NotNull List<ProtectEvidence> evidences) {
        boolean requireOnLabel = true;
        boolean flagGermline = report.patientReporterData().getReportSettings().getFlagGermlineOnReport() != null ? report.patientReporterData().getReportSettings().getFlagGermlineOnReport() : false;

        Map<String, List<ProtectEvidence>> onLabelTreatments =
                ClinicalEvidenceFunctions.buildTreatmentMap(evidences, flagGermline, requireOnLabel);
        document.add(ClinicalEvidenceFunctions.createTrialTable(header, onLabelTreatments, contentWidth()));
    }
}