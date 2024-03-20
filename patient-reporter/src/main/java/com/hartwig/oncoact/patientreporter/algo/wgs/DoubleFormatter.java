package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.oncoact.patientreporter.cfreport.ReportResources;

import java.text.DecimalFormat;

class DoubleFormatter {
    private static final DecimalFormat SINGLE_DECIMAL_FORMAT = ReportResources.decimalFormat("#.#");

    static String formatSingleDecimal(double value) {
        return SINGLE_DECIMAL_FORMAT.format(value);
    }
}
