package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.model.ClinicalStudy;
import com.hartwig.oncoact.patientreporter.model.HighLevelEvidence;
import com.hartwig.oncoact.patientreporter.model.WgsReport;
import com.itextpdf.layout.Document;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    private final WgsReport wgsReport;

    public ClinicalEvidenceChapter(@NotNull final WgsReport wgsReport, @NotNull final ReportResources reportResources) {
        this.wgsReport = wgsReport;
        clinicalEvidenceFunctions = new ClinicalEvidenceFunctions(reportResources);
    }

    @Override
    public void render(@NotNull final Document document) {

        addTreatmentSection(document, wgsReport.therapy().highLevelEvidence());
        addTrialSection(document, wgsReport.therapy().clinicalStudies());

        document.add(clinicalEvidenceFunctions.note("Potential eligibility for DRUP is dependent on tumor type details therefore"
                + " patients with certain tumor types may not be eligible for the DRUP study.\n"));
        document.add(clinicalEvidenceFunctions.note("The iClusion database is used to annotate genomic events for potential clinical"
                + " study eligibility. Please note that clinical study eligibility depends on multiple patient and tumor "
                + "characteristics of which only the genomic events are considered in this report.\n"));
        document.add(clinicalEvidenceFunctions.noteEvidence());
        document.add(clinicalEvidenceFunctions.noteEvidenceMatching());
    }

    private void addTreatmentSection(@NotNull Document document, @NotNull List<HighLevelEvidence> highLevelEvidences) {
        document.add(clinicalEvidenceFunctions.createTreatmentApproachTable("High level evidence", highLevelEvidences, contentWidth()));
    }

    private void addTrialSection(@NotNull Document document, @NotNull List<ClinicalStudy> studies) {
        document.add(clinicalEvidenceFunctions.createTrialTable("Tumor type specific clinical studies (NL)",
                studies,
                contentWidth()));
    }
}