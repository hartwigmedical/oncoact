package com.hartwig.oncoact.cuppa;

import com.hartwig.hmftools.datamodel.cuppa.CuppaPrediction;

import org.jetbrains.annotations.NotNull;

public class MolecularTissueOriginReportingFactory {

    private static final String RESULTS_INCONCLUSIVE = "Results inconclusive";

    private MolecularTissueOriginReportingFactory() {
    }

    @NotNull
    public static MolecularTissueOriginReporting create(@NotNull CuppaPrediction bestPrediction) {
        double likelihood = bestPrediction.likelihood();
        String cancerType = curateCancerType(bestPrediction.cancerType());
        String interpretCancerType = interpretTumorLocation(likelihood, cancerType);
        Double interpretLikelihood = likelihood >= 0.8 ? likelihood : null;

        return ImmutableMolecularTissueOriginReporting.builder()
                .bestCancerType(cancerType)
                .bestLikelihood(likelihood)
                .interpretCancerType(interpretCancerType)
                .interpretLikelihood(interpretLikelihood)
                .build();
    }

    @NotNull
    private static String curateCancerType(@NotNull String cancerType) {
        if (cancerType.equals("Uterus: Endometrium")) {
            cancerType = "Endometrium";
        } else if (cancerType.equals("Colorectum/Appendix/SmallIntestine")) {
            cancerType = "Lower GI tract";
        }
        return cancerType;
    }

    @NotNull
    private static String interpretTumorLocation(double likelihood, @NotNull String cancerType) {
        // our cut-off is 80% likelihood. When this is below 80% then the results is inconclusive
        String interpretCancerType;
        if (likelihood <= 0.8) {
            interpretCancerType = RESULTS_INCONCLUSIVE;
        } else {
            interpretCancerType = cancerType;
        }
        return interpretCancerType;
    }
}