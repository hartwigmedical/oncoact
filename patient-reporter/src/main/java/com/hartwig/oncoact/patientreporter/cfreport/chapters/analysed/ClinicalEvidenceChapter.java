package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.itextpdf.layout.Document;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class ClinicalEvidenceChapter implements ReportChapter {

    private final ClinicalEvidenceFunctions clinicalEvidenceFunctions;

    @Override
    @NotNull
    public String name() {
        return "Genomic based therapy approaches";
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @NotNull
    private final AnalysedPatientReport report;

    public ClinicalEvidenceChapter(@NotNull final AnalysedPatientReport report, @NotNull final ReportResources reportResources) {
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

        List<ProtectEvidence> reportedStudiesEvidences = Lists.newArrayList();
        reportedStudiesEvidences.addAll(reportedStudies);

        addTreatmentSection(document, allEvidences);
        document.add(clinicalEvidenceFunctions.noteEvidence());
        addTrialSection(document, reportedStudiesEvidences);
        document.add(clinicalEvidenceFunctions.note("The Clinical Knowledgebase (CKB) database is used to annotate "
                + "genomic events for potential clinical study eligibility. Please note that clinical study eligibility "
                + "depends on multiple patient and tumor characteristics of which only the genomic events are considered "
                + "in this report. Therefore, although a study is shown, the patient may still not be eligible. "
                + "When the information is available, clinical studies are filtered on gender."));

        document.add(clinicalEvidenceFunctions.noteEvidenceMatching());
    }

    private void addTreatmentSection(@NotNull Document document, @NotNull List<ProtectEvidence> evidences) {
        boolean flagGermline = report.lamaPatientData().getReportSettings().getFlagGermlineOnReport();

        Map<String, List<ProtectEvidence>> onLabelTreatments =
                ClinicalEvidenceFunctions.buildTreatmentMap(evidences, flagGermline, null, "treatmentApproach");
        document.add(clinicalEvidenceFunctions.createTreatmentApproachTable("High level evidence", onLabelTreatments, contentWidth()));
    }

    private void addTrialSection(@NotNull Document document, @NotNull List<ProtectEvidence> evidences) {
        boolean flagGermline = report.lamaPatientData().getReportSettings().getFlagGermlineOnReport();

        Map<String, List<ProtectEvidence>> onLabelTreatments =
                ClinicalEvidenceFunctions.buildTrialMap(evidences, flagGermline, true, "study");
        document.add(clinicalEvidenceFunctions.createTrialTable("Tumor type specific clinical studies (NL)",
                onLabelTreatments,
                contentWidth()));
    }
}