package com.hartwig.oncoact.patientreporter.cfreport.chapters.failed;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.LineDivider;
import com.hartwig.oncoact.patientreporter.cfreport.components.TumorLocationAndTypeTable;
import com.hartwig.oncoact.patientreporter.model.WgsReportFailed;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReason;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class QCFailChapter implements ReportChapter {

    @NotNull
    private final WgsReportFailed failReport;
    @NotNull
    private final ReportResources reportResources;
    private final TumorLocationAndTypeTable tumorLocationAndTypeTable;

    private final QCFailReason reason;
    private final boolean isCorrection;

    public QCFailChapter(@NotNull WgsReportFailed failReport, @NotNull ReportResources reportResources, @NotNull QCFailReason reason,
                         boolean isCorrection) {
        this.failReport = failReport;
        this.reportResources = reportResources;
        this.tumorLocationAndTypeTable = new TumorLocationAndTypeTable(reportResources);
        this.reason = reason;
        this.isCorrection = isCorrection;
    }

    @NotNull
    @Override
    public String name() {
        return Strings.EMPTY;
    }

    @NotNull
    @Override
    public String pdfTitle() {
        Set<QCFailReason> tumorFailTitle = Sets.newHashSet(QCFailReason.WGS_TUMOR_PROCESSING_ISSUE, QCFailReason.WGS_TCP_FAIL);

        if (tumorFailTitle.contains(reason)) {
            if (isCorrection) {
                return "OncoAct tumor WGS report \n- failed tumor analysis (Corrected)";
            } else {
                return "OncoAct tumor WGS report \n- failed tumor analysis";
            }
        } else {
            if (isCorrection) {
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
        reportDocument.add(tumorLocationAndTypeTable.createTumorLocation(failReport.tumorSample().primaryTumor(),
                contentWidth()));
        reportDocument.add(tumorLocationAndTypeTable.disclaimerTextTumorLocationBiopsyLocation().addStyle(reportResources.subTextStyle()));

        reportDocument.add(LineDivider.createLineDivider(contentWidth()));

        reportDocument.add(createFailReasonDiv(failReport.failedDatabase().reportReason(),
                failReport.failedDatabase().reportExplanation(),
                failReport.failedDatabase().sampleFailReasonComment()));
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