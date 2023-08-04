package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.itextpdf.layout.Document;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ClinicalEvidenceOnLabelChapter implements ReportChapter {

    private final ClinicalEvidenceFunctions clinicalEvidenceFunctions;

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

    public ClinicalEvidenceOnLabelChapter(@NotNull final AnalysedPatientReport report,
                                          @NotNull final ReportResources reportResources) {
        this.report = report;
        clinicalEvidenceFunctions = new ClinicalEvidenceFunctions(reportResources);
    }

    @Override
    public void render(@NotNull final Document document) {

        GenomicAnalysis analysis = report.genomicAnalysis();
        List<ProtectEvidence> reportedOnLabel = analysis.tumorSpecificEvidence();
        List<ProtectEvidence> reportedOffLabel = analysis.offLabelEvidence();
        List<ProtectEvidence> reportedStudies = analysis.clinicalTrials();

        List<ProtectEvidence> allEvidences = Lists.newArrayList();
        allEvidences.addAll(reportedOnLabel);
        allEvidences.addAll(reportedOffLabel);

        addTreatmentSection(document, "Tumor type specific evidence based on treatment approach", allEvidences, "Treatment approach");
        addTrialSection(document, "Tumor type specific clinical trials (NL)", reportedStudies);

        document.add(clinicalEvidenceFunctions.note("Potential eligibility for DRUP is dependent on tumor type details therefore "
                + "certain tumor types may not be eligible for the DRUP.\n"));
        document.add(clinicalEvidenceFunctions.note("The iClusion knowledgebase is used to annotate DNA aberrations for potential "
                + "clinical study eligibility. Please note clinical study eligibility depends on multiple patient and tumor "
                + "characteristics of which only the DNA aberrations are considered in this report. \n"));
        document.add(clinicalEvidenceFunctions.noteEvidence());
        document.add(clinicalEvidenceFunctions.noteGlossaryTerms());
        document.add(clinicalEvidenceFunctions.noteEvidenceMatching());
    }

    private void addTreatmentSection(@NotNull Document document, @NotNull String header, @NotNull List<ProtectEvidence> evidences, @NotNull String columnName) {
        boolean flagGermline = report.lamaPatientData().getReportSettings().getFlagGermlineOnReport();

        Map<String, List<ProtectEvidence>> onLabelTreatments =
                ClinicalEvidenceFunctions.buildTreatmentAppraochMap(evidences, flagGermline, null);
        document.add(clinicalEvidenceFunctions.createTreatmentTable(header, onLabelTreatments, contentWidth(), columnName));
    }

    private void addTrialSection(@NotNull Document document, @NotNull String header, @NotNull List<ProtectEvidence> evidences) {
        boolean flagGermline = report.lamaPatientData().getReportSettings().getFlagGermlineOnReport();

        Map<String, List<ProtectEvidence>> onLabelTreatments =
                ClinicalEvidenceFunctions.buildTreatmentMap(evidences, flagGermline, true);
        document.add(clinicalEvidenceFunctions.createTrialTable(header, onLabelTreatments, contentWidth()));
    }
}