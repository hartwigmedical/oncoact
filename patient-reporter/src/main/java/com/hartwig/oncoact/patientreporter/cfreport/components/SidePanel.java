package com.hartwig.oncoact.patientreporter.cfreport.components;

import com.hartwig.lama.client.model.BiopsySite;
import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.PanelReport;
import com.hartwig.oncoact.patientreporter.PatientReport;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.util.Formats;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class SidePanel {

    private static final float ROW_SPACING = 42;
    private static final float CONTENT_X_START = 455;
    private static final float RECTANGLE_WIDTH = 170;
    private static final float RECTANGLE_HEIGHT_SHORT = 110;

    private SidePanel() {
    }

    public static void renderSidePatientReport(@NotNull PdfPage page, @NotNull PatientReport patientReport, boolean fullHeight) {
        renderSidePanel(page,
                patientReport.patientReporterData(),
                patientReport.reportDate(),
                fullHeight);
    }

    public static void renderSidePanelPanelReport(@NotNull PdfPage page, @NotNull PanelReport patientReport, boolean fullHeight) {
        renderSidePanel(page,
                patientReport.patientReporterData(),
                patientReport.reportDate(),
                fullHeight);

    }

    public static void renderSidePanel(@NotNull PdfPage page, @NotNull PatientReporterData patientReporterData, @NotNull String reportDate,
                                       boolean fullHeight) {
        PdfCanvas canvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), page.getDocument());
        Rectangle pageSize = page.getPageSize();
        renderBackgroundRect(fullHeight, canvas, pageSize);
        BaseMarker.renderMarkerGrid(4, (fullHeight ? 11 : 2), CONTENT_X_START, 35, 820, -ROW_SPACING, .05f, .15f, canvas);

        int sideTextIndex = -1;
        Canvas cv = new Canvas(canvas, page.getDocument(), page.getPageSize());


        if (patientReporterData.getReportingId().substring(0, 4).matches("[a-zA-Z]+")) {
            cv.add(createSidePanelDiv(++sideTextIndex, "Study id", patientReporterData.getReportingId()));
        } else {
            cv.add(createSidePanelDiv(++sideTextIndex, "Hospital patient id", patientReporterData.getReportingId()));

        }

        if (patientReporterData.getPathologyNumber() != null && !patientReporterData.getPathologyNumber().equals(Strings.EMPTY)) {
            cv.add(createSidePanelDiv(++sideTextIndex, "Hospital pathology id", patientReporterData.getPathologyNumber()));
        }

        cv.add(createSidePanelDiv(++sideTextIndex, "Report date", reportDate));
        cv.add(createSidePanelDiv(++sideTextIndex, "Name", "Name"));
        cv.add(createSidePanelDiv(++sideTextIndex, "Birth date", "Birth date"));


        if (patientReporterData.getRequesterName() != null && !patientReporterData.getRequesterName().equals(Strings.EMPTY)) {
            cv.add(createSidePanelDiv(++sideTextIndex, "Requested by", patientReporterData.getRequesterName()));

        }
        if (patientReporterData.getRequesterEmail() != null && !patientReporterData.getRequesterEmail().equals(Strings.EMPTY)) {
            cv.add(createSidePanelDiv(++sideTextIndex, "Email", patientReporterData.getRequesterEmail()));

        }

        cv.add(createSidePanelDiv(++sideTextIndex, "Hospital", patientReporterData.getHospitalName()));
        BiopsySite biopsySite = patientReporterData.getBiopsySite();
        String biopsyLocation = biopsySite != null && biopsySite.getLocation() != null ? biopsySite.getLocation() : Strings.EMPTY;
        String biopsySubLocation= biopsySite != null && biopsySite.getSubLocation() != null ? biopsySite.getSubLocation() : Strings.EMPTY;
        BiopsySite.LateralisationEnum biopsyLateralisation = biopsySite != null && biopsySite.getLateralisation() != null ? biopsySite.getLateralisation() : BiopsySite.LateralisationEnum.UNKNOWN;

        cv.add(createSidePanelDiv(++sideTextIndex, "Biopsy location", biopsyLocation));
        cv.add(createSidePanelDiv(++sideTextIndex, "Biopsy sublocation", biopsySubLocation));
        cv.add(createSidePanelDiv(++sideTextIndex, "Biopsy lateralisation", biopsyLateralisation.getValue()));

        if (biopsySite != null) {
            cv.add(createSidePanelDiv(++sideTextIndex, "Biopsy is primaryTumor", String.valueOf(biopsySite.getIsPrimaryTumor() )));
        }


        canvas.release();
    }

    private static void renderBackgroundRect(boolean fullHeight, @NotNull PdfCanvas canvas, @NotNull Rectangle pageSize) {
        float size = -pageSize.getHeight() / 4;
        canvas.rectangle(pageSize.getWidth(),
                pageSize.getHeight(),
                -RECTANGLE_WIDTH,
                fullHeight ? (size * 2) + -(RECTANGLE_HEIGHT_SHORT / 2) : -RECTANGLE_HEIGHT_SHORT);
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
