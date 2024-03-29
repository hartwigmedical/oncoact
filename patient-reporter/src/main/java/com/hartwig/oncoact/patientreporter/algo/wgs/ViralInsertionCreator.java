package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.hartwig.hmftools.datamodel.virus.AnnotatedVirus;
import com.hartwig.hmftools.datamodel.virus.VirusInterpretation;
import com.hartwig.hmftools.datamodel.virus.VirusLikelihoodType;
import com.hartwig.oncoact.patientreporter.model.ObservedViralInsertion;
import com.hartwig.oncoact.patientreporter.model.VirusDriverInterpretation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class ViralInsertionCreator {

    static List<ObservedViralInsertion> createViralInsertion(
            @NotNull List<AnnotatedVirus> reportableViruses
    ) {
        List<ObservedViralInsertion> observedViralInsertions = Lists.newArrayList();
        for (AnnotatedVirus virus : reportableViruses) {
            observedViralInsertions.add(ObservedViralInsertion.builder()
                    .virus(interpretVirusName(virus.name(), virus.interpretation(), virus.virusDriverLikelihoodType()))
                    .detectedIntegrationSites(integrations(virus))
                    .viralCoveragePercentage(Math.round(virus.percentageCovered()))
                    .virusDriverInterpretation(driverLikelihood(virus))
                    .build());
        }
        return observedViralInsertions;
    }

    @NotNull
    public static String interpretVirusName(@NotNull String virusName, @Nullable VirusInterpretation interpretation, @NotNull VirusLikelihoodType likelihoodType) {
        if (interpretation == VirusInterpretation.HPV) {
            if (likelihoodType == VirusLikelihoodType.HIGH) {
                return virusName + " (high risk)";
            } else {
                return virusName + " (low risk)";
            }
        } else {
            return virusName;
        }
    }

    @NotNull
    public static String integrations(@NotNull AnnotatedVirus virus) {
        return virus.integrations() == 0 ? "Detected without integration sites" : Integer.toString(virus.integrations());
    }

    @NotNull
    public static VirusDriverInterpretation driverLikelihood(@NotNull AnnotatedVirus virus) {
        switch (virus.virusDriverLikelihoodType()) {
            case HIGH: {
                return VirusDriverInterpretation.HIGH;
            }
            case LOW: {
                return VirusDriverInterpretation.LOW;
            }
            case UNKNOWN: {
                return VirusDriverInterpretation.UNKNOWN;
            }
            default:
                throw new IllegalStateException("Unknown virus driver likelihood: " + virus.virusDriverLikelihoodType());
        }
    }
}