package com.hartwig.oncoact.patientreporter.cfreport.chapters.panel;

import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.itextpdf.layout.Document;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class TumorCharacteristicsChapter implements ReportChapter {

    public TumorCharacteristicsChapter() {
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @NotNull
    @Override
    public String name() {
        return "Tumor genomic profiles";
    }

    @Override
    public void render(@NotNull final Document document) {
    }
}
