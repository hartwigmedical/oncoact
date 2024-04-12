package com.hartwig.oncoact.patientreporter.cfreport.chapters.failed;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.model.HlaAlleleFail;
import com.hartwig.oncoact.patientreporter.model.WgsReportFailed;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QCFailPGXChapter implements ReportChapter {

    @NotNull
    private final WgsReportFailed failReport;
    @NotNull
    private final ReportResources reportResources;
    @NotNull
    private final TableUtil tableUtil;

    public QCFailPGXChapter(@NotNull WgsReportFailed failReport, @NotNull ReportResources reportResources) {
        this.failReport = failReport;
        this.reportResources = reportResources;
        this.tableUtil = new TableUtil(reportResources);
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return Strings.EMPTY;
    }

    @Override
    @NotNull
    public String name() {
        return "Genotypes";
    }

    @Override
    public void render(@NotNull Document reportDocument) {
        reportDocument.add(createPharmacogeneticsGenotypesTable(failReport.failGenomic().pharmacogenetics()));
        reportDocument.add(createHlaTable(failReport.failGenomic().hlaAlleles(), failReport.failGenomic().hlaQc()));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1}));
        table.setWidth(contentWidth());

        table.addCell(TableUtil.createLayoutCell().add(createSectionTitle("Details on the reported pharmacogenetics")));
        table.addCell(TableUtil.createLayoutCell()
                .add(new Div().add(new Paragraph().add(new Text(
                                "The pharmacogenetic haplotypes are reported based on germline analysis. The "))
                        .add(new Text("PharmGKB database ").addStyle(reportResources.urlStyle())
                                .setAction(PdfAction.createURI("https://www.pharmgkb.org/")))
                        .add(new Text(
                                "is used to annotate the observed haplotypes. Details on the pharmacogenetic haplotypes and advice on related treatment adjustments can be downloaded from the "))
                        .add(new Text("resources").addStyle(reportResources.urlStyle())
                                .setAction(PdfAction.createURI(
                                        "https://storage.googleapis.com/hmf-public/OncoAct-Resources/latest_oncoact.zip")))
                        .add(new Text("."))
                        .addStyle(reportResources.subTextStyle())
                        .setFixedLeading(ReportResources.BODY_TEXT_LEADING)))
                .add(createContentDiv(new String[]{
                        "The called haplotypes for a gene are the simplest combination of haplotypes that perfectly explains all of the "
                                + "observed variants for that gene. If no combination of haplotypes in the panel can perfectly explain the "
                                + "observed variants, then 'Unresolved Haplotype' is called.",
                        "Wild type is assumed when no variants are observed."})));
        table.addCell(TableUtil.createLayoutCell());
        table.addCell(TableUtil.createLayoutCell(1, 1).setHeight(30));

        table.addCell(TableUtil.createLayoutCell().add(createSectionTitle("Details on the reported HLA Alleles")));

        table.addCell(TableUtil.createLayoutCell()
                .add(createContentDiv(new String[]{"HLA Class I types (HLA-A, HLA-B and HLA-C) are reported based on germline analysis."}))
                .add(createContentDivWithLinkThree("The IMGT/HLA ",
                        "database ",
                        "is used as a reference set of Human MHC class I alleles. HLA typing is done to 4-digits, which means it uniquely identifies a specific protein, but ignores synonymous variants (6 digits) and intronic differences (8 digits).",
                        "https://www.ebi.ac.uk/ipd/imgt/hla")));
        reportDocument.add(table);
    }

    @NotNull
    private Table createHlaTable(@NotNull List<HlaAlleleFail> hlaAlleleFail, @NotNull String hlaQc) {

        String title = "HLA Alleles";
        Table table = TableUtil.createReportContentTable(new float[]{10, 10, 10},
                new Cell[]{tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Germline allele"),
                        tableUtil.createHeaderCell("Germline copies")},
                ReportResources.CONTENT_WIDTH_WIDE_SMALL);
        if (!hlaQc.equals("PASS")) {
            String noConsent = "The QC of the HLA types do not meet the QC cut-offs";
            return tableUtil.createNoConsentReportTable(title,
                    noConsent,
                    TableUtil.TABLE_BOTTOM_MARGIN,
                    ReportResources.CONTENT_WIDTH_WIDE_SMALL);
        } else if (hlaAlleleFail.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE_SMALL);
        } else {
            for (HlaAlleleFail hlaAllele : hlaAlleleFail) {
                table.addCell(tableUtil.createContentCell(hlaAllele.gene()));
                table.addCell(tableUtil.createTransparentCell(hlaAllele.germlineAllele()));
                table.addCell(tableUtil.createTransparentCell(hlaAllele.germlineCopies()));
            }
        }
        return tableUtil.createWrappingReportTable(title, null, table, TableUtil.TABLE_BOTTOM_MARGIN);
    }

    @NotNull
    private Table createPharmacogeneticsGenotypesTable(@NotNull List<com.hartwig.oncoact.patientreporter.model.Pharmacogenetics> pharmacogeneticsData) {
        String title = "Pharmacogenetics";

        if (pharmacogeneticsData.isEmpty()) {
            return tableUtil.createNoneReportTable(title, null, TableUtil.TABLE_BOTTOM_MARGIN, ReportResources.CONTENT_WIDTH_WIDE);
        } else {
            Table contentTable = TableUtil.createReportContentTable(new float[]{60, 60, 60, 100, 60},
                    new Cell[]{tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Genotype"),
                            tableUtil.createHeaderCell("Function"), tableUtil.createHeaderCell("Linked drugs"),
                            tableUtil.createHeaderCell("Source")},
                    ReportResources.CONTENT_WIDTH_WIDE);

            for (com.hartwig.oncoact.patientreporter.model.Pharmacogenetics pharmacogenetics : pharmacogeneticsData) {
                contentTable.addCell(tableUtil.createContentCell(pharmacogenetics.gene()));
                contentTable.addCell(tableUtil.createTransparentCell(pharmacogenetics.genotype()));
                contentTable.addCell(tableUtil.createTransparentCell(pharmacogenetics.function()));
                contentTable.addCell(tableUtil.createTransparentCell(pharmacogenetics.linkedDrugs()));
                contentTable.addCell(tableUtil.createTransparentCell(new Paragraph(pharmacogenetics.source().name()).addStyle(
                                reportResources.dataHighlightLinksStyle()))
                        .setAction(PdfAction.createURI(pharmacogenetics.source().url())));
            }

            contentTable.addCell(TableUtil.createLayoutCell(1, contentTable.getNumberOfColumns())
                    .add(new Paragraph("\n #Note that we do not separately call the *36 allele. Dutch clinical "
                            + "guidelines consider the *36 allele to be clinically equivalent to the *1 allele.").addStyle(reportResources.subTextStyle()
                            .setTextAlignment(TextAlignment.LEFT))));
            return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN);
        }
    }

    @NotNull
    private Div createContentDiv(@NotNull String[] contentParagraphs) {
        Div div = new Div();
        for (String s : contentParagraphs) {
            div.add(new Paragraph(s).addStyle(reportResources.smallBodyTextStyle()).setFixedLeading(ReportResources.BODY_TEXT_LEADING));
        }
        return div;
    }

    @NotNull
    private Paragraph createSectionTitle(@NotNull String sectionTitle) {
        return new Paragraph(sectionTitle).addStyle(reportResources.smallBodyHeadingStyle());
    }

    @NotNull
    private Paragraph createParaGraphWithLinkThree(@NotNull String string1, @NotNull String string2, @NotNull String string3,
                                                   @NotNull String link) {
        return new Paragraph(string1).addStyle(reportResources.subTextStyle())
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string2).addStyle(reportResources.urlStyle()).setAction(PdfAction.createURI(link)))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING)
                .add(new Text(string3).addStyle(reportResources.subTextStyle()))
                .setFixedLeading(ReportResources.BODY_TEXT_LEADING);
    }

    @NotNull
    private Div createContentDivWithLinkThree(@NotNull String string1, @NotNull String string2, @NotNull String string3,
                                              @NotNull String link) {
        Div div = new Div();

        div.add(createParaGraphWithLinkThree(string1, string2, string3, link));
        return div;
    }
}