package com.hartwig.oncoact.patientreporter.cfreport.components;

import com.hartwig.lama.client.model.TumorType;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class TumorLocationAndTypeTable {

    private TumorLocationAndTypeTable() {
    }

    @NotNull
    public static Table createTumorLocation(@Nullable TumorType tumorType, float width) {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 2, 2 }));
        table.setWidth(width);

        table.addCell(TableUtil.createLayoutCell().add(new Paragraph("PRIMARY TUMOR LOCATION").addStyle(ReportResources.subTextStyle())));
        table.addCell(TableUtil.createLayoutCell().add(new Paragraph("PRIMARY TUMOR TYPE").addStyle(ReportResources.subTextStyle())));

        String tumorLocation = Optional.ofNullable(tumorType).map(TumorType::getLocation).orElse("");
        table.addCell(TableUtil.createLayoutCell().add(DataLabel.createDataLabel(tumorLocation)));
        String type = Optional.ofNullable(tumorType).map(TumorType::getType).orElse("");
        table.addCell(TableUtil.createLayoutCell().add(DataLabel.createDataLabel(type)));

        return table;
    }
}
