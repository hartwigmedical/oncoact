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

    private TableUtil() {
    }

    @NotNull
    public static Cell createTransparentCell(@NotNull String text) {
        return createTransparentCell(new Paragraph(text));
    }

    @NotNull
    public static Cell createTransparentCell(@NotNull IBlockElement element) {
        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        cell.setBorderBottom(Border.NO_BORDER);
        cell.addStyle(ReportResources.tableContentStyle());
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
    public static Table createNoConsentReportTable(@NotNull String tableTitle, @NotNull String peachUnreliable, float tableBottomMargin,
            float contentWide) {
        Table table = TableUtil.createReportContentTable(new float[] { 1 }, new Cell[] {}, contentWide);
        table.setKeepTogether(true);
        table.setMarginBottom(tableBottomMargin);
        table.addCell(TableUtil.createContentCell(new Paragraph(peachUnreliable)));
        return createWrappingReportTable(tableTitle, null, table, tableBottomMargin);
    }

    @NotNull
    public static Table createNoneReportTable(@NotNull String tableTitle, @Nullable String subTableTitle, float tableBottomMargin,
            float contentWide) {
        Cell headerCell;
        if (subTableTitle == null) {
            headerCell = new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph(tableTitle).addStyle(ReportResources.sectionTitleStyle()
                            .setFontColor(ReportResources.PALETTE_LIGHT_GREY)));
        } else {
            headerCell = new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph(tableTitle).addStyle(ReportResources.sectionTitleStyle()
                            .setFontColor(ReportResources.PALETTE_LIGHT_GREY)))
                    .add(new Paragraph(subTableTitle).addStyle(ReportResources.sectionSubTitleStyle()
                            .setFontColor(ReportResources.PALETTE_LIGHT_GREY)));
        }

        Table table = TableUtil.createReportContentTable(new float[] { 1 }, new Cell[] { headerCell }, contentWide);
        table.setKeepTogether(true);
        table.setMarginBottom(tableBottomMargin);
        table.addCell(TableUtil.createDisabledContentCell(new Paragraph(Formats.NONE_STRING)));

        return table;
    }

    @NotNull
    public static Table createWrappingReportTable(@NotNull String tableTitle, @Nullable String subtitle, @NotNull Table contentTable,
            float tableBottomMargin) {
        contentTable.addFooterCell(new Cell(1, contentTable.getNumberOfColumns()).setBorder(Border.NO_BORDER)
                        .setPaddingTop(5)
                        .setPaddingBottom(5)
                        .add(new Paragraph("The table continues on the next page".toUpperCase()).addStyle(ReportResources.subTextStyle())))
                .setSkipLastFooter(true);

        Table continuedWrapTable = new Table(1).setMinWidth(contentTable.getWidth())
                .addHeaderCell(new Cell().setBorder(Border.NO_BORDER)
                        .add(new Paragraph("Continued from the previous page".toUpperCase()).addStyle(ReportResources.subTextStyle())))
                .setSkipFirstHeader(true)
                .addCell(new Cell().add(contentTable).setPadding(0).setBorder(Border.NO_BORDER));

        if (subtitle == null) {
            return new Table(1).setMinWidth(contentTable.getWidth())
                    .setMarginBottom(tableBottomMargin)
                    .addHeaderCell(new Cell().setBorder(Border.NO_BORDER)
                            .setPadding(0)
                            .add(new Paragraph(tableTitle).addStyle(ReportResources.sectionTitleStyle())))
                    .addCell(new Cell().add(continuedWrapTable).setPadding(0).setBorder(Border.NO_BORDER));
        } else {
            return new Table(1).setMinWidth(contentTable.getWidth())
                    .setMarginBottom(tableBottomMargin)
                    .addHeaderCell(new Cell().setBorder(Border.NO_BORDER)
                            .setPadding(0)
                            .add(new Paragraph(tableTitle).addStyle(ReportResources.sectionTitleStyle()))
                            .add(new Paragraph(subtitle).addStyle(ReportResources.sectionSubTitleStyle())))
                    .addCell(new Cell().add(continuedWrapTable).setPadding(0).setBorder(Border.NO_BORDER));
        }
    }

    @NotNull
    public static Cell createHeaderCell(@NotNull String text) {
        return createHeaderCell(text, 1);
    }

    @NotNull
    public static Cell createHeaderCell(@NotNull String text, int colSpan) {
        return createHeaderCell(colSpan).add(new Paragraph(text.toUpperCase()));
    }

    @NotNull
    private static Cell createHeaderCell(int colSpan) {
        Cell c = new Cell(1, colSpan);
        c.setHeight(23); // Set fixed height to create consistent spacing between table title and header
        c.setBorder(Border.NO_BORDER);
        c.setVerticalAlignment(VerticalAlignment.BOTTOM);
        c.addStyle(ReportResources.tableHeaderStyle());
        return c;
    }

    @NotNull
    public static Cell createContentCell(@NotNull String text) {
        return createContentCell(new Paragraph(text));
    }

    @NotNull
    public static Cell createContentCell(@NotNull IBlockElement element) {
        Cell c = new Cell();
        c.setBorder(Border.NO_BORDER);
        c.setBorderBottom(new SolidBorder(ReportResources.PALETTE_MID_GREY, 0.25f));
        c.addStyle(ReportResources.tableContentStyle());
        c.setKeepTogether(true);
        c.add(element);
        return c;
    }

    @NotNull
    public static Cell createContentCellPurityPloidy(@NotNull String text) {
        return createContentCellPurityPloidy(new Paragraph(text));
    }

    @NotNull
    public static Cell createContentCellPurityPloidy(@NotNull IBlockElement element) {
        Cell c = new Cell();
        c.setBorder(Border.NO_BORDER);
        c.setBorderBottom(new SolidBorder(ReportResources.PALETTE_MID_GREY, 0.25f));
        c.addStyle(ReportResources.dataHighlightStyle());
        c.setKeepTogether(true);
        c.add(element);
        return c;
    }

    @NotNull
    private static Cell createDisabledContentCell(@NotNull IBlockElement element) {
        Cell c = new Cell();
        c.setBorder(Border.NO_BORDER);
        c.setBorderBottom(new SolidBorder(ReportResources.PALETTE_LIGHT_GREY, 0.25f));
        c.addStyle(ReportResources.tableContentStyle().setFontColor(ReportResources.PALETTE_LIGHT_GREY));
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
}
