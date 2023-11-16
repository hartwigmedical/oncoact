package com.hartwig.oncoact.patientreporter.cfreport.chapters.panel;

import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.itextpdf.layout.Document;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class GenomicAnalyzerChapter implements ReportChapter {

    public GenomicAnalyzerChapter() {
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @NotNull
    @Override
    public String name() {
        return "Genomic events";
    }

    @Override
    public void render(@NotNull final Document document) {

    }
}
