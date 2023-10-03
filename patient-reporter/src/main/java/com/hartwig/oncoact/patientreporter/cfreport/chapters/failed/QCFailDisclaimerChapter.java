package com.hartwig.oncoact.patientreporter.cfreport.chapters.failed;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.ReportSignature;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.lama.LamaInterpretation;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReason;
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
    private final ReportResources reportResources;

    public QCFailDisclaimerChapter(@NotNull QCFailReport failReport, @NotNull ReportResources reportResources) {
        this.failReport = failReport;
        this.reportResources = reportResources;
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
    public void render(@NotNull Document reportDocument) {
        reportDocument.add(createContentBody());
        ReportSignature reportSignature = ReportSignature.create(reportResources);
        reportDocument.add(reportSignature.createSignatureDiv(failReport.logoRVAPath(), failReport.signaturePath()).setMarginTop(15));
        reportDocument.add(reportSignature.createEndOfReportIndication());
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
        Set<QCFailReason> qcFailReasons = Sets.newHashSet(QCFailReason.WGS_PROCESSING_ISSUE,
                QCFailReason.WGS_TCP_SHALLOW_FAIL,
                QCFailReason.WGS_TCP_FAIL,
                QCFailReason.WGS_TUMOR_PROCESSING_ISSUE,
                QCFailReason.WGS_PIPELINE_FAIL);

        Div div = createSampleDetailsDiv();
        div.add(samplesAreEvaluatedAtHMFAndWithSampleID());
        div.add(generateHMFAndPathologySampleIDParagraph(failReport));
        div.add(reportIsBasedOnTumorSampleArrivedAt());
        div.add(resultsAreObtainedBetweenDates());
        if (qcFailReasons.contains(failReport.reason())) {
            div.add(reportIsBasedOnBloodSampleArrivedAt());
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
        div.add(forComplaintsPleaseContactHMF());
        return div;
    }

    @NotNull
    private Paragraph resubmitSample() {
        return createContentParagraph("If available new biomaterial(s) can be provided for a new assessment, please contact ",
                "info@hartwigmedicalfoundation.nl");
    }

    @NotNull
    private Paragraph generateHMFAndPathologySampleIDParagraph(@NotNull QCFailReport failReport) {
        String pathologyId = failReport.lamaPatientData().getPathologyNumber();
        String reportingId = failReport.lamaPatientData().getReportingId();

        String interpretId;
        if (failReport.lamaPatientData().getIsStudy()) {
            interpretId = "The study ID is: ";
        } else {
            interpretId = "The hospital patient ID is: ";
        }

        if (pathologyId != null) {
            return createContentParagraphTwice(interpretId, reportingId, " and the pathology tissue ID is: ", pathologyId);
        } else {
            return createContentParagraph(interpretId, reportingId);
        }
    }

    @NotNull
    private Paragraph resultsAreObtainedBetweenDates() {
        String earliestArrivalDate = LamaInterpretation.extractEarliestArrivalDate(failReport.lamaPatientData().getReferenceArrivalDate(),
                failReport.lamaPatientData().getTumorArrivalDate());
        return createContentParagraphTwice("The results in this report have been obtained between ",
                earliestArrivalDate != null ? earliestArrivalDate : Formats.NA_STRING,
                " and ",
                failReport.reportDate());
    }

    @NotNull
    private Paragraph reportIsBasedOnTumorSampleArrivedAt() {
        return createContentParagraphTwice("This experiment is performed on the tumor sample as arrived on ",
                Formats.formatDate(failReport.lamaPatientData().getTumorArrivalDate()),
                " with barcode ",
                failReport.lamaPatientData().getTumorSampleBarcode());
    }

    @NotNull
    private Paragraph reportIsBasedOnBloodSampleArrivedAt() {
        return createContentParagraphTwice("This analysis is performed on the reference sample as arrived on ",
                Formats.formatDate(failReport.lamaPatientData().getReferenceArrivalDate()),
                " with barcode ",
                Formats.formatNullableString(failReport.lamaPatientData().getReferenceSampleBarcode()));
    }

    @NotNull
    private Paragraph reportIsBasedOnBloodAndTumorSamples() {
        return createContentParagraph("The results stated in this report are based on the tested tumor and reference sample.");
    }

    @NotNull
    private Paragraph reportIsGeneratedByPatientReporterVersion() {
        return createContentParagraph("This report is generated using the OncoAct reporting pipeline version ", "1.0.");
    }

    @NotNull
    private Paragraph forQuestionsPleaseContactHMF() {
        return createContentParagraph("For questions regarding the results described in this report, please contact ",
                ReportResources.CONTACT_EMAIL_GENERAL);
    }

    @NotNull
    private Paragraph forComplaintsPleaseContactHMF() {
        return createContentParagraph("For feedback or complaints please contact ", "qualitysystem@hartwigmedicalfoundation.nl.");
    }

    @NotNull
    private Paragraph samplesAreEvaluatedAtHMFAndWithSampleID() {
        return createContentParagraph("The biomaterials are evaluated at ", ReportResources.HARTWIG_ADDRESS);
    }

    @NotNull
    private Paragraph reportIsVerifiedByAndAddressedTo() {
        return createContentParagraph("This report was generated " + failReport.user() + " and is addressed to ",
                failReport.lamaPatientData().getHospitalAddress() + ".");
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
    private Div createSampleDetailsDiv() {
        Div div = new Div();
        div.add(new Paragraph("Sample details").addStyle(reportResources.smallBodyHeadingStyle()));
        return div;
    }

    @NotNull
    private Div createDisclaimerDiv() {
        Div div = new Div();
        div.add(new Paragraph("Disclaimers").addStyle(reportResources.smallBodyHeadingStyle()));
        return div;
    }

    @NotNull
    private Paragraph createContentParagraphRed(@NotNull String text) {
        return new Paragraph(text).addStyle(reportResources.smallBodyTextStyleRed()).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private Paragraph createContentParagraph(@NotNull String text) {
        return new Paragraph(text).addStyle(reportResources.smallBodyTextStyle()).setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private Paragraph createContentParagraph(@NotNull String regularPart, @NotNull String boldPart) {
        return createContentParagraph(regularPart).add(new Text(boldPart).addStyle(reportResources.smallBodyBoldTextStyle()))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private Paragraph createContentParagraphTwice(@NotNull String regularPart, @NotNull String boldPart, @NotNull String regularPart2,
            @NotNull String boldPart2) {
        return createContentParagraph(regularPart).add(new Text(boldPart).addStyle(reportResources.smallBodyBoldTextStyle()))
                .add(regularPart2)
                .add(new Text(boldPart2).addStyle(reportResources.smallBodyBoldTextStyle()))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private Paragraph createContentParagraphTwiceWithOneBold(@NotNull String regularPart, @NotNull String boldPart,
            @NotNull String regularPart2, @NotNull String boldPart2) {
        return createContentParagraph(regularPart).add(new Text(boldPart).addStyle(reportResources.smallBodyBoldTextStyle()))
                .add(regularPart2)
                .add(new Text(boldPart2).addStyle(reportResources.smallBodyBoldTextStyle()))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

}