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

    @NotNull
    private final ClinicalEvidenceFunctions clinicalEvidenceFunctions;

    public ClinicalEvidenceOffLabelChapter(@NotNull final AnalysedPatientReport report, @NotNull final ReportResources reportResources) {
        this.report = report;
        this.clinicalEvidenceFunctions = new ClinicalEvidenceFunctions(reportResources);
    }

    @Override
    public void render(@NotNull final Document document) {

        GenomicAnalysis analysis = report.genomicAnalysis();
        List<ProtectEvidence> reportedOnLabel = analysis.tumorSpecificEvidence();
        List<ProtectEvidence> reportedOffLabel = analysis.offLabelEvidence();

        List<ProtectEvidence> allEvidences = Lists.newArrayList();
        allEvidences.addAll(reportedOnLabel);
        allEvidences.addAll(reportedOffLabel);

        addTreatmentSection(document, "Tumor type specific evidence based on treatment", allEvidences, "Treatment");
        document.add(clinicalEvidenceFunctions.noteEvidence());
        document.add(clinicalEvidenceFunctions.noteGlossaryTerms());
        document.add(clinicalEvidenceFunctions.noteEvidenceMatching());
    }

    private void addTreatmentSection(@NotNull Document document, @NotNull String header, @NotNull List<ProtectEvidence> evidences, @
            NotNull String columnName) {
        boolean flagGermline = report.lamaPatientData().getReportSettings().getFlagGermlineOnReport();
        Map<String, List<ProtectEvidence>> offLabelTreatments =
                ClinicalEvidenceFunctions.buildTreatmentMap(evidences, flagGermline, null);
        document.add(clinicalEvidenceFunctions.createTreatmentTable(header, offLabelTreatments, contentWidth(), columnName));
    }
}
