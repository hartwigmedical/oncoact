package com.hartwig.oncoact.patientreporter.cfreport.chapters.panel;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.itextpdf.io.IOException;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.UnitValue;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class PanelExplanationChapter implements ReportChapter {

    @NotNull
    private final ReportResources reportResources;

    public PanelExplanationChapter(@NotNull ReportResources reportResources) {
        this.reportResources = reportResources;
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @NotNull
    @Override
    public String name() {
        return "Result explanation";
    }

    @Override
    public void render(@NotNull Document reportDocument) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 1 }));
        table.setWidth(contentWidth());
        table.addCell(TableUtil.createLayoutCell().add(createExplanationDiv()));
        reportDocument.add(table);
    }

    @NotNull
    private Div createExplanationDiv() {
        Div div = new Div();

        div.add(new Paragraph("Details on the report general ").addStyle(reportResources.smallBodyHeadingStyle()));
        div.add(createContentParagraph("The variant calling of the sequencing data is based on reference genome version GRCh38."));
        div.add(createContentDivWithLinkThree("The gene name list can be downloaded from ", " https://storage.googleapis.com/hmf-public/OncoPanel-Resources/latest_oncopanel.zip",
                "."));
        div.add(new Paragraph("").addStyle(reportResources.smallBodyHeadingStyle()));
        div.add(new Paragraph("Details on the VCF file").addStyle(reportResources.smallBodyHeadingStyle()));
        div.add(createContentDivWithLinkThree("A short description of the headers present in the VCF file can be downloaded from ",
                " https://storage.googleapis.com/hmf-public/OncoPanel-Resources/latest_oncopanel.zip",
                "."));
        return div;
    }

    @NotNull
    private Paragraph createContentParagraph(@NotNull String text) {
        return new Paragraph(text).addStyle(reportResources.smallBodyTextStyle()).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private Div createContentDivWithLinkThree(@NotNull String string1, @NotNull String link, @NotNull String string3) {
        Div div = new Div();

        div.add(createParaGraphWithLinkThree(string1, link, string3));
        return div;
    }

    @NotNull
    private Paragraph createParaGraphWithLinkThree(@NotNull String string1, @NotNull String link, @NotNull String string3) {
        return new Paragraph(string1).addStyle(reportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(link).addStyle(reportResources.urlStyle()).setAction(PdfAction.createURI(link)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string3).addStyle(reportResources.subTextStyle()))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }
}