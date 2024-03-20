package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.hmftools.datamodel.cuppa.CuppaData;
import com.hartwig.hmftools.datamodel.cuppa.CuppaPrediction;
import com.hartwig.hmftools.datamodel.cuppa.ImmutableCuppaPrediction;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.cuppa.MolecularTissueOriginReporting;
import com.hartwig.oncoact.cuppa.MolecularTissueOriginReportingFactory;
import com.hartwig.oncoact.patientreporter.algo.GenomicAnalysis;
import com.hartwig.oncoact.util.Formats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class TissueOfOriginPredictionCreator {
    private static final Logger LOGGER = LogManager.getLogger(TissueOfOriginPredictionCreator.class);

    static String createTissueOfOriginPrediction(
            @NotNull GenomicAnalysis genomicAnalysis,
            @Nullable CuppaData cuppa
    ) {
        if (cuppa == null) {
            return Formats.NA_STRING;
        }

        if (genomicAnalysis.purpleQCStatus().contains(PurpleQCStatus.FAIL_CONTAMINATION) || !genomicAnalysis.hasReliablePurity()) {
            return Formats.NA_STRING;
        }

        CuppaPrediction best = bestCuppaPrediction(cuppa);
        MolecularTissueOriginReporting molecularTissueOriginReporting = MolecularTissueOriginReportingFactory.create(best);

        Double likelihood = molecularTissueOriginReporting.interpretLikelihood();
        String cancerType = molecularTissueOriginReporting.interpretCancerType();

        if (likelihood == null) {
            return cancerType;
        } else {
            return cancerType + " (" + Formats.formatPercentageDigit(likelihood) + ")";
        }
    }

    @NotNull
    private static CuppaPrediction bestCuppaPrediction(@NotNull CuppaData cuppa) {
        CuppaPrediction best = null;

        for (CuppaPrediction prediction : cuppa.predictions()) {
            if (best == null || prediction.likelihood() > best.likelihood()) {
                best = prediction;
            }
        }

        if (best == null) {
            LOGGER.warn("No best CUPPA prediction found");
            return ImmutableCuppaPrediction.builder().cancerType("Unknown").likelihood(0D).build();
        }

        return best;
    }
}
