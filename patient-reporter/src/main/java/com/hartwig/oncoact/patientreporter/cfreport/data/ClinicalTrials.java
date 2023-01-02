package com.hartwig.oncoact.patientreporter.cfreport.data;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.ProtectEvidence;

import org.jetbrains.annotations.NotNull;

public final class ClinicalTrials {

    private ClinicalTrials() {
    }

    public static int uniqueEventCount(@NotNull List<ProtectEvidence> trials) {
        Set<String> events = Sets.newHashSet();
        for (ProtectEvidence trial : trials) {
            String event = trial.gene() != null ? trial.gene() + " " + trial.event() : trial.event();
            events.add(event);
        }
        return events.size();
    }

    public static int uniqueTrialCount(@NotNull List<ProtectEvidence> trials) {
        Set<String> acronyms = Sets.newHashSet();
        for (ProtectEvidence trial : trials) {
            acronyms.add(trial.treatment().name());
        }
        return acronyms.size();
    }
}