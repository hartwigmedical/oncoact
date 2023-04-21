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

public class ClinicalEvidenceOffLabelChapter implements ReportChapter {

    @Override
    @NotNull
    public String name() {
        return "Therapy details (Other tumor types)";
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @NotNull
    private final AnalysedPatientReport report;

    public ClinicalEvidenceOffLabelChapter(@NotNull final AnalysedPatientReport report) {
        this.report = report;
    }

    @Override
    public void render(@NotNull final Document document) {
        GenomicAnalysis analysis = report.genomicAnalysis();
        List<ProtectEvidence> reportedOffLabel = analysis.offLabelEvidence();
        addTreatmentSection(document, "Evidence on other tumor types", reportedOffLabel);
        document.add(ClinicalEvidenceFunctions.noteEvidence());
        document.add(ClinicalEvidenceFunctions.noteGlossaryTerms());
        document.add(ClinicalEvidenceFunctions.noteEvidenceMatching());
    }

    private void addTreatmentSection(@NotNull Document document, @NotNull String header, @NotNull List<ProtectEvidence> evidences) {
        boolean reportGermline = report.sampleReport().germlineReportingLevel().equals(true);
        boolean requireOnLabel = false;
        Map<String, List<ProtectEvidence>> offLabelTreatments =
                ClinicalEvidenceFunctions.buildTreatmentMap(evidences, reportGermline, requireOnLabel);
        document.add(ClinicalEvidenceFunctions.createTreatmentTable(header, offLabelTreatments, contentWidth()));
    }
}
