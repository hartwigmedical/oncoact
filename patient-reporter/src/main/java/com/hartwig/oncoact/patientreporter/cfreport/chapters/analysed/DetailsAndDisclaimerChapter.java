package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import com.hartwig.oncoact.patientreporter.algo.AnalysedPatientReport;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.ReportSignature;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.lama.LamaInterpretation;
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

public class DetailsAndDisclaimerChapter implements ReportChapter {

    @NotNull
    private final AnalysedPatientReport patientReport;
    @NotNull
    private final ReportResources reportResources;

    public DetailsAndDisclaimerChapter(@NotNull AnalysedPatientReport patientReport, @NotNull ReportResources reportResources) {
        this.patientReport = patientReport;
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
    public void render(@NotNull Document reportDocument) throws IOException {
        ReportSignature reportSignature = ReportSignature.create(reportResources);
        var signatureDiv = reportSignature.createSignatureDiv(patientReport.logoRVAPath(), patientReport.signaturePath());
        var endOfReportIndication = reportSignature.createEndOfReportIndication();
        var chapterTable = new Table(UnitValue.createPercentArray(new float[] { 1, 0.1f, 1 })).setWidth(contentWidth())
                .addCell(TableUtil.createLayoutCell().add(createSampleDetailsColumn()))
                .addCell(TableUtil.createLayoutCell())
                .addCell(TableUtil.createLayoutCell().add(createDisclaimerColumn()))
                .addCell(TableUtil.createLayoutCell().add(signatureDiv))
                .addCell(TableUtil.createLayoutCell())
                .addCell(TableUtil.createLayoutCell().add(endOfReportIndication));
        reportDocument.add(chapterTable);
    }

    @NotNull
    private Div createSampleDetailsColumn() {
        Div div = new Div();

        div.add(new Paragraph("Sample details").addStyle(reportResources.smallBodyHeadingStyle()));

        div.add(createContentParagraph("The samples have been sequenced at ", ReportResources.HARTWIG_ADDRESS, "."));

        div.add(generateHMFAndPathologySampleIDParagraph(patientReport));

        String earliestArrivalDate =
                LamaInterpretation.extractEarliestArrivalDate(patientReport.lamaPatientData().getReferenceArrivalDate(),
                        patientReport.lamaPatientData().getTumorArrivalDate());
        div.add(createContentParagraphTwice("The results in this report have been obtained between ",
                Formats.formatNullableString(earliestArrivalDate),
                " and ",
                patientReport.reportDate(),
                "."));

        div.add(createContentParagraphTwice("This analysis is performed on the tumor sample as arrived on ",
                Formats.formatDate(patientReport.lamaPatientData().getTumorArrivalDate()),
                " with barcode ",
                patientReport.lamaPatientData().getTumorSampleBarcode(),
                "."));
        div.add(createContentParagraphTwice("This analysis is performed on the reference sample as arrived on ",
                Formats.formatDate(patientReport.lamaPatientData().getReferenceArrivalDate()),
                " with barcode ",
                Formats.formatNullableString(patientReport.lamaPatientData().getReferenceSampleBarcode()),
                "."));
        div.add(createContentParagraph("The results stated in this report are based on the tested tumor and reference sample."));
        div.add(createContentParagraph("This experiment is performed according to lab procedures: ",
                patientReport.lamaPatientData().getSopString()));

        div.add(createContentParagraph("This report is addressed to: ",
                LamaInterpretation.hospitalContactReport(patientReport.lamaPatientData()),
                "."));

        patientReport.comments().ifPresent(comments -> div.add(createContentParagraphRed("Comments: " + comments, ".")));

        return div;
    }

    @NotNull
    private Div createDisclaimerColumn() {
        String pipelineVersion = patientReport.pipelineVersion() == null ? "No pipeline version is known" : patientReport.pipelineVersion();
        Div div = new Div();

        div.add(new Paragraph("Disclaimers").addStyle(reportResources.smallBodyHeadingStyle()));

        div.add(createContentParagraph("The data on which this report is based is generated "
                + "from tests that are performed under NEN-EN-ISO/IEC-17025:2017 TESTING L633 accreditation and have passed all internal quality controls."));
        div.add(createContentParagraphTwice("This report is generated using the molecular pipeline version ",
                pipelineVersion,
                " and OncoAct reporting pipeline version ",
                "1.0."));
        div.add(createContentParagraph("(basic) UDI-DI: ", patientReport.udiDi() + "."));

        String whoVerified = "This report was generated " + patientReport.user();
        div.add(createContentParagraph(whoVerified, "."));

        div.add(createContentParagraph("The ‘primary tumor location’ and ‘primary tumor type’ have influence on the "
                + "clinical evidence/study matching. No check is performed to verify the received information."));
        div.add(createContentParagraph("The conclusion of this report is based solely on the results of the whole genome sequencing "
                + "of the received biomaterials, and the additional primary tumor location and type information received from the "
                + "hospital. Further interpretation of these results within the patient’s clinical context is required by a clinician "
                + "with support of a molecular tumor board."));
        div.add(createContentParagraph("Based on a implied tumor purity of at least 20%, the test has a sensitivity of > 95% for "
                + "detection of tumor observed variants, tumor observed gains and losses, tumor observed gene fusions and tumor observed "
                + "gene/homozygous disruptions."));
        div.add(createContentParagraph("Hartwig Medical Foundation is not responsible for the content of all external data sources "
                + "used to do the analyses and generate this report. Hartwig Medical Foundation is not liable and cannot be held "
                + "accountable for any incorrectness, incompleteness or error of any other kind in these data sources, or the external "
                + "software used to harmonize and curate these data sources."));
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
    private Paragraph generateHMFAndPathologySampleIDParagraph(@NotNull AnalysedPatientReport patientReport) {
        String pathologyId = patientReport.lamaPatientData().getPathologyNumber();
        String reportingId = patientReport.lamaPatientData().getReportingId();

        String interpretId;
        if (patientReport.lamaPatientData().getIsStudy()) {
            interpretId = "The study ID is: ";
        } else {
            interpretId = "The hospital patient ID is: ";
        }

        if (pathologyId != null) {
            return createContentParagraphTwice(interpretId, reportingId, " and the pathology tissue ID is: ", pathologyId, ".");
        } else {
            return createContentParagraph(interpretId, reportingId, ".");
        }
    }

    @NotNull
    private Paragraph createContentParagraphRed(@NotNull String text, @NotNull String text1) {
        return new Paragraph(text).addStyle(reportResources.smallBodyTextStyleRed())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(text1)
                .addStyle(reportResources.smallBodyTextStyle());
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
    private Paragraph createContentParagraph(@NotNull String regularPart, @NotNull String boldPart, @NotNull String regularPart1) {
        return createContentParagraph(regularPart).add(new Text(boldPart).addStyle(reportResources.smallBodyBoldTextStyle()))
                .add(regularPart1)
                .addStyle(reportResources.smallBodyTextStyle())
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
    private Paragraph createContentParagraphTwice(@NotNull String regularPart, @NotNull String boldPart, @NotNull String regularPart2,
            @NotNull String boldPart2, @NotNull String regularPar3) {
        return createContentParagraph(regularPart).add(new Text(boldPart).addStyle(reportResources.smallBodyBoldTextStyle()))
                .add(regularPart2)
                .add(new Text(boldPart2).addStyle(reportResources.smallBodyBoldTextStyle()))
                .add(new Text(regularPar3).addStyle(reportResources.smallBodyTextStyle()))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

}