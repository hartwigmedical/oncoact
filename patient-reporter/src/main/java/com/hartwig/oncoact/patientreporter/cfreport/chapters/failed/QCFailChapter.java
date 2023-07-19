package com.hartwig.oncoact.patientreporter.cfreport.chapters.failed;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.LineDivider;
import com.hartwig.oncoact.patientreporter.cfreport.components.TumorLocationAndTypeTable;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class QCFailChapter implements ReportChapter {

    private static final String TITLE_REPORT = "OncoAct tumor WGS report - Failed";

    @NotNull
    private final QCFailReport failReport;
    @NotNull
    private final ReportResources reportResources;
    private final TumorLocationAndTypeTable tumorLocationAndTypeTable;

    public QCFailChapter(@NotNull QCFailReport failReport, @NotNull ReportResources reportResources) {
        this.failReport = failReport;
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
        return failReport.isCorrectedReport() ? TITLE_REPORT + " (Corrected)" : TITLE_REPORT;
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
        reportDocument.add(tumorLocationAndTypeTable.createTumorLocation(failReport.lamaPatientData().getPrimaryTumorType(), contentWidth()));

        reportDocument.add(new Paragraph("The information regarding 'primary tumor location', 'primary tumor type' and 'biopsy location'"
                + " is based on information received from the originating hospital.").addStyle(reportResources.subTextSmallStyle()));
        reportDocument.add(LineDivider.createLineDivider(contentWidth()));

        reportDocument.add(createFailReasonDiv(failReport.failExplanation().reportReason(),
                failReport.failExplanation().reportExplanation(), failReport.failExplanation().reportExplanationDetail()));
        reportDocument.add(LineDivider.createLineDivider(contentWidth()));
    }

    @NotNull
    private Div createFailReasonDiv(@NotNull String reportReason, @NotNull String reportExplanation, @NotNull String reportExplanationDetail) {
        Div div = new Div();
        div.setKeepTogether(true);

        div.add(new Paragraph(reportReason).addStyle(reportResources.dataHighlightStyle()));
        div.add(new Paragraph(reportExplanation).addStyle(reportResources.bodyTextStyle()).setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        div.add(new Paragraph(reportExplanationDetail).addStyle(reportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        return div;
    }
}