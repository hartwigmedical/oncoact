package com.hartwig.oncoact.patientreporter.actionability;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.ImmutableProtectEvidence;
import com.hartwig.oncoact.protect.KnowledgebaseSource;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.jetbrains.annotations.NotNull;

public final class ClinicalTrialFactory {
    private ClinicalTrialFactory() {
    }

    @NotNull
    public static List<ProtectEvidence> extractOnLabelTrials(@NotNull List<ProtectEvidence> evidenceItems) {
        List<ProtectEvidence> trials = Lists.newArrayList();
        for (ProtectEvidence evidence : evidenceItems) {
            Set<KnowledgebaseSource> protectSources = Sets.newHashSet();
            ClinicalTrial clinicalTrial = evidence.clinicalTrial();

            for (KnowledgebaseSource protectSource : evidence.sources()) {
                if (protectSource.name() == Knowledgebase.CKB_TRIAL && evidence.onLabel()) {
                    Set<String> countries = clinicalTrial.countriesOfStudy();
                    // We want to report only NL studies
                    if (countries.contains("netherlands")) {
                        protectSources.add(protectSource);
                    }
                }
            }

            if (protectSources.size() >= 1) {
                trials.add(ImmutableProtectEvidence.builder().from(evidence).sources(protectSources).build());
            }

        }
        return trials;
    }
}