package com.hartwig.oncoact.patientreporter.cfreport;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.GsonBuilder;
import com.hartwig.oncoact.patientreporter.OutputFileUtil;
import com.hartwig.oncoact.patientreporter.PatientReport;
import com.hartwig.oncoact.patientreporter.ReportWriter;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed.CircosChapter;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed.DetailsAndDisclaimerChapter;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed.ExplanationChapter;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed.SummaryChapter;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.panel.*;
import com.hartwig.oncoact.patientreporter.model.WgsPatientReport;
import com.hartwig.oncoact.patientreporter.model.WgsReport;
import com.hartwig.oncoact.patientreporter.panel.PanelFailReport;
import com.hartwig.oncoact.patientreporter.panel.PanelReport;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReport;
import com.hartwig.oncoact.patientreporter.xml.ReportXML;
import com.hartwig.oncoact.patientreporter.xml.XMLFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.property.AreaBreakType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;

public class CFReportWriter implements ReportWriter {

    private static final Logger LOGGER = LogManager.getLogger(CFReportWriter.class);

    private final boolean writeToFile;

    @NotNull
    public static CFReportWriter createProductionReportWriter() {
        return new CFReportWriter(true);
    }

    @VisibleForTesting
    CFReportWriter(final boolean writeToFile) {
        this.writeToFile = writeToFile;
    }

    @Override
    public void writeAnalysedPatientReport(@NotNull WgsReport report, @NotNull String outputFilePath, @NotNull String logoCompanyPath, @NotNull String purpleCircosPlot, @NotNull String logoRVAPath, @NotNull String signaturePath) throws IOException {
        ReportResources reportResources = ReportResources.create();
        ReportChapter[] chapters = new ReportChapter[]{
                new SummaryChapter(report, reportResources),
                new CircosChapter(reportResources, purpleCircosPlot),
                new ExplanationChapter(reportResources),
                new DetailsAndDisclaimerChapter(report, reportResources, logoRVAPath, signaturePath)};
//        =
//                new ReportChapter[]{new SummaryChapter(report, reportResources), new ClinicalEvidenceChapter(report, reportResources),
//                        new GenomicAlterationsChapter(report, reportResources), new TumorCharacteristicsChapter(report, reportResources),
//                        new CircosChapter(report, reportResources), new ExplanationChapter(reportResources),
//                        new DetailsAndDisclaimerChapter(report, reportResources)};

        writeReport(reportResources, report, chapters, outputFilePath, logoCompanyPath);
    }

    @Override
    public void writeQCFailReport(@NotNull QCFailReport report, @NotNull String outputFilePath) throws IOException {
//        ReportResources reportResources = ReportResources.create();
//        if (report.reason().isDeepWGSDataAvailable()) {
//            Set<PurpleQCStatus> purpleQCStatuses = report.purpleQC();
//            if (purpleQCStatuses != null && !purpleQCStatuses.contains(PurpleQCStatus.FAIL_CONTAMINATION)) {
//                writeReport(reportResources,
//                        report,
//                        new ReportChapter[]{new QCFailChapter(report, reportResources), new QCFailPGXChapter(report, reportResources),
//                                new QCFailDisclaimerChapter(report, reportResources)},
//                        outputFilePath);
//            } else {
//                writeReport(reportResources,
//                        report,
//                        new ReportChapter[]{new QCFailChapter(report, reportResources),
//                                new QCFailDisclaimerChapter(report, reportResources)},
//                        outputFilePath);
//            }
//
//        } else {
//            writeReport(reportResources,
//                    report,
//                    new ReportChapter[]{new QCFailChapter(report, reportResources),
//                            new QCFailDisclaimerChapter(report, reportResources)},
//                    outputFilePath);
//        }

    }

    @Override
    public void writePanelAnalysedReport(@NotNull PanelReport report, @NotNull String outputFilePath) throws IOException {
        ReportResources reportResources = ReportResources.create();
        ReportChapter[] chapters =
                new ReportChapter[]{new PanelChapter(report, reportResources), new PanelExplanationChapter(reportResources),
                        new SampleAndDisclaimerChapter(report, reportResources)};
        writePanel(reportResources, report, chapters, outputFilePath);
    }

    @Override
    public void writePanelQCFailReport(@NotNull PanelFailReport report, @NotNull String outputFilePath) throws IOException {
        ReportResources reportResources = ReportResources.create();
        ReportChapter[] chapters = new ReportChapter[]{new PanelQCFailChapter(report, reportResources),
                new SampleAndDisclaimerChapterFail(report, reportResources)};
        writePanel(reportResources, report, chapters, outputFilePath);
    }

    public void writeJsonFailedFile(@NotNull QCFailReport report, @NotNull String outputFilePath) throws IOException {
        // writeReportDataToJson(report, outputFilePath);
    }

    public void writeJsonAnalysedFile(@NotNull WgsReport report, @NotNull String outputFilePath) throws IOException {
        writeReportDataToJson(report, outputFilePath);
    }

    public void writeReportDataToJson(@NotNull WgsPatientReport report, @NotNull String outputDirData) throws IOException {
        if (writeToFile) {
//            String outputFileData = outputDirData + File.separator + OutputFileUtil.generateOutputFileName(report) + ".json";
//            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileData));
//            writer.write(convertToJson(report));
//            writer.close();
//            LOGGER.info(" Created report data json file at {} ", outputFileData);
        }
    }

    public void writeXMLAnalysedFile(@NotNull WgsReport report, @NotNull String outputFilePath) throws IOException {
        ReportXML xmlReport = XMLFactory.generateXMLData(report);
        writeReportDataToXML(xmlReport, outputFilePath, report);
    }

    public void writeReportDataToXML(@NotNull ReportXML importWGS, @NotNull String outputDirData, @NotNull WgsPatientReport report)
            throws IOException {
        if (writeToFile) {

//            XmlMapper xmlMapper = new XmlMapper();
//            xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
//
//            String outputFileData = outputDirData + File.separator + OutputFileUtil.generateOutputFileName(report) + ".xml";
//
//            xmlMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFileData), importWGS);
//
//            LOGGER.info(" Created report data xml file at {} ", outputFileData);
        }
    }

    @VisibleForTesting
    @NotNull
    public String convertToJson(@NotNull PatientReport report) {
        return new GsonBuilder().serializeNulls()
                .serializeSpecialFloatingPointValues()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(report);
    }

    private void writeReport(@NotNull ReportResources reportResources, @NotNull WgsReport patientReport,
                             @NotNull ReportChapter[] chapters, @NotNull String outputFilePath, @NotNull String logoCompanyPath) throws IOException {
        Document doc = initializeReport(outputFilePath, writeToFile);
        PdfDocument pdfDocument = doc.getPdfDocument();

        PageEventHandler pageEventHandler = new PageEventHandler(patientReport, reportResources, logoCompanyPath);
        pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, pageEventHandler);

        for (int i = 0; i < chapters.length; i++) {
            ReportChapter chapter = chapters[i];

            pageEventHandler.pdfTitle(chapter.pdfTitle());
            pageEventHandler.chapterTitle(chapter.name());
            pageEventHandler.resetChapterPageCounter();
            pageEventHandler.sidebarType(!chapter.isFullWidth());

            if (i > 0) {
                doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
            chapter.render(doc);
        }

        pageEventHandler.writeDynamicTextParts(doc.getPdfDocument());

        doc.close();
        pdfDocument.close();

        if (writeToFile) {
            LOGGER.info("Created patient report at {}", outputFilePath);
        } else {
            LOGGER.info("Successfully generated in-memory patient report");
        }
    }

    public void writeJsonPanelFile(@NotNull PanelReport report, @NotNull String outputFilePath) throws IOException {
        writeReportDataToJson(report, outputFilePath);
    }

    public void writeJsonPanelFailedFile(@NotNull PanelFailReport report, @NotNull String outputFilePath) throws IOException {
        writeReportDataToJson(report, outputFilePath);
    }

    public void writeReportDataToJson(@NotNull com.hartwig.oncoact.patientreporter.PanelReport report, @NotNull String outputDirData)
            throws IOException {
        if (writeToFile) {
            String outputFileData = outputDirData + File.separator + OutputFileUtil.generateOutputFileName(report) + ".json";
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileData));
            writer.write(convertToJson(report));
            writer.close();
            LOGGER.info(" Created report data json file at {} ", outputFileData);
        }
    }

    @VisibleForTesting
    @NotNull
    public String convertToJson(@NotNull com.hartwig.oncoact.patientreporter.PanelReport report) {
        return new GsonBuilder().serializeNulls()
                .serializeSpecialFloatingPointValues()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(report);
    }

    private void writePanel(@NotNull ReportResources reportResources,
                            @NotNull com.hartwig.oncoact.patientreporter.PanelReport patientReport, @NotNull ReportChapter[] chapters,
                            @NotNull String outputFilePath) throws IOException {
        Document doc = initializeReport(outputFilePath, writeToFile);
        PdfDocument pdfDocument = doc.getPdfDocument();

        PageEventHandlerPanel pageEventHandler = new PageEventHandlerPanel(patientReport, reportResources);
        pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, pageEventHandler);

        for (int i = 0; i < chapters.length; i++) {
            ReportChapter chapter = chapters[i];

            pageEventHandler.pdfTitle(chapter.pdfTitle());
            pageEventHandler.chapterTitle(chapter.name());
            pageEventHandler.resetChapterPageCounter();
            pageEventHandler.sidebarType(!chapter.isFullWidth());

            if (i > 0) {
                doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
            chapter.render(doc);
        }

        pageEventHandler.writeDynamicTextParts(doc.getPdfDocument());

        doc.close();
        pdfDocument.close();

        if (writeToFile) {
            LOGGER.info("Created patient report at {}", outputFilePath);
        } else {
            LOGGER.info("Successfully generated in-memory patient report");
        }
    }

    @NotNull
    private static Document initializeReport(@NotNull String outputFilePath, boolean writeToFile) throws IOException {
        PdfWriter writer;
        if (writeToFile) {
            var path = new File(outputFilePath).toPath();
            Files.deleteIfExists(path);

            writer = new PdfWriter(outputFilePath);
        } else {
            // Write output to output stream where it is effectively ignored.
            writer = new PdfWriter(new ByteArrayOutputStream());
        }

        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(PageSize.A4);
        pdf.getDocumentInfo().setTitle(ReportResources.METADATA_TITLE);
        pdf.getDocumentInfo().setAuthor(ReportResources.METADATA_AUTHOR);

        Document document = new Document(pdf);
        document.setMargins(ReportResources.PAGE_MARGIN_TOP,
                ReportResources.PAGE_MARGIN_RIGHT,
                ReportResources.PAGE_MARGIN_BOTTOM,
                ReportResources.PAGE_MARGIN_LEFT);

        return document;
    }
}
