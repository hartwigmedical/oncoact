package com.hartwig.oncoact.patientreporter.cfreport.chapters.failed;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.LineDivider;
import com.hartwig.oncoact.patientreporter.cfreport.components.TumorLocationAndTypeTable;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReason;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class QCFailChapter implements ReportChapter {

    @NotNull
    private final QCFailReport failReport;

    public QCFailChapter(@NotNull QCFailReport failReport) {
        this.failReport = failReport;
    }

    @NotNull
    @Override
    public String name() {
        return Strings.EMPTY;
    }

    @NotNull
    @Override
    public String pdfTitle() {
        Set<QCFailReason> tumorFailTitle = Sets.newHashSet(QCFailReason.HARTWIG_TUMOR_PROCESSING_ISSUE, QCFailReason.TCP_WGS_FAIL);

        if (tumorFailTitle.contains(failReport.reason())) {
            if (failReport.isCorrectedReport()) {
                return "OncoAct tumor WGS report - failed tumor analysis (Corrected)";
            } else {
                return "OncoAct tumor WGS report - failed tumor analysis";
            }
        } else {
            if (failReport.isCorrectedReport()) {
                return "OncoAct tumor WGS report - failed analysis (Corrected)";
            } else {
                return "OncoAct tumor WGS report - failed analysis";
            }
        }
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
        reportDocument.add(TumorLocationAndTypeTable.createTumorLocation(failReport.lamaPatientData().getPrimaryTumorType(), contentWidth()));

        reportDocument.add(new Paragraph("The information regarding 'primary tumor location', 'primary tumor type' and 'biopsy location'"
                + " is based on information received from the originating hospital.").addStyle(ReportResources.subTextSmallStyle()));
        reportDocument.add(LineDivider.createLineDivider(contentWidth()));

        reportDocument.add(createFailReasonDiv(failReport.failExplanation().reportReason(),
                failReport.failExplanation().reportExplanation(),
                failReport.failExplanation().sampleFailReasonComment()));
        reportDocument.add(LineDivider.createLineDivider(contentWidth()));
    }

    @NotNull
    private static Div createFailReasonDiv(@NotNull String reportReason, @NotNull String reportExplanation,
                                           @Nullable String sampleFailReasonComment) {
        Div div = new Div();
        div.setKeepTogether(true);

        div.add(new Paragraph(reportReason).addStyle(ReportResources.dataHighlightStyle()));
        div.add(new Paragraph(reportExplanation).addStyle(ReportResources.bodyTextStyle()).setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        if (sampleFailReasonComment != null) {
            div.add(new Paragraph(sampleFailReasonComment).addStyle(ReportResources.subTextBoldStyle())
                    .setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        }
        return div;
    }
}