package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.orange.virus.VirusDriverLikelihood;
import com.hartwig.oncoact.orange.virus.VirusInterpretation;
import com.hartwig.oncoact.orange.virus.VirusInterpreterEntry;

import org.jetbrains.annotations.NotNull;

public final class ViralPresence {

    private ViralPresence() {
    }

    @NotNull
    public static Set<String> virusInterpretationSummary(@NotNull Iterable<VirusInterpreterEntry> reportableViruses) {
        Set<VirusInterpretation> positiveHighDriverInterpretations = Sets.newHashSet();
        Set<String> virusInterpretationSummary = Sets.newHashSet();

        for (VirusInterpreterEntry virus : reportableViruses) {
            if (virus.driverLikelihood() == VirusDriverLikelihood.HIGH) {
                VirusInterpretation virusInterpretation = virus.interpretation();
                if (virus.interpretation() != null) {
                    assert virusInterpretation != null;
                    positiveHighDriverInterpretations.add(virus.interpretation());
                }
            }
        }

        for (VirusInterpretation positiveVirus : positiveHighDriverInterpretations) {
            virusInterpretationSummary.add(positiveVirus + " positive");
        }

        return virusInterpretationSummary;
    }

    @NotNull
    public static String createViralCoverageString(double percentageCovered) {
        return Math.round(percentageCovered) + "%";
    }

    @NotNull
    public static String createIntegrationSiteString(Integer integrations) {
        return integrations == 0 ? "Detected without integration sites" : Integer.toString(integrations);
    }
}