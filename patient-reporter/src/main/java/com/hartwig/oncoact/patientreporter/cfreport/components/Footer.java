package com.hartwig.oncoact.patientreporter.cfreport.components;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.patientreporter.PatientReporterApplication;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import org.jetbrains.annotations.NotNull;

public class Footer {

    private final ReportResources reportResources;

    public Footer(ReportResources reportResources) {
        this.reportResources = reportResources;
    }

    private final List<PageNumberTemplate> pageNumberTemplates = Lists.newArrayList();

    public void renderFooter(@NotNull PdfPage page, @NotNull String qsFormNumber, boolean fullWidth) {
        PdfCanvas canvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), page.getDocument());

        int pageNumber = page.getDocument().getPageNumber(page);
        String reporterVersion = PatientReporterApplication.VERSION != null ? PatientReporterApplication.VERSION : "X.X";
        String version = qsFormNumber + " v" + reporterVersion;

        PdfFormXObject pageNumberTemplate = new PdfFormXObject(new Rectangle(0, 0, 200, 20));
        canvas.addXObject(pageNumberTemplate, 58, 20);
        pageNumberTemplates.add(new PageNumberTemplate(pageNumber, version, pageNumberTemplate, reportResources));

        BaseMarker.renderMarkerGrid(5, 1, 156, 87, 22, 0, .2f, 0, canvas);

        canvas.release();
    }

    public void writeTotalPageCount(@NotNull PdfDocument document) {
        int totalPageCount = document.getNumberOfPages();
        for (PageNumberTemplate tpl : pageNumberTemplates) {
            tpl.renderPageNumber(totalPageCount, document);
        }
    }

    private static class PageNumberTemplate {

        private final int pageNumber;
        private final String qsFormNumber;
        @NotNull
        private final PdfFormXObject template;
        private final ReportResources reportResources;

        PageNumberTemplate(int pageNumber, String qsFormNumber, @NotNull PdfFormXObject template, @NotNull ReportResources reportResources) {
            this.pageNumber = pageNumber;
            this.qsFormNumber = qsFormNumber;
            this.template = template;
            this.reportResources = reportResources;
        }

        void renderPageNumber(int totalPageCount, @NotNull PdfDocument document) {
            String displayString = pageNumber + "/" + totalPageCount + " " + qsFormNumber;

            Canvas canvas = new Canvas(template, document);
            canvas.showTextAligned(new Paragraph().add(displayString).addStyle(reportResources.pageNumberStyle()),
                    0,
                    0,
                    TextAlignment.LEFT);
        }
    }
}
