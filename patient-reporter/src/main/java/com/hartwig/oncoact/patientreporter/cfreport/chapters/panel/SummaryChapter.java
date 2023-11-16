package com.hartwig.oncoact.patientreporter.cfreport.chapters.panel;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.TumorLocationAndTypeTable;
import com.itextpdf.layout.Document;

import org.jetbrains.annotations.NotNull;

public class SummaryChapter implements ReportChapter {

    private final TumorLocationAndTypeTable tumorLocationAndTypeTable;

    public SummaryChapter(@NotNull final ReportResources reportResources) {
        this.tumorLocationAndTypeTable = new TumorLocationAndTypeTable(reportResources);

    }

    @NotNull
    @Override
    public String pdfTitle() {
        return "OncoAct tumor NGS panel report";
    }

    @NotNull
    @Override
    public String name() {
        return "Summary";
    }

    @Override
    public boolean isFullWidth() {
        return false;
    }

    @Override
    public boolean hasCompleteSidebar() {
        return true;
    }

    @Override
    public void render(@NotNull Document reportDocument) {
    }
}
