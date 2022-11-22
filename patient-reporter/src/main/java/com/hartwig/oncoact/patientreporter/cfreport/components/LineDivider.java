package com.hartwig.oncoact.patientreporter.cfreport.components;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.element.LineSeparator;

import org.jetbrains.annotations.NotNull;

public final class LineDivider {

    private LineDivider() {
    }

    @NotNull
    public static LineSeparator createLineDivider(float width) {
        SolidLine line = new SolidLine(1f);
        line.setColor(ReportResources.PALETTE_BLUE);

        LineSeparator ls = new LineSeparator(line);
        ls.setMarginTop(10);
        ls.setWidth(width);
        ls.setMarginBottom(4);

        return ls;
    }
}
