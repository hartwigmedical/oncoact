package com.hartwig.oncoact.patientreporter.cfreport.chapters.panel;

import com.hartwig.lama.client.model.TumorType;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.LineDivider;
import com.hartwig.oncoact.patientreporter.cfreport.components.TumorLocationAndTypeTable;
import com.hartwig.oncoact.patientreporter.lama.InterpretTumorType;
import com.hartwig.oncoact.patientreporter.lama.LamaInterpretation;
import com.hartwig.oncoact.patientreporter.panel.PanelReport;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class PanelChapter implements ReportChapter {

    private static final String TITLE_REPORT = "Oncopanel Result Report";

    @NotNull
    private final PanelReport report;

    public PanelChapter(@NotNull PanelReport report) {
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

        reportDocument.add(createResultDiv());
    }

    @NotNull
    private Div createResultDiv() {
        Div div = new Div();
        div.setKeepTogether(true);

        div.add(new Paragraph("Data result file information").addStyle(ReportResources.dataHighlightStyle()));
        div.add(createContentParagraph("VCF file name: ", report.VCFFilename()));

        return div;
    }

    @NotNull
    private static Paragraph createContentParagraph(@NotNull String regularPart, @NotNull String boldPart) {
        return createContentParagraph(regularPart).add(new Text(boldPart).addStyle(ReportResources.smallBodyBoldTextStyle()))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private static Paragraph createContentParagraph(@NotNull String text) {
        return new Paragraph(text).addStyle(ReportResources.smallBodyTextStyle()).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }
}