package com.hartwig.oncoact.patientreporter.cfreport;

import com.hartwig.oncoact.patientreporter.PatientReport;
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

class PageEventHandler implements IEventHandler {

    @NotNull
    private final PatientReport patientReport;

    @NotNull
    private final Footer footer;
    @NotNull
    private final Header header;
    @NotNull
    private final SidePanel sidePanel;

    private boolean fullSidebar;

    private String chapterTitle = "Undefined";
    private String pdfTitle = "Undefined";
    private boolean firstPageOfChapter = true;

    private PdfOutline outline = null;

    PageEventHandler(@NotNull final PatientReport patientReport, @NotNull final ReportResources reportResources) {
        this.patientReport = patientReport;
        this.sidePanel = new SidePanel(reportResources);
        this.header = new Header(patientReport.logoCompanyPath(), reportResources);
        this.footer = new Footer(reportResources);
    }

    @Override
    public void handleEvent(@NotNull Event event) {
        PdfDocumentEvent documentEvent = (PdfDocumentEvent) event;
        if (documentEvent.getType().equals(PdfDocumentEvent.START_PAGE)) {
            PdfPage page = documentEvent.getPage();

            header.renderHeader(chapterTitle, pdfTitle, firstPageOfChapter, page, patientReport.isWGSReport());
            if (firstPageOfChapter) {
                firstPageOfChapter = false;

                createChapterBookmark(documentEvent.getDocument(), chapterTitle);
            }
            sidePanel.renderSidePatientReport(page, patientReport, fullSidebar);
            footer.renderFooter(page, patientReport.qsFormNumber(), !fullSidebar);
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
