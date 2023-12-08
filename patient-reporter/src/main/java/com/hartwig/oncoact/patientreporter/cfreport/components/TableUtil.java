package com.hartwig.oncoact.patientreporter.cfreport.components;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.util.Formats;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TableUtil {

    public static final float TABLE_BOTTOM_MARGIN = 20;
    public static final float TABLE_BOTTOM_MARGIN_SUMMARY = 0;
    @NotNull
    private final ReportResources reportResources;

    public TableUtil(@NotNull ReportResources reportResources) {
        this.reportResources = reportResources;
    }

    @NotNull
    public Cell createTransparentCell(@NotNull String text) {
        return createTransparentCell(new Paragraph(text));
    }

    @NotNull
    public Cell createTransparentCell(@NotNull IBlockElement element) {
        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        cell.setBorderBottom(Border.NO_BORDER);
        cell.addStyle(reportResources.tableContentStyle());
        cell.setKeepTogether(true);
        cell.add(element);
        return cell;
    }

    @NotNull
    public static Table createReportContentTable(@NotNull float[] columnPercentageWidths, @NotNull Cell[] headerCells, float contentWidth) {
        Table table = new Table(UnitValue.createPercentArray(columnPercentageWidths)).setWidth(contentWidth);
        table.setFixedLayout();

        for (Cell headerCell : headerCells) {
            table.addHeaderCell(headerCell);
        }

        return table;
    }

    @NotNull
    public Table createNoConsentReportTable(@NotNull String tableTitle, @NotNull String peachUnreliable, float tableBottomMargin,
            float contentWide) {
        Table table = TableUtil.createReportContentTable(new float[] { 1 }, new Cell[] {}, contentWide);
        table.setKeepTogether(true);
        table.setMarginBottom(tableBottomMargin);
        table.addCell(createContentCell(new Paragraph(peachUnreliable)));
        return createWrappingReportTable(tableTitle, null, table, tableBottomMargin);
    }

    @NotNull
    public Table createNoneReportTable(@NotNull String tableTitle, @Nullable String subTableTitle, float tableBottomMargin,
            float contentWide) {
        Cell headerCell;
        if (subTableTitle == null) {
            headerCell = new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph(tableTitle).addStyle(reportResources.sectionTitleStyle()
                            .setFontColor(ReportResources.PALETTE_LIGHT_GREY)));
        } else {
            headerCell = new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph(tableTitle).addStyle(reportResources.sectionTitleStyle()
                            .setFontColor(ReportResources.PALETTE_LIGHT_GREY)))
                    .add(new Paragraph(subTableTitle).addStyle(reportResources.sectionSubTitleStyle()
                            .setFontColor(ReportResources.PALETTE_LIGHT_GREY)));
        }

        Table table = createReportContentTable(new float[] { 1 }, new Cell[] { headerCell }, contentWide);
        table.setKeepTogether(true);
        table.setMarginBottom(tableBottomMargin);
        table.addCell(createDisabledContentCell(new Paragraph(Formats.NONE_STRING)));

        return table;
    }

    @NotNull
    public Table createWrappingReportTable(@NotNull String tableTitle, @Nullable String subtitle, @NotNull Table contentTable,
            float tableBottomMargin) {
        contentTable.addFooterCell(new Cell(1, contentTable.getNumberOfColumns()).setBorder(Border.NO_BORDER)
                        .setPaddingTop(15)
                        .setPaddingBottom(5)
                        .add(new Paragraph("The table continues on the next page".toUpperCase()).addStyle(reportResources.subTextStyle())))
                .setSkipLastFooter(true);

        Table continuedWrapTable = new Table(1).setMinWidth(contentTable.getWidth())
                .addHeaderCell(new Cell().setBorder(Border.NO_BORDER)
                        .add(new Paragraph("Continued from the previous page".toUpperCase()).addStyle(reportResources.subTextStyle())))
                .setSkipFirstHeader(true)
                .addCell(new Cell().add(contentTable).setPadding(0).setBorder(Border.NO_BORDER));

        if (subtitle == null) {
            return new Table(1).setMinWidth(contentTable.getWidth())
                    .setMarginBottom(tableBottomMargin)
                    .addHeaderCell(new Cell().setBorder(Border.NO_BORDER)
                            .setPadding(0)
                            .add(new Paragraph(tableTitle).addStyle(reportResources.sectionTitleStyle())))
                    .addCell(new Cell().add(continuedWrapTable).setPadding(0).setBorder(Border.NO_BORDER));
        } else {
            return new Table(1).setMinWidth(contentTable.getWidth())
                    .setMarginBottom(tableBottomMargin)
                    .addHeaderCell(new Cell().setBorder(Border.NO_BORDER)
                            .setPadding(0)
                            .add(new Paragraph(tableTitle).addStyle(reportResources.sectionTitleStyle()))
                            .add(new Paragraph(subtitle).addStyle(reportResources.sectionSubTitleStyle())))
                    .addCell(new Cell().add(continuedWrapTable).setPadding(0).setBorder(Border.NO_BORDER));
        }
    }

    @NotNull
    public Cell createHeaderCell(@NotNull String text) {
        return createHeaderCell(text, 1);
    }

    @NotNull
    public Cell createHeaderCell(@NotNull String text, int colSpan) {
        return createHeaderCell(colSpan).add(new Paragraph(text.toUpperCase()));
    }

    @NotNull
    private Cell createHeaderCell(int colSpan) {
        Cell c = new Cell(1, colSpan);
        c.setHeight(23); // Set fixed height to create consistent spacing between table title and header
        c.setBorder(Border.NO_BORDER);
        c.setVerticalAlignment(VerticalAlignment.BOTTOM);
        c.addStyle(reportResources.tableHeaderStyle());
        return c;
    }

    @NotNull
    public Cell createContentCell(@NotNull String text) {
        return createContentCell(new Paragraph(text));
    }

    @NotNull
    public Cell createContentCell(@NotNull IBlockElement element) {
        Cell c = new Cell();
        c.setBorder(Border.NO_BORDER);
        c.setBorderBottom(new SolidBorder(ReportResources.PALETTE_MID_GREY, 0.25f));
        c.addStyle(reportResources.tableContentStyle());
        c.setKeepTogether(true);
        c.add(element);
        return c;
    }

    @NotNull
    public Cell createContentCellPurityPloidy(@NotNull String text) {
        return createContentCellPurityPloidy(new Paragraph(text));
    }

    @NotNull
    public Cell createContentCellPurityPloidy(@NotNull IBlockElement element) {
        Cell c = new Cell();
        c.setBorder(Border.NO_BORDER);
        c.setBorderBottom(new SolidBorder(ReportResources.PALETTE_MID_GREY, 0.25f));
        c.addStyle(reportResources.dataHighlightStyle());
        c.setKeepTogether(true);
        c.add(element);
        return c;
    }

    @NotNull
    private Cell createDisabledContentCell(@NotNull IBlockElement element) {
        Cell c = new Cell();
        c.setBorder(Border.NO_BORDER);
        c.setBorderBottom(new SolidBorder(ReportResources.PALETTE_LIGHT_GREY, 0.25f));
        c.addStyle(reportResources.tableContentStyle().setFontColor(ReportResources.PALETTE_LIGHT_GREY));
        c.setKeepTogether(true);
        c.add(element);
        return c;
    }

    @NotNull
    public static Cell createLayoutCell() {
        return createLayoutCell(1, 1);
    }

    @NotNull
    public static Cell createLayoutCellSummary() {
        return createLayoutCell(2, 2);
    }

    @NotNull
    public static Cell createLayoutCell(int rowSpan, int colSpan) {
        Cell c = new Cell(rowSpan, colSpan);
        c.setBorder(Border.NO_BORDER);
        c.setKeepTogether(true);
        c.setPadding(0);
        c.setMargin(0);
        return c;
    }

    @NotNull
    public Cell createContentCellRowSpan(@NotNull String text, int rowSpan) {
        return createContentCellRowSpan(new Paragraph(text), rowSpan);
    }

    @NotNull
    public Cell createContentCellRowSpan(@NotNull IBlockElement element, int rowSpan) {
        return new Cell(rowSpan, 1).setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(ReportResources.PALETTE_MID_GREY, 0.25f))
                .addStyle(reportResources.tableContentStyle())
                .setKeepTogether(true)
                .add(element);
    }
}
