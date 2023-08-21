package com.hartwig.oncoact.patientreporter.cfreport.chapters.failed;

import java.util.Set;

import com.google.common.collect.Sets;
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

public class QCFailChapter implements ReportChapter {

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
        Set<QCFailReason> tumorFailTitle = Sets.newHashSet(QCFailReason.HARTWIG_TUMOR_PROCESSING_ISSUE, QCFailReason.TCP_WGS_FAIL);

        if (tumorFailTitle.contains(failReport.reason())) {
            if (failReport.isCorrectedReport()) {
                return "OncoAct tumor WGS report \n- failed tumor analysis (Corrected)";
            } else {
                return "OncoAct tumor WGS report \n- failed tumor analysis";
            }
        } else {
            if (failReport.isCorrectedReport()) {
                return "OncoAct tumor WGS report \n- failed analysis (Corrected)";
            } else {
                return "OncoAct tumor WGS report \n- failed analysis";
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
        reportDocument.add(tumorLocationAndTypeTable.createTumorLocation(failReport.lamaPatientData().getPrimaryTumorType(),
                contentWidth()));

        reportDocument.add(new Paragraph("\nThe information regarding 'primary tumor location', 'primary tumor type' and 'biopsy location'"
                + "  \nis based on information received from the originating hospital.").addStyle(reportResources.subTextStyle()));
        reportDocument.add(LineDivider.createLineDivider(contentWidth()));

        reportDocument.add(createFailReasonDiv(failReport.failExplanation().reportReason(),
                failReport.failExplanation().reportExplanation(),
                failReport.failExplanation().sampleFailReasonComment()));
        reportDocument.add(LineDivider.createLineDivider(contentWidth()));
    }

    @NotNull
    private Div createFailReasonDiv(@NotNull String reportReason, @NotNull String reportExplanation,
            @Nullable String sampleFailReasonComment) {
        Div div = new Div();
        div.setKeepTogether(true);

        div.add(new Paragraph(reportReason).addStyle(reportResources.dataHighlightStyle()));
        div.add(new Paragraph(reportExplanation).addStyle(reportResources.bodyTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        if (sampleFailReasonComment != null) {
            div.add(new Paragraph(sampleFailReasonComment).addStyle(reportResources.subTextBoldStyle())
                    .setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        }
        return div;
    }
}