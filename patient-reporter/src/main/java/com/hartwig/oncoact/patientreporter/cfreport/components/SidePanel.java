package com.hartwig.oncoact.patientreporter.cfreport.components;

import com.hartwig.oncoact.lims.cohort.LimsCohortConfig;
import com.hartwig.oncoact.patientreporter.PanelReport;
import com.hartwig.oncoact.patientreporter.PatientReport;
import com.hartwig.oncoact.patientreporter.PatientReporterApplication;
import com.hartwig.oncoact.patientreporter.SampleReport;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;

import org.jetbrains.annotations.NotNull;

public final class SidePanel {

    private static final float ROW_SPACING = 42;
    private static final float CONTENT_X_START = 455;
    private static final float RECTANGLE_WIDTH = 170;
    private static final float RECTANGLE_HEIGHT_SHORT = 110;

    private SidePanel() {
    }

    public static void renderSidePatientReport(@NotNull PdfPage page, @NotNull PatientReport patientReport, boolean fullHeight,
            boolean fullContent) {
        renderSidePanel(page,
                patientReport.sampleReport(),
                patientReport.reportDate(),
                patientReport.qsFormNumber(),
                fullHeight,
                fullContent);
    }

    public static void renderSidePanelPanelReport(@NotNull PdfPage page, @NotNull PanelReport patientReport, boolean fullHeight,
            boolean fullContent) {
        renderSidePanel(page,
                patientReport.sampleReport(),
                patientReport.reportDate(),
                patientReport.qsFormNumber(),
                fullHeight,
                fullContent);

    }

    public static void renderSidePanel(@NotNull PdfPage page, @NotNull SampleReport sampleReport, @NotNull String reportDate,
            @NotNull String qsFormNumber, boolean fullHeight, boolean fullContent) {
        PdfCanvas canvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), page.getDocument());
        Rectangle pageSize = page.getPageSize();
        renderBackgroundRect(fullHeight, canvas, pageSize);
        BaseMarker.renderMarkerGrid(4, (fullHeight ? 11 : 2), CONTENT_X_START, 35, 820, -ROW_SPACING, .05f, .15f, canvas);

        int sideTextIndex = -1;
        Canvas cv = new Canvas(canvas, page.getDocument(), page.getPageSize());

        cv.add(createSidePanelDiv(++sideTextIndex, "HMF sample id", sampleReport.sampleNameForReport()));
        cv.add(createSidePanelDiv(++sideTextIndex, "Report date", reportDate));
        cv.add(createSidePanelDiv(++sideTextIndex, "Name" , "Name"));
        cv.add(createSidePanelDiv(++sideTextIndex, "Birth date" , "Birth date"));

        LimsCohortConfig cohort = sampleReport.cohort();

        if (fullHeight && fullContent) {
            if (cohort.requireHospitalId() && !sampleReport.hospitalPatientId().isEmpty()) {
                cv.add(createSidePanelDiv(++sideTextIndex, "Hospital patient id", sampleReport.hospitalPatientId()));
            }

            if (cohort.requireHospitalPAId() && sampleReport.hospitalPathologySampleId() != null) {
                cv.add(createSidePanelDiv(++sideTextIndex, "Hospital pathology id", sampleReport.hospitalPathologySampleId()));
            }


            if (cohort.requireAdditionalInformationForSidePanel()) {
                cv.add(createSidePanelDiv(++sideTextIndex, "Requested by", sampleReport.hospitalContactData().requesterName()));
                cv.add(createSidePanelDiv(++sideTextIndex, "Email", sampleReport.hospitalContactData().requesterEmail()));
            }

            cv.add(createSidePanelDiv(++sideTextIndex, "Hospital", sampleReport.hospitalContactData().hospitalName()));
            cv.add(createSidePanelDiv(++sideTextIndex, "Biopsy location" , sampleReport.biopsyLocationString()));

        }

        canvas.release();
    }

    private static void renderBackgroundRect(boolean fullHeight, @NotNull PdfCanvas canvas, @NotNull Rectangle pageSize) {
        float size = -pageSize.getHeight()/4;
        canvas.rectangle(pageSize.getWidth(),
                pageSize.getHeight(),
                -RECTANGLE_WIDTH,
                fullHeight ? (size *2) + -(RECTANGLE_HEIGHT_SHORT/2): -RECTANGLE_HEIGHT_SHORT);
        canvas.setFillColor(ReportResources.PALETTE_BLUE);
        canvas.fill();
    }

    @NotNull
    private static Div createSidePanelDiv(int index, @NotNull String label, @NotNull String value) {
        float Y_START = 802;
        float VALUE_TEXT_Y_OFFSET = 18;
        float MAX_WIDTH = 120;

        Div div = new Div();
        div.setKeepTogether(true);

        float yPos = Y_START - index * ROW_SPACING;
        div.add(new Paragraph(label.toUpperCase()).addStyle(ReportResources.sidePanelLabelStyle())
                .setFixedPosition(CONTENT_X_START, yPos, MAX_WIDTH));

        float valueFontSize = ReportResources.maxPointSizeForWidth(ReportResources.fontBold(), 11, 6, value, MAX_WIDTH);
        yPos -= VALUE_TEXT_Y_OFFSET;
        div.add(new Paragraph(value).addStyle(ReportResources.sidePanelValueStyle().setFontSize(valueFontSize))
                .setHeight(15)
                .setFixedPosition(CONTENT_X_START, yPos, MAX_WIDTH)
                .setFixedLeading(valueFontSize));

        return div;
    }
}
