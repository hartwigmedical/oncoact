package com.hartwig.oncoact.patientreporter.cfreport.components;

import java.util.Optional;

import com.hartwig.lama.client.model.TumorType;
import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TumorLocationAndTypeTable {

    private final ReportResources reportResources;

    public TumorLocationAndTypeTable(ReportResources reportResources) {
        this.reportResources = reportResources;
    }

    @NotNull
    public Table createTumorLocation(@Nullable TumorType tumorType, float width) {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 2, 2 }));
        table.setWidth(width);

        table.addCell(TableUtil.createLayoutCell().add(new Paragraph("PRIMARY TUMOR LOCATION").addStyle(reportResources.subTextStyle())));
        table.addCell(TableUtil.createLayoutCell().add(new Paragraph("PRIMARY TUMOR TYPE").addStyle(reportResources.subTextStyle())));

        String tumorLocation = Optional.ofNullable(tumorType).map(TumorType::getLocation).orElse("");
        table.addCell(TableUtil.createLayoutCell().add(DataLabel.createDataLabel(reportResources, tumorLocation)));

        String type = Optional.ofNullable(tumorType).map(TumorType::getType).orElse("");
        String extra = Optional.ofNullable(tumorType).map(TumorType::getExtra).orElse("");
        String combined = extra.isEmpty() ? type : type + " \n " + extra;
        table.addCell(TableUtil.createLayoutCell().add(DataLabel.createDataLabel(reportResources, combined)));

        return table;
    }

    @NotNull
    public Paragraph disclaimerTextTumorLocationBiopsyLocation() {
        return new Paragraph("The information regarding the primary tumor location and type, and the information related \n "
                + "to the biopsy, is based on information received from the originating hospital.").setMarginTop(10);
    }
}
