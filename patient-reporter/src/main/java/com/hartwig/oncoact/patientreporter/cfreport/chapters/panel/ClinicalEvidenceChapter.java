package com.hartwig.oncoact.patientreporter.cfreport.chapters.panel;

import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.itextpdf.layout.Document;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class ClinicalEvidenceChapter implements ReportChapter {

    public ClinicalEvidenceChapter() {
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @NotNull
    @Override
    public String name() {
        return "Genomic based therapy approaches";
    }

    @Override
    public void render(@NotNull final Document document) {
    }
}
