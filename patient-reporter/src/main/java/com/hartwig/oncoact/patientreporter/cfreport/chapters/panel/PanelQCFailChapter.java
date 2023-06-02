package com.hartwig.oncoact.patientreporter.cfreport.chapters.panel;

import com.hartwig.lama.client.model.TumorType;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.LineDivider;
import com.hartwig.oncoact.patientreporter.cfreport.components.TumorLocationAndTypeTable;
import com.hartwig.oncoact.patientreporter.lama.InterpretTumorType;
import com.hartwig.oncoact.patientreporter.lama.LamaInterpretation;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReason;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.util.Formats;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class PanelQCFailChapter implements ReportChapter {

    private static final Logger LOGGER = LogManager.getLogger(PanelQCFailChapter.class);

    private static final String TITLE_REPORT = "Oncopanel Result Report Failed";

    @NotNull
    private final PanelFailReport report;

    public PanelQCFailChapter(@NotNull PanelFailReport report) {
        this.report = report;
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
        InterpretTumorType interpretTumorType = LamaInterpretation.interpretTumorType(report.patientReporterData().getPrimaryTumorType());

        reportDocument.add(TumorLocationAndTypeTable.createTumorLocation(interpretTumorType.location(), interpretTumorType.type(), contentWidth()));
        reportDocument.add(new Paragraph("The information regarding 'primary tumor location', 'primary tumor type' and 'biopsy location'"
                + " is based on information received from the originating hospital.").addStyle(ReportResources.subTextSmallStyle()));
        reportDocument.add(LineDivider.createLineDivider(contentWidth()));
        reportDocument.add(createFailReasonDiv(report.panelFailReason()));
        reportDocument.add(LineDivider.createLineDivider(contentWidth()));
    }

    @NotNull
    private static Div createFailReasonDiv(@NotNull PanelFailReason failReason) {
        String reason = Formats.NA_STRING;
        String explanation = Formats.NA_STRING;
        String explanationDetail = Formats.NA_STRING;

        switch (failReason) {
            case PANEL_FAILURE: {
                reason = "Insufficient quality of received biomaterial(s)";
                explanation = "The received biomaterial(s) did not meet the requirements that are needed for \n"
                        + "high quality Next Generation Sequencing.";
                explanationDetail =
                        "Sequencing could not be performed due to insufficient DNA.";
                break;
            } default: {
                LOGGER.warn("Unexpected fail reason: {}", failReason);
            }
        }

        Div div = new Div();
        div.setKeepTogether(true);

        div.add(new Paragraph(reason).addStyle(ReportResources.dataHighlightStyle()));
        div.add(new Paragraph(explanation).addStyle(ReportResources.bodyTextStyle()).setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        div.add(new Paragraph(explanationDetail).addStyle(ReportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        return div;
    }
}