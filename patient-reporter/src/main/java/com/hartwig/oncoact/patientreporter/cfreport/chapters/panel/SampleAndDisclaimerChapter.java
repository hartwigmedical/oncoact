package com.hartwig.oncoact.patientreporter.cfreport.chapters.panel;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.ReportSignature;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.lama.LamaInterpretation;
import com.hartwig.oncoact.patientreporter.panel.PanelReport;
import com.hartwig.oncoact.util.Formats;
import com.itextpdf.io.IOException;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.UnitValue;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class SampleAndDisclaimerChapter implements ReportChapter {

    @NotNull
    private final PanelReport report;

    public SampleAndDisclaimerChapter(@NotNull PanelReport report) {
        this.report = report;
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @Override
    @NotNull
    public String name() {
        return "Sample details & disclaimers";
    }

    @Override
    public boolean isFullWidth() {
        return false;
    }

    @Override
    public void render(@NotNull Document reportDocument) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 1, 0.1f, 1 }));
        table.setWidth(contentWidth());
        table.addCell(TableUtil.createLayoutCell().add(createSampleDetailsColumn()));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell().add(createDisclaimerColumn()));
        reportDocument.add(table);

        reportDocument.add(ReportSignature.createSignatureDivPanel(report.signaturePath()));
        reportDocument.add(ReportSignature.createEndOfReportIndication());
    }

    @NotNull
    private Div createSampleDetailsColumn() {

        Div div = createSampleDetailsDiv();
        div.add(createContentParagraph("The samples have been sequenced at ", ReportResources.HARTWIG_ADDRESS));
        div.add(createContentParagraph("The sample(s) have been analyzed by Next Generation Sequencing using targeted enrichment."));
        div.add(generateHMFSampleIDParagraph(report.lamaPatientData().getReportingId()));

        String earliestArrivalDate = LamaInterpretation.extractEarliestArrivalDate(report.lamaPatientData().getReferenceArrivalDate(),
                report.lamaPatientData().getTumorArrivalDate());
        div.add(createContentParagraphTwice("The results in this report have been obtained between ",
                Formats.formatNullableString(earliestArrivalDate),
                " and ",
                report.reportDate()));

        div.add(createContentParagraphTwice("This experiment is performed on the tumor sample which arrived on ",
                Formats.formatDate(report.lamaPatientData().getTumorArrivalDate()),
                " with barcode ",
                report.lamaPatientData().getTumorSampleBarcode()));
        div.add(createContentParagraph("The results stated in this report are based on the tested tumor sample."));
        div.add(createContentParagraph("This experiment is performed according to lab procedures: ", report.lamaPatientData().getSopString()));
        String whoVerified = "This report was generated " + report.user();

        div.add(createContentParagraph(whoVerified));
        div.add(createContentParagraph("This report is addressed to: ", LamaInterpretation.hospitalContactReport(report.lamaPatientData().getStudyPI(),
                report.lamaPatientData().getRequesterName(), report.lamaPatientData().getHospitalName(),
                report.lamaPatientData().getHospitalPostalCode(), report.lamaPatientData().getHospitalCity(),
                report.lamaPatientData().getHospitalAddress())));
        report.comments().ifPresent(comments -> div.add(createContentParagraphRed("Comments: " + comments)));

        return div;
    }

    @NotNull
    private static Paragraph createContentParagraphRed(@NotNull String text) {
        return new Paragraph(text).addStyle(ReportResources.smallBodyTextStyleRed()).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private static Paragraph generateHMFSampleIDParagraph(@NotNull String reportingId) {
        if (reportingId.substring(0, 4).matches("[a-zA-Z]+")) {
            return createContentParagraph("Study id: ", reportingId);
        } else {
            return createContentParagraph("Hospital patient id: ", reportingId);
        }
    }

    @NotNull
    private Div createDisclaimerColumn() {
        Div div = createDisclaimerDiv();

        String pipelineVersion = report.pipelineVersion() == null ? "No pipeline version is known" : report.pipelineVersion();
        div.add(createContentParagraphTwice("This report is generated by patient reporter ",
                ReportResources.VERSION_REPORT,
                " based on ",
                report.qsFormNumber() + "."));
        div.add(createContentParagraph("This report is based on pipeline version ",
                pipelineVersion));

        div.add(createContentParagraph("No check is performed to verify the ‘primary tumor location’ and ‘primary tumor type’ information."));
        div.add(createContentParagraph("The results in this report and in the result files are solely based on the results of the DNA "
                + "sequencing of the received tumor material."));
        div.add(createContentParagraph("Any clinical interpretation of the result files is the responsibility of the hospital."));
        div.add(createContentParagraph("This test is intended for tumor samples with minimal 10% tumor cel percentage."));
        div.add(createContentParagraph("For feedback or complaints please contact ", ReportResources.CONTACT_EMAIL_QA + "."));
        div.add(createContentParagraph("For questions about the contents of this report, please contact ",
                ReportResources.CONTACT_EMAIL_GENERAL + "."));

        return div;
    }

    @NotNull
    private static Div createDisclaimerDiv() {
        Div div = new Div();
        div.add(new Paragraph("Disclaimer").addStyle(ReportResources.smallBodyHeadingStyle()));
        return div;
    }

    @NotNull
    private static Div createSampleDetailsDiv() {
        Div div = new Div();
        div.add(new Paragraph("Sample details").addStyle(ReportResources.smallBodyHeadingStyle()));
        return div;
    }

    @NotNull
    private static Paragraph createContentParagraph(@NotNull String regularPart, @NotNull String boldPart) {
        return createContentParagraph(regularPart).add(new Text(boldPart).addStyle(ReportResources.smallBodyBoldTextStyle()))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private static Paragraph createContentParagraphTwice(@NotNull String regularPart, @NotNull String boldPart,
            @NotNull String regularPart2, @NotNull String boldPart2) {
        return createContentParagraph(regularPart).add(new Text(boldPart).addStyle(ReportResources.smallBodyBoldTextStyle()))
                .add(regularPart2)
                .add(new Text(boldPart2).addStyle(ReportResources.smallBodyBoldTextStyle()))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private static Paragraph createContentParagraph(@NotNull String text) {
        return new Paragraph(text).addStyle(ReportResources.smallBodyTextStyle()).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }
}