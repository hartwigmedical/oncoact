package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.patientreporter.cfreport.MathUtil;
import com.hartwig.oncoact.patientreporter.cfreport.data.TumorPurity;
import com.hartwig.oncoact.patientreporter.model.Purity;
import com.hartwig.oncoact.util.Formats;

class PurityCreator {

    static Purity createPurity(GenomicAnalysis curatedAnalysis) {
        double value = curatedAnalysis.impliedPurity();
        boolean isReliable = curatedAnalysis.hasReliablePurity();

        return Purity.builder()
                .value(isReliable ? value : null)
                .isReliable(isReliable)
                .label(getPurityLabel(value, isReliable))
                .build();
    }

    private static String getPurityLabel(double value, boolean isReliable) {
        if (!isReliable) {
            return Formats.NA_STRING;
        }
        double impliedPurityPercentage = MathUtil.mapPercentage(value, TumorPurity.RANGE_MIN, TumorPurity.RANGE_MAX);
        return Formats.formatPercentage(impliedPurityPercentage);
    }
}