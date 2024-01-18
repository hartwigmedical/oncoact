package com.hartwig.oncoact.patientreporter.cfreport;

import java.util.Random;

import com.hartwig.oncoact.patientreporter.PanelReport;
import com.hartwig.oncoact.patientreporter.cfreport.components.Footer;
import com.hartwig.oncoact.patientreporter.cfreport.components.Header;
import com.hartwig.oncoact.patientreporter.cfreport.components.SidePanel;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitRemoteGoToDestination;

import org.jetbrains.annotations.NotNull;

class PageEventHandlerPanel implements IEventHandler {

    @NotNull
    private final PanelReport patientReport;

    private final Footer footer;
    private final Header header;
    private final SidePanel sidePanel;
    private boolean fullSidebar;
    private String chapterTitle = "Undefined";
    private String pdfTitle = "Undefined";
    private boolean firstPageOfChapter = true;

    private PdfOutline outline = null;

    PageEventHandlerPanel(@NotNull final PanelReport patientReport, @NotNull final ReportResources reportResources) {
        this.patientReport = patientReport;
        var random = new Random(patientReport.lamaPatientData().getTumorSampleBarcode().hashCode());
        this.header = new Header(patientReport.logoCompanyPath(), reportResources);
        this.footer = new Footer(reportResources, random);
        this.sidePanel = new SidePanel(reportResources, random);
    }

    @Override
    public void handleEvent(@NotNull Event event) {
        PdfDocumentEvent documentEvent = (PdfDocumentEvent) event;
        if (documentEvent.getType().equals(PdfDocumentEvent.START_PAGE)) {
            PdfPage page = documentEvent.getPage();

            header.renderHeader(chapterTitle, pdfTitle, firstPageOfChapter, page);
            if (firstPageOfChapter) {
                firstPageOfChapter = false;

                createChapterBookmark(documentEvent.getDocument(), chapterTitle);
            }
            sidePanel.renderSidePanelPanelReport(page, patientReport, fullSidebar);
            footer.renderFooter(page, patientReport.qsFormNumber());
        }
    }

    void pdfTitle(@NotNull String pdfTitle) {
        this.pdfTitle = pdfTitle;
    }

    void chapterTitle(@NotNull String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    void sidebarType(boolean full) {
        fullSidebar = full;
    }

    void resetChapterPageCounter() {
        firstPageOfChapter = true;
    }

    void writeDynamicTextParts(@NotNull PdfDocument document) {
        header.writeChapterTitles(document);
        footer.writeTotalPageCount(document);
    }

    private void createChapterBookmark(@NotNull PdfDocument pdf, @NotNull String title) {
        if (outline == null) {
            outline = pdf.getOutlines(false);
        }

        PdfOutline chapterItem = outline.addOutline(title);
        chapterItem.addDestination(PdfExplicitRemoteGoToDestination.createFitH(pdf.getNumberOfPages(), 0));
    }
}
