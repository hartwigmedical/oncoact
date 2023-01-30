package com.hartwig.oncoact.patientreporter.cfreport.components;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;

import org.jetbrains.annotations.NotNull;

public final class TumorLocationAndTypeTable {

    private TumorLocationAndTypeTable() {
    }

    @NotNull
    public static Table createTumorLocation(@NotNull String primaryTumorLocation, @NotNull String primaryTumorType,
                                            float width) {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 2, 2 }));
        table.setWidth(width);

        table.addCell(TableUtil.createLayoutCell().add(new Paragraph("PRIMARY TUMOR LOCATION").addStyle(ReportResources.subTextStyle())));
        table.addCell(TableUtil.createLayoutCell().add(new Paragraph("PRIMARY TUMOR TYPE").addStyle(ReportResources.subTextStyle())));

        table.addCell(TableUtil.createLayoutCell().add(DataLabel.createDataLabel(primaryTumorLocation)));
        table.addCell(TableUtil.createLayoutCell().add(DataLabel.createDataLabel(primaryTumorType)));

        return table;
    }

}
