package com.hartwig.oncoact.patientreporter.cfreport.chapters.failed;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.ReportSignature;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.lama.LamaInterpretation;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailType;
import com.hartwig.oncoact.util.Formats;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.UnitValue;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class QCFailDisclaimerChapter implements ReportChapter {
    @NotNull
    private final QCFailReport failReport;

    public QCFailDisclaimerChapter(@NotNull QCFailReport failReport) {
        this.failReport = failReport;
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @Override
    @NotNull
    public String name() {
        return "Disclaimers";
    }

    @Override
    public boolean isFullWidth() {
        return false;
    }

    @Override
    public void render(@NotNull Document reportDocument) {
        reportDocument.add(createContentBody());
        reportDocument.add(ReportSignature.createSignatureDiv(failReport.logoRVAPath(), failReport.signaturePath()).setMarginTop(15));
        reportDocument.add(ReportSignature.createEndOfReportIndication());
    }

    @NotNull
    private Table createContentBody() {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 1, 0.1f, 1 }));
        table.setWidth(contentWidth());
        table.addCell(TableUtil.createLayoutCell().add(createSampleDetailsColumn()));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell().add(createDisclaimerColumn()));
        return table;
    }

    @NotNull
    private Div createSampleDetailsColumn() {
        Div div = createSampleDetailsDiv();
        div.add(samplesAreEvaluatedAtHMFAndWithSampleID());
        div.add(reportIsBasedOnTumorSampleArrivedAt());
        div.add(reportIsBasedOnBloodSampleArrivedAt());
        div.add(resultsAreObtainedBetweenDates());
        if (!failReport.patientReporterData().getPathologyNumber().isEmpty() && !failReport.patientReporterData().getPatientId().isEmpty()) {
            if (failReport.patientReporterData().getPathologyNumber() != null) {
                div.add(reportIsForPathologySampleID());
            }
        }
        if (!failReport.patientReporterData().getPathologyNumber().isEmpty() && failReport.patientReporterData().getPatientId().isEmpty()) {
            if (failReport.patientReporterData().getPathologyNumber() != null && failReport.patientReporterData().getPatientId() != null) {
                div.add(reportHospitalPatientIDAndPathologySampleId());
            }
        }

        if (!failReport.patientReporterData().getSubmissionNr().isEmpty() && !failReport.patientReporterData().getSubmissionNr().isEmpty()) {
            div.add(reportIsForProjectAndSubmission());
        }

        if (failReport.reason().type() == QCFailType.LOW_QUALITY_BIOPSY) {
            div.add(sampleHasMolecularTumorPercentage());
        }
        div.add(reportIsBasedOnBloodAndTumorSamples());

        return div;
    }

    @NotNull
    private Div createDisclaimerColumn() {
        Div div = createDisclaimerDiv();
        div.add(testsArePerformedByAccreditedLab());
        div.add(testsArePerformedUnderUNI());
        div.add(testsManual());
        div.add(reportIsVerifiedByAndAddressedTo());
        div.add(reportIsGeneratedByPatientReporterVersion());
        failReport.comments().ifPresent(comments -> div.add(createContentParagraphRed("Comments: " + comments)));
        div.add(resubmitSample());
        div.add(forQuestionsPleaseContactHMF());
        return div;
    }

    @NotNull
    private Paragraph resubmitSample() {
        return createContentParagraph("If available new biomaterial(s) can be provided for a new assessment, please contact ",
                "info@hartwigmedicalfoundation.nl");
    }

    @NotNull
    private Paragraph reportHospitalPatientIDAndPathologySampleId() {
        return createContentParagraphTwice("The hospital patient ID is ",
                failReport.patientReporterData().getPatientId(),
                " and the pathology tissue ID is: ",
                failReport.patientReporterData().getPathologyNumber());

    }

    @NotNull
    private Paragraph reportIsForPathologySampleID() {
        return createContentParagraph("The pathology tissue ID is: ", failReport.patientReporterData().getPathologyNumber());
    }

    @NotNull
    private Paragraph reportIsForProjectAndSubmission() {
        return createContentParagraphTwice("The project name of the sample is ",
                failReport.patientReporterData().getSubmissionNr(),
                " and the submission ID is ",
                failReport.patientReporterData().getSubmissionNr());
    }

    @NotNull
    private Paragraph resultsAreObtainedBetweenDates() {
        String earliestArrivalDate = LamaInterpretation.extractEarliestArrivalDate(failReport.patientReporterData().getReferenceArrivalDate(), failReport.patientReporterData().getTumorArrivalDate());
        return createContentParagraphTwice("The results in this report have been obtained between ",
                earliestArrivalDate != null ? earliestArrivalDate : Formats.NA_STRING,
                " and ",
                failReport.reportDate());
    }

    @NotNull
    private Paragraph reportIsBasedOnTumorSampleArrivedAt() {
        return createContentParagraphTwice("This experiment is performed on the tumor sample which arrived on ",
                Formats.formatDate(failReport.patientReporterData().getTumorArrivalDate()),
                " with barcode ",
                failReport.patientReporterData().getTumorSampleBarcode());
    }

    @NotNull
    private Paragraph reportIsBasedOnBloodSampleArrivedAt() {
        return createContentParagraphTwice("This experiment is performed on the blood sample which arrived on ",
                Formats.formatDate(failReport.patientReporterData().getReferenceArrivalDate()),
                " with barcode ",
                Formats.formatNullableString(failReport.patientReporterData().getReferenceSampleBarcode()));
    }

    @NotNull
    private Paragraph reportIsBasedOnBloodAndTumorSamples() {
        return createContentParagraph("The results stated in this report are based on the tested tumor and blood sample.");
    }

    @NotNull
    private Paragraph reportIsGeneratedByPatientReporterVersion() {
        return createContentParagraphTwiceWithOneBold("This report is generated by patient reporter ",
                ReportResources.VERSION_REPORT,
                " based on ",
                failReport.qsFormNumber() + ".");
    }

    @NotNull
    private Paragraph forQuestionsPleaseContactHMF() {
        return createContentParagraph("For questions regarding the results described in this report, please contact ",
                ReportResources.CONTACT_EMAIL_GENERAL);
    }

    @NotNull
    private Paragraph sampleHasMolecularTumorPercentage() {
        String effectivePurity =
                failReport.wgsPurityString() != null ? failReport.wgsPurityString() : Integer.toString(failReport.patientReporterData().getShallowPurity());
        if (effectivePurity.equals("N/A") || effectivePurity.equals("N/A")) {
            return createContentParagraph("The tumor percentage based on molecular estimation", " could not be determined.");
        } else {
            return createContentParagraph("The tumor percentage based on molecular estimation is ", effectivePurity);
        }
    }

    @NotNull
    private Paragraph samplesAreEvaluatedAtHMFAndWithSampleID() {
        return createContentParagraphTwice("The biomaterials are evaluated at ",
                ReportResources.HARTWIG_ADDRESS,
                " and are known as HMF sample ID  ",
                failReport.patientReporterData().getReportingId());
    }

    @NotNull
    private Paragraph reportIsVerifiedByAndAddressedTo() {
        return createContentParagraph("This report was generated " + failReport.user() + " and is addressed to ",
                failReport.patientReporterData().getHospitalAddress() + ".");
    }

    @NotNull
    private Paragraph testsArePerformedByAccreditedLab() {
        return createContentParagraph(
                "The results on this report are based on tests that are performed under NEN-EN-ISO/IEC-17025:2017 TESTING L633 accreditation.");
    }

    @NotNull
    private Paragraph testsArePerformedUnderUNI() {
        return createContentParagraph("UDI-DI: ", failReport.udiDi() + ".");
    }

    @NotNull
    private Paragraph testsManual() {
        return createContentParagraph("The OncoAct user manual can be found at ", ReportResources.MANUAL + ".");
    }

    @NotNull
    private static Div createSampleDetailsDiv() {
        Div div = new Div();
        div.add(new Paragraph("Sample details").addStyle(ReportResources.smallBodyHeadingStyle()));
        return div;
    }

    @NotNull
    private static Div createDisclaimerDiv() {
        Div div = new Div();
        div.add(new Paragraph("Disclaimer").addStyle(ReportResources.smallBodyHeadingStyle()));
        return div;
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

    @NotNull
    private static Paragraph createContentParagraphTwiceWithOneBold(@NotNull String regularPart, @NotNull String boldPart,
            @NotNull String regularPart2, @NotNull String boldPart2) {
        return createContentParagraph(regularPart).add(new Text(boldPart).addStyle(ReportResources.smallBodyBoldTextStyle()))
                .add(regularPart2)
                .add(new Text(boldPart2).addStyle(ReportResources.smallBodyBoldTextStyle()))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

}
