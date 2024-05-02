package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.virus.VirusInterpretation;
import com.hartwig.hmftools.datamodel.virus.VirusInterpreterEntry;
import com.hartwig.hmftools.datamodel.virus.VirusLikelihoodType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ViralPresence {

    private ViralPresence() {
    }

    @NotNull
    public static Set<String> virusInterpretationSummary(@NotNull Iterable<VirusInterpreterEntry> reportableViruses) {
        Set<VirusInterpretation> positiveHighDriverInterpretations = Sets.newHashSet();
        Set<String> virusInterpretationSummary = Sets.newHashSet();

        for (VirusInterpreterEntry virus : reportableViruses) {
            if (virus.driverLikelihood() == VirusLikelihoodType.HIGH) {
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
    public static String percentageCovered(@NotNull VirusInterpreterEntry virus) {
        return Math.round(virus.percentageCovered()) + "%";
    }

    @NotNull
    public static String integrations(@NotNull VirusInterpreterEntry virus) {
        return virus.integrations() == 0 ? "Detected without integration sites" : Integer.toString(virus.integrations());
    }

    @NotNull
    public static String driverLikelihood(@NotNull VirusInterpreterEntry virus) {
        switch (virus.driverLikelihood()) {
            case HIGH: {
                return "High";
            }
            case LOW: {
                return "Low";
            }
            case UNKNOWN: {
                return "Unknown";
            }
            default: {
                return "Invalid";
            }
        }
    }
}