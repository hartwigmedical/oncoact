package com.hartwig.oncoact.patientreporter.cfreport.chapters.panel;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.LineDivider;
import com.hartwig.oncoact.patientreporter.cfreport.components.TumorLocationAndTypeTable;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class PanelQCFailChapter implements ReportChapter {

    private static final String TITLE_REPORT = "OncoAct tumor NGS report \n- Result Report Failed";

    @NotNull
    private final PanelFailReport report;
    @NotNull
    private final ReportResources reportResources;
    private final TumorLocationAndTypeTable tumorLocationAndTypeTable;

    public PanelQCFailChapter(@NotNull PanelFailReport report, @NotNull ReportResources reportResources) {
        this.report = report;
        this.reportResources = reportResources;
        this.tumorLocationAndTypeTable = new TumorLocationAndTypeTable(reportResources);
    }

    @NotNull
    @Override
    public String name() {
        return Strings.EMPTY;
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return report.isCorrectedReport() ? TITLE_REPORT + " (Corrected)" : TITLE_REPORT;
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
        reportDocument.add(tumorLocationAndTypeTable.createTumorLocation(report.lamaPatientData().getPrimaryTumorType(), contentWidth()));
        reportDocument.add(new Paragraph("The information regarding 'primary tumor location', 'primary tumor type' and 'biopsy location' \n"
                + "is based on information received from the originating hospital.").setMarginTop(10)
                .addStyle(reportResources.subTextSmallStyle()));
        reportDocument.add(LineDivider.createLineDivider(contentWidth()));
        reportDocument.add(createFailReasonDiv());
        reportDocument.add(LineDivider.createLineDivider(contentWidth()));
    }

    @NotNull
    private Div createFailReasonDiv() {

        Div div = new Div();
        div.setKeepTogether(true);

        div.add(new Paragraph(report.failExplanation().reportReason()).addStyle(reportResources.dataHighlightStyle()));
        div.add(new Paragraph(report.failExplanation().reportExplanation()).addStyle(reportResources.bodyTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        if (report.failExplanation().sampleFailReasonComment() != null) {
            div.add(new Paragraph(report.failExplanation().sampleFailReasonComment()).addStyle(reportResources.subTextBoldStyle())
                    .setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        }

        return div;
    }
}