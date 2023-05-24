package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.ReportSignature;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.lama.LamaInterpretation;
import com.hartwig.oncoact.util.Formats;
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

public class DetailsAndDisclaimerChapter implements ReportChapter {

    @NotNull
    private final AnalysedPatientReport patientReport;

    public DetailsAndDisclaimerChapter(@NotNull AnalysedPatientReport patientReport) {
        this.patientReport = patientReport;
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
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 0.1f, 1}));
        table.setWidth(contentWidth());
        table.addCell(TableUtil.createLayoutCell().add(createSampleDetailsDiv(patientReport)));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell().add(createDisclaimerDiv(patientReport)));
        reportDocument.add(table);

        reportDocument.add(ReportSignature.createSignatureDiv(patientReport.logoRVAPath(), patientReport.signaturePath()));
        reportDocument.add(ReportSignature.createEndOfReportIndication());
    }

    @NotNull
    private static Div createSampleDetailsDiv(@NotNull AnalysedPatientReport patientReport) {
        Div div = new Div();

        div.add(new Paragraph("Sample details").addStyle(ReportResources.smallBodyHeadingStyle()));

        div.add(createContentParagraph("The samples have been sequenced at ", ReportResources.HARTWIG_ADDRESS));
        div.add(createContentParagraph("The samples have been analyzed by Next Generation Sequencing using Whole Genome Sequencing"));

        div.add(generateHMFAndPathologySampleIDParagraph(patientReport));

        String earliestArrivalDate = LamaInterpretation.extractEarliestArrivalDate(patientReport.patientReporterData().getReferenceArrivalDate(), patientReport.patientReporterData().getTumorArrivalDate());
        div.add(createContentParagraphTwice("The results in this report have been obtained between ",
                Formats.formatNullableString(earliestArrivalDate),
                " and ",
                patientReport.reportDate()));

        div.add(createContentParagraphTwice("This experiment is performed on the tumor sample which arrived on ",
                Formats.formatDate(patientReport.patientReporterData().getTumorArrivalDate()),
                " with barcode ",
                patientReport.patientReporterData().getTumorSampleBarcode()));
        div.add(createContentParagraphTwice("This experiment is performed on the blood sample which arrived on ",
                Formats.formatDate(patientReport.patientReporterData().getReferenceArrivalDate()),
                " with barcode ",
                Formats.formatNullableString(patientReport.patientReporterData().getReferenceSampleBarcode())));
        div.add(createContentParagraph("The results stated in this report are based on the tested tumor and blood sample."));
        div.add(createContentParagraph("This experiment is performed according to lab procedures: ", patientReport.patientReporterData().getSopString()));
        String whoVerified = "This report was generated " + patientReport.user();

        div.add(createContentParagraph(whoVerified));
        div.add(createContentParagraph("This report is addressed to: ",
                LamaInterpretation.hospitalContactReport(patientReport.patientReporterData().getStudyPI(),
                        patientReport.patientReporterData().getRequesterName(), patientReport.patientReporterData().getHospitalName(),
                        patientReport.patientReporterData().getHospitalPostalCode(), patientReport.patientReporterData().getHospitalCity(),
                        patientReport.patientReporterData().getHospitalAddress())));

        if (!patientReport.patientReporterData().getPatientId().equals(Strings.EMPTY)) {
            div.add(createContentParagraph("The hospital patient ID is: ", patientReport.patientReporterData().getPatientId()));
        }

        if (!patientReport.patientReporterData().getSubmissionNr().isEmpty() && !patientReport.patientReporterData().getSubmissionNr().isEmpty()) {
            div.add(createContentParagraphTwice("The project name of sample is: ",
                    patientReport.patientReporterData().getSubmissionNr(),
                    " and the submission ID is ",
                    patientReport.patientReporterData().getSubmissionNr()));
        }

        patientReport.comments().ifPresent(comments -> div.add(createContentParagraphRed("Comments: " + comments)));

        return div;
    }

    @NotNull
    private static Div createDisclaimerDiv(@NotNull AnalysedPatientReport patientReport) {
        String pipelineVersion = patientReport.pipelineVersion() == null ? "No pipeline version is known" : patientReport.pipelineVersion();
        Div div = new Div();

        div.add(new Paragraph("Disclaimer").addStyle(ReportResources.smallBodyHeadingStyle()));

        div.add(createContentParagraph("The data on which this report is based is generated "
                + "from tests that are performed under NEN-EN-ISO/IEC-17025:2017 TESTING L633 accreditation and have passed all internal quality controls."));
        div.add(createContentParagraphTwice("This report is generated by patient reporter ",
                ReportResources.VERSION_REPORT,
                " based on ",
                patientReport.qsFormNumber() + "."));
        div.add(createContentParagraph("(basic) UDI-DI: ", patientReport.udiDi() + "."));
        div.add(createContentDivWithLink("The OncoAct user manual can be found at ", ReportResources.MANUAL + ".", ReportResources.MANUAL + "."));
        div.add(createContentParagraph("This report is based on pipeline version ", pipelineVersion + "."));
        div.add(createContentParagraph("The ‘primary tumor location’ and ‘primary tumor type’ have influence on the "
                + "clinical evidence/study matching. No check is performed to verify the received information."));
        div.add(createContentParagraph("The conclusion of this report is based solely on the results of the DNA sequencing of the tumor "
                + "and the received tumor type. Final interpretation of the clinical consequence of this report should therefore "
                + "always be performed by the treating physician."));
        div.add(createContentParagraph("Based on a tumor purity of at least 20%, the test has a sensitivity of >95% for detection of "
                + "somatic variants and >95% for detection of translocations and gene copy number changes."));
        div.add(createContentParagraph("Based on the Dutch Act on Exceptional Medical Treatments (in Dutch: ‘Wet op de bijzondere"
                + " medische verrichten’) Stichting Hartwig Medical Foundation is not allowed to provide genetic counseling and"
                + " therefore will not share specific germline information, unless otherwise instructed and on explicit request "
                + "of a hospital that is authorised to provide genetic counseling to individual patients."));
        div.add(createContentParagraph("For feedback or complaints please contact ", ReportResources.CONTACT_EMAIL_QA + "."));
        div.add(createContentParagraph("For questions about the contents of this report, please contact ",
                ReportResources.CONTACT_EMAIL_GENERAL + "."));

        return div;
    }

    @NotNull
    private static Div createContentDivWithLink(@NotNull String string1, @NotNull String string2, @NotNull String link) {
        Div div = new Div();

        div.add(createParaGraphWithLink(string1, string2, link));
        return div;
    }

    @NotNull
    private static Paragraph createParaGraphWithLink(@NotNull String string1, @NotNull String string2, @NotNull String link) {
        return new Paragraph(string1).addStyle(ReportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string2).addStyle(ReportResources.urlStyle()).setAction(PdfAction.createURI(link)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private static Paragraph generateHMFAndPathologySampleIDParagraph(@NotNull AnalysedPatientReport patientReport) {
        String pathologyId = patientReport.patientReporterData().getPathologyNumber();
        String patientId = patientReport.patientReporterData().getPatientId();
        if (pathologyId != null && !pathologyId.equals(Strings.EMPTY)) {
            return createContentParagraphTwice("The patient ID is: ",
                    patientId,
                    " and the pathology tissue ID is: ",
                    pathologyId);
        } else {
            return createContentParagraph("The patient ID is: ", patientId);
        }
    }

    @NotNull
    private static Paragraph createContentParagraphRed(@NotNull String text) {
        return new Paragraph(text).addStyle(ReportResources.smallBodyTextStyleRed()).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private static Paragraph createContentParagraph(@NotNull String text) {
        return new Paragraph(text).addStyle(ReportResources.smallBodyTextStyle()).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
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

}