package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.cfreport.chapters.ReportChapter;
import com.hartwig.oncoact.patientreporter.cfreport.components.InlineBarChart;
import com.hartwig.oncoact.patientreporter.cfreport.components.LineDivider;
import com.hartwig.oncoact.patientreporter.cfreport.components.TableUtil;
import com.hartwig.oncoact.patientreporter.cfreport.components.TumorLocationAndTypeTable;
import com.hartwig.oncoact.patientreporter.cfreport.data.TumorPurity;
import com.hartwig.oncoact.patientreporter.model.*;
import com.hartwig.oncoact.util.Formats;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.StringJoiner;

public class SummaryChapter implements ReportChapter {

    private static final float TABLE_SPACER_HEIGHT = 5;

    @NotNull
    private final WgsReport wgsReport;
    @NotNull
    private final ReportResources reportResources;
    private final TableUtil tableUtil;
    private final TumorLocationAndTypeTable tumorLocationAndTypeTable;

    public SummaryChapter(@NotNull final WgsReport wgsReport, @NotNull final ReportResources reportResources) {
        this.wgsReport = wgsReport;
        this.reportResources = reportResources;
        this.tableUtil = new TableUtil(reportResources);
        this.tumorLocationAndTypeTable = new TumorLocationAndTypeTable(reportResources);
    }

    @NotNull
    @Override
    public String pdfTitle() {
        return wgsReport.summary().titleReport();
    }

    @NotNull
    @Override
    public String name() {
        return "Summary";
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
        reportDocument.add(tumorLocationAndTypeTable.createTumorLocation(wgsReport.tumorSample().primaryTumor(),
                contentWidth()));
        reportDocument.add(tumorLocationAndTypeTable.disclaimerTextTumorLocationBiopsyLocation()
                .addStyle(reportResources.subTextStyle()));

        renderClinicalConclusionText(reportDocument);
        renderSpecialRemarkText(reportDocument);

        renderGermline(reportDocument);
        renderTumorCharacteristics(reportDocument);
        renderGenomicAlterations(reportDocument);
    }

    private void renderClinicalConclusionText(@NotNull Document reportDocument) {
        String clinicalConclusion = wgsReport.summary().mostRelevantFindings();

        if (!clinicalConclusion.isEmpty()) {
            Div div = createSectionStartDiv(contentWidth());
            div.add(new Paragraph("Summary of most relevant findings").addStyle(reportResources.sectionTitleStyle()));

            div.add(new Paragraph(clinicalConclusion).setWidth(contentWidth())
                    .addStyle(reportResources.bodyTextStyle())
                    .setFixedLeading(11));
            div.add(new Paragraph("\nFurther interpretation of these results within the patient’s clinical context is required "
                    + "by a clinician with support of a molecular tumor board.").addStyle(reportResources.subTextStyle()));

            reportDocument.add(div);
        }
    }

    private void renderSpecialRemarkText(@NotNull Document reportDocument) {
        String text = wgsReport.summary().specialRemark();

        if (text != null && !text.isEmpty()) {
            Div div = createSectionStartDiv(contentWidth());
            div.add(new Paragraph("Special Remark").addStyle(reportResources.sectionTitleStyle()));

            div.add(new Paragraph(text).setWidth(contentWidth()).addStyle(reportResources.bodyTextStyle()).setFixedLeading(11));

            reportDocument.add(div);
        }
    }

    private void renderTumorCharacteristics(@NotNull Document reportDocument) {
        TumorCharacteristics tumorCharacteristics = wgsReport.summary().tumorCharacteristics();
        boolean hasReliablePurity = tumorCharacteristics.purity().isReliable();

        Div div = createSectionStartDiv(contentWidth());

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, .33f, .66f}));
        table.setWidth(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_LEFT);
        table.addCell(TableUtil.createLayoutCell()
                .add(new Paragraph("Tumor characteristics").setVerticalAlignment(VerticalAlignment.TOP)
                        .addStyle(reportResources.sectionTitleStyle())));
        table.addCell(TableUtil.createLayoutCell(1, 3).setHeight(TABLE_SPACER_HEIGHT));

        renderTumorPurity(tumorCharacteristics, table);

        Style dataStyleMolecularTissuePrediction =
                hasReliablePurity ? reportResources.dataHighlightStyle() : reportResources.dataHighlightNaStyle();

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Molecular tissue of origin prediction").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(tumorCharacteristics.tissueOfOriginPrediction()).addStyle(dataStyleMolecularTissuePrediction)));

        Style dataStyle = hasReliablePurity ? reportResources.dataHighlightStyle() : reportResources.dataHighlightNaStyle();
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Tumor mutational burden status").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(tumorCharacteristics.tumorMutationalBurden()).addStyle(dataStyle)));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Microsatellite status").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(tumorCharacteristics.microsatellite()).addStyle(dataStyle)));

        String hrdString = tumorCharacteristics.homologousRecombinationDeficiency();
        Style hrdStyle;
        if (!hrdString.equals(Formats.NA_STRING)) {
            hrdStyle = reportResources.dataHighlightStyle();
        } else {
            hrdStyle = reportResources.dataHighlightNaStyle();
        }
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("HR Status").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(hrdString).addStyle(hrdStyle)));

        Style style;
        if (tumorCharacteristics.viruses().equals(Formats.NONE_STRING)) {
            style = reportResources.dataHighlightNaStyle();
        } else {
            style = reportResources.dataHighlightStyle();
        }
        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Virus").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(tumorCharacteristics.viruses())).addStyle(style));

        div.add(table);

        reportDocument.add(div);
    }

    private void renderTumorPurity(@NotNull TumorCharacteristics tumorCharacteristics, @NotNull Table table) {
        String label = "Tumor purity";
        table.addCell(createMiddleAlignedCell().add(new Paragraph(label).addStyle(reportResources.bodyTextStyle())));

        Double impliedPurity = tumorCharacteristics.purity().value();
        if (impliedPurity != null) {
            table.addCell(createMiddleAlignedCell().add(createHighlightParagraph(tumorCharacteristics.purity().label()).addStyle(reportResources.dataHighlightStyle())));
            table.addCell(createMiddleAlignedCell().add(createInlineBarChart(impliedPurity, TumorPurity.RANGE_MIN, TumorPurity.RANGE_MAX)));
        } else {
            table.addCell(createMiddleAlignedCell(2).add(createHighlightParagraph(tumorCharacteristics.purity().label()).addStyle(reportResources.dataHighlightNaStyle())));
        }
    }

    private void renderGenomicAlterations(@NotNull Document reportDocument) {
        GenomicAlterations genomicAlterations = wgsReport.summary().genomicAlterations();
        Div div = createSectionStartDiv(contentWidth());

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        table.setWidth(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_LEFT);
        table.addCell(TableUtil.createLayoutCellSummary()
                .add(new Paragraph("Genomic alterations in cancer genes").addStyle(reportResources.sectionTitleStyle())));
        table.addCell(TableUtil.createLayoutCell(1, 2).setHeight(TABLE_SPACER_HEIGHT));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Genes with driver mutation").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(genomicAlterations.genesWithDriverMutation()));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Amplified gene(s)").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(genomicAlterations.amplifiedGenes()));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Deleted gene(s)").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(genomicAlterations.deletedGenes()));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Homozygously disrupted genes").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(genomicAlterations.homozygouslyDisruptedGenes()));

        table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                .add(new Paragraph("Gene fusions").addStyle(reportResources.bodyTextStyle())));
        table.addCell(createGeneSetCell(genomicAlterations.geneFusions()));

        String potentialMsiGenes = genomicAlterations.potentialMsiGenes();
        if (potentialMsiGenes != null) {
            table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                    .add(new Paragraph("Potential MMR genes").addStyle(reportResources.bodyTextStyle())));
            table.addCell(createGeneSetCell(potentialMsiGenes));
        }

        String potentialHrdGenes = genomicAlterations.potentialHrdGenes();
        if (potentialHrdGenes != null) {
            table.addCell(createMiddleAlignedCell().setVerticalAlignment(VerticalAlignment.TOP)
                    .add(new Paragraph("Potential HRD genes").addStyle(reportResources.bodyTextStyle())));
            table.addCell(createGeneSetCell(potentialHrdGenes));
        }

        div.add(table);
        reportDocument.add(div);
    }

    private void renderGermline(@NotNull Document reportDocument) {
        int width = 180;
        int leftPosition = 400;
        int bottomPosition = 70;

        Div div = new Div();

        div.add(renderPharmacogenetics());
        div.add(new Paragraph(Strings.EMPTY)).setFontSize(2);
        div.add(renderHla());
        div.add(renderGermlineText());

        reportDocument.add(div.setFixedPosition(leftPosition, bottomPosition, width));

    }

    private Table renderPharmacogenetics() {
        String title = "Pharmacogenetics";

        List<PharmacogeneticsGenotype> pharmacogeneticsData = wgsReport.summary().pharmacogenetics();
        if (wgsReport.summary().pharmacogenetics().isEmpty()) {
            return tableUtil.createNoneReportTable(title,
                    null,
                    TableUtil.TABLE_BOTTOM_MARGIN_SUMMARY,
                    ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT);
        } else {
            Table contentTable = TableUtil.createReportContentTable(new float[]{1, 3},
                    new Cell[]{tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Function")},
                    ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT);

            for (PharmacogeneticsGenotype pharmacogenetics : pharmacogeneticsData) {
                contentTable.addCell(tableUtil.createContentCell(pharmacogenetics.gene()));
                contentTable.addCell(tableUtil.createContentCell(concat(pharmacogenetics.functions())));
            }
            return tableUtil.createWrappingReportTable(title, null, contentTable, TableUtil.TABLE_BOTTOM_MARGIN_SUMMARY);
        }
    }

    private Table renderHla() {
        String title = "HLA Alleles";
        if (!wgsReport.summary().hlaQc()) {
            String noConsent = "The QC of the HLA types do not meet the QC cut-offs";
            return tableUtil.createNoConsentReportTable(title,
                    noConsent,
                    TableUtil.TABLE_BOTTOM_MARGIN_SUMMARY,
                    ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT);
        } else if (wgsReport.summary().hlaAlleles().isEmpty()) {
            return tableUtil.createNoneReportTable(title,
                    null,
                    TableUtil.TABLE_BOTTOM_MARGIN_SUMMARY,
                    ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT);
        } else {
            Table table = TableUtil.createReportContentTable(new float[]{8, 10},
                    new Cell[]{tableUtil.createHeaderCell("Gene"), tableUtil.createHeaderCell("Germline allele")},
                    ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT);

            List<HlaAlleleSummary> hlaData = wgsReport.summary().hlaAlleles();
            for (HlaAlleleSummary hla : hlaData) {
                table.addCell(tableUtil.createContentCell(hla.gene()));
                table.addCell(tableUtil.createContentCell(concat(hla.germlineAlleles())));
            }

            return tableUtil.createWrappingReportTable(title, null, table, TableUtil.TABLE_BOTTOM_MARGIN_SUMMARY);
        }
    }

    private Div renderGermlineText() {
        String text = "Underlying data concerning cancer predisposition genes may be requested by a licensed clinical genetics laboratory"
                + " after the patient has given informed consent.";

        Div div = createSectionStartDivWithoutLineDivider(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT);
        div.add(new Paragraph("Germline results").addStyle(reportResources.sectionTitleStyle()));

        return div.add(new Paragraph(text).setWidth(ReportResources.CONTENT_WIDTH_WIDE_SUMMARY_RIGHT)
                .addStyle(reportResources.bodyTextStyle())
                .setFixedLeading(11));
    }

    @NotNull
    public static String concat(@Nullable Iterable<String> strings) {
        if (strings == null) {
            return Strings.EMPTY;
        }

        StringJoiner joiner = new StringJoiner(" | ");
        for (String entry : strings) {
            joiner.add(entry);
        }
        return joiner.toString();
    }

    @NotNull
    private static Div createSectionStartDiv(float width) {
        return new Div().setKeepTogether(true).setWidth(width).add(LineDivider.createLineDivider(width));
    }

    @NotNull
    private static Div createSectionStartDivWithoutLineDivider(float width) {
        return new Div().setKeepTogether(true).setWidth(width);
    }

    @NotNull
    private Cell createMiddleAlignedCell() {
        return createMiddleAlignedCell(1);
    }

    @NotNull
    private Cell createMiddleAlignedCell(int colSpan) {
        return TableUtil.createLayoutCell(1, colSpan).setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    @NotNull
    private Cell createGeneSetCell(@NotNull String genes) {
        Style style = genes.equals(Formats.NONE_STRING) ? reportResources.dataHighlightStyle() : reportResources.dataHighlightNaStyle();

        return createMiddleAlignedCell().add(createHighlightParagraph(genes)).addStyle(style);
    }

    @NotNull
    private static Paragraph createHighlightParagraph(@NotNull String text) {
        return new Paragraph(text).setFixedLeading(14);
    }

    @NotNull
    private static InlineBarChart createInlineBarChart(double value, double min, double max) {
        InlineBarChart chart = new InlineBarChart(value, min, max);
        chart.setWidth(41);
        chart.setHeight(6);
        return chart;
    }
}