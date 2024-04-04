package com.hartwig.oncoact.patientreporter.cfreport.components;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.hartwig.oncoact.patientreporter.model.PrimaryTumor;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import org.jetbrains.annotations.NotNull;

public final class TumorLocationAndTypeTable {

    private final ReportResources reportResources;

    public TumorLocationAndTypeTable(ReportResources reportResources) {
        this.reportResources = reportResources;
    }

    @NotNull
    public Table createTumorLocation(@NotNull PrimaryTumor primaryTumor, float width) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2}));
        table.setWidth(width);

        table.addCell(TableUtil.createLayoutCell().add(new Paragraph("PRIMARY TUMOR LOCATION").addStyle(reportResources.subTextStyle())));
        table.addCell(TableUtil.createLayoutCell().add(new Paragraph("PRIMARY TUMOR TYPE").addStyle(reportResources.subTextStyle())));

        table.addCell(TableUtil.createLayoutCell().add(DataLabel.createDataLabel(reportResources, primaryTumor.location())));
        table.addCell(TableUtil.createLayoutCell().add(DataLabel.createDataLabel(reportResources, primaryTumor.type())));

        return table;
    }

    @NotNull
    public Paragraph disclaimerTextTumorLocationBiopsyLocation() {
        return new Paragraph("The information regarding the primary tumor location and type, and the information related \n "
                + "to the biopsy, is based on information received from the originating hospital.").setMarginTop(10);
    }
}
