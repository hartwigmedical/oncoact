package com.hartwig.oncoact.patientreporter.cfreport.components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.hartwig.lama.client.model.BiopsySite;
import com.hartwig.lama.client.model.PatientReporterData;
import com.hartwig.oncoact.patientreporter.PanelReport;
import com.hartwig.oncoact.patientreporter.PatientReport;
import com.hartwig.oncoact.patientreporter.QsFormNumber;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.diagnosticsilo.DiagnosticSiloJsonInterpretation;
import com.hartwig.silo.diagnostic.client.model.PatientInformationResponse;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SidePanel {

    public static final float Y_START = 810;
    private static final float ROW_SPACING = 39;
    private static final float CONTENT_X_START = 420;
    private static final float RECTANGLE_WIDTH = 200;
    private static final float RECTANGLE_HEIGHT_SHORT = 120;

    @NotNull
    private final ReportResources reportResources;

    private static final Logger LOGGER = LogManager.getLogger(SidePanel.class);

    public SidePanel(@NotNull ReportResources reportResources) {
        this.reportResources = reportResources;
    }

    public void renderSidePatientReport(@NotNull PdfPage page, @NotNull PatientReport patientReport, boolean fullHeight) {
        renderSidePanel(page,
                patientReport.lamaPatientData(),
                patientReport.diagnosticSiloPatientData(),
                patientReport.reportDate(),
                fullHeight,
                !patientReport.qsFormNumber().equals(QsFormNumber.FOR_209.display()) && !patientReport.qsFormNumber()
                        .equals(QsFormNumber.FOR_080.display()));
    }

    public void renderSidePanelPanelReport(@NotNull PdfPage page, @NotNull PanelReport patientReport, boolean fullHeight) {
        renderSidePanel(page,
                patientReport.lamaPatientData(),
                patientReport.diagnosticSiloPatientData(),
                patientReport.reportDate(),
                fullHeight,
                !patientReport.qsFormNumber().equals(QsFormNumber.FOR_209.display()) && !patientReport.qsFormNumber()
                        .equals(QsFormNumber.FOR_080.display()));

    }

    public void renderSidePanel(@NotNull PdfPage page, @NotNull PatientReporterData lamaPatientData,
            @Nullable PatientInformationResponse patientInformationData, @NotNull String reportDate, boolean fullHeight,
            boolean isFailure) {
        PdfCanvas canvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), page.getDocument());
        Rectangle pageSize = page.getPageSize();
        renderBackgroundRect(fullHeight, canvas, pageSize, isFailure);

        int yCount = fullHeight ? (isFailure ? 120 : 13) : 3;
        var gridYStart = Y_START + 18;
        BaseMarker.renderMarkerGrid(5, yCount, CONTENT_X_START, 35, gridYStart, -ROW_SPACING, .05f, .15f, canvas);

        int sideTextIndex = -1;
        Canvas cv = new Canvas(canvas, page.getDocument(), page.getPageSize());

        if (lamaPatientData.getIsStudy()) {
            cv.add(createSidePanelDiv(++sideTextIndex, "Study id", lamaPatientData.getReportingId()));
        } else {
            cv.add(createSidePanelDiv(++sideTextIndex, "Hospital patient id", lamaPatientData.getReportingId()));
        }
        if (fullHeight) {
            if (lamaPatientData.getPathologyNumber() != null) {
                cv.add(createSidePanelDiv(++sideTextIndex, "Hospital pathology id", lamaPatientData.getPathologyNumber()));
            }
            if (lamaPatientData.getHospitalSampleLabel() != null) {
                cv.add(createSidePanelDiv(++sideTextIndex, "Hospital sample label", lamaPatientData.getHospitalSampleLabel()));
            }
        }

        cv.add(createSidePanelDiv(++sideTextIndex, "Report date", reportDate));

        if (fullHeight) {
            if (patientInformationData != null) {
                String name = DiagnosticSiloJsonInterpretation.determineName(patientInformationData);
                if (!name.equals(Strings.EMPTY)) {
                    cv.add(createSidePanelDiv(++sideTextIndex, "Name", name));
                }

                if (patientInformationData.getBirthdate() != null) {
                    LocalDate date = LocalDate.parse(patientInformationData.getBirthdate());
                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
                    String outputDateString = date.format(outputFormatter);
                    cv.add(createSidePanelDiv(++sideTextIndex, "Date of birth", outputDateString));
                }
            }

            if (lamaPatientData.getIsStudy()) {
                if (lamaPatientData.getStudyPI() != null) {
                    cv.add(createSidePanelDiv(++sideTextIndex, "Requested by", lamaPatientData.getStudyPI()));
                } else {
                    LOGGER.warn("Missing study PI");
                }
            } else {
                if (lamaPatientData.getRequesterName() != null) {
                    cv.add(createSidePanelDiv(++sideTextIndex, "Requested by", lamaPatientData.getRequesterName()));
                } else if (lamaPatientData.getStudyPI() != null) {
                    cv.add(createSidePanelDiv(++sideTextIndex, "Requested by", lamaPatientData.getStudyPI()));
                } else {
                    LOGGER.warn("Missing requester name");
                }
            }

            cv.add(createSidePanelDiv(++sideTextIndex, "Hospital", lamaPatientData.getOfficialHospitalName()));
            BiopsySite biopsySite = lamaPatientData.getBiopsySite();
            String biopsyLocation = "-";
            String biopsySubLocation = "-";
            String biopsyLateralisation = "-";
            String isPrimaryTumor = "-";
            if (biopsySite != null) {
                biopsyLocation = biopsySite.getLocation();
                biopsySubLocation = biopsySite.getSubLocation();
                biopsyLateralisation =
                        biopsySite.getLateralisation() != null ? biopsySite.getLateralisation().toString() : biopsyLateralisation;
                isPrimaryTumor = biopsySite.getIsPrimaryTumor() != null ? (biopsySite.getIsPrimaryTumor() ? "yes" : "no") : "-";
            }

            cv.add(createSidePanelDiv(++sideTextIndex, "Biopsy location", biopsyLocation));
            cv.add(createSidePanelDiv(++sideTextIndex, "Biopsy sublocation", biopsySubLocation));
            cv.add(createSidePanelDiv(++sideTextIndex, "Biopsy lateralisation", biopsyLateralisation));
            cv.add(createSidePanelDiv(++sideTextIndex, "Biopsy from primary tumor", isPrimaryTumor));
        }
        canvas.release();
    }

    private static void renderBackgroundRect(boolean fullHeight, @NotNull PdfCanvas canvas, @NotNull Rectangle pageSize,
            boolean isFailure) {
        if (isFailure) {
            canvas.rectangle(pageSize.getWidth(),
                    pageSize.getHeight(),
                    -RECTANGLE_WIDTH,
                    fullHeight ? -pageSize.getHeight() : -RECTANGLE_HEIGHT_SHORT);
        } else {
            float size = -pageSize.getHeight() / 2;
            canvas.rectangle(pageSize.getWidth(), pageSize.getHeight(), -RECTANGLE_WIDTH, fullHeight ? size - 70 : -RECTANGLE_HEIGHT_SHORT);
        }
        canvas.setFillColor(ReportResources.PALETTE_BLUE);
        canvas.fill();
    }

    @NotNull
    private Div createSidePanelDiv(int index, @NotNull String label, @NotNull String value) {
        float VALUE_TEXT_Y_OFFSET = 16;
        float MAX_WIDTH = 170;

        Div div = new Div();
        div.setKeepTogether(true);

        float yPos = Y_START - index * ROW_SPACING;
        div.add(new Paragraph(label.toUpperCase()).addStyle(reportResources.sidePanelLabelStyle())
                .setFixedPosition(CONTENT_X_START, yPos, MAX_WIDTH));

        float valueFontSize = ReportResources.maxPointSizeForWidth(reportResources.fontBold(), 11, 6, value, MAX_WIDTH);
        yPos -= VALUE_TEXT_Y_OFFSET;
        div.add(new Paragraph(value).addStyle(reportResources.sidePanelValueStyle().setFontSize(valueFontSize))
                .setHeight(15)
                .setFixedPosition(CONTENT_X_START, yPos, MAX_WIDTH)
                .setFixedLeading(valueFontSize));
        return div;
    }
}
