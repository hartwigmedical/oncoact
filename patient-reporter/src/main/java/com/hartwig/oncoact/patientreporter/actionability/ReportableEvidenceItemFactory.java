package com.hartwig.oncoact.patientreporter.actionability;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.protect.ImmutableProtectEvidence;
import com.hartwig.oncoact.protect.KnowledgebaseSource;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.serve.datamodel.Knowledgebase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public final class ReportableEvidenceItemFactory {

    private ReportableEvidenceItemFactory() {
    }

    @NotNull
    public static List<ProtectEvidence> extractNonTrialsOnLabel(@NotNull List<ProtectEvidence> evidenceItems) {
        List<ProtectEvidence> nonTrials = Lists.newArrayList();
        for (ProtectEvidence evidence : evidenceItems) {
            Set<KnowledgebaseSource> protectSources = Sets.newHashSet();

            for (KnowledgebaseSource source : evidence.sources()) {
                if (source.name() != Knowledgebase.CKB_TRIAL && evidence.onLabel()) {
                    protectSources.add(source);
                }
            }

            if (protectSources.size() >= 1) {
                nonTrials.add(ImmutableProtectEvidence.builder().from(evidence).sources(protectSources).build());
            }
        }
        return nonTrials;
    }

    @NotNull
    public static List<ProtectEvidence> extractNonTrialsOffLabel(@NotNull List<ProtectEvidence> evidenceItems) {
        List<ProtectEvidence> nonTrials = Lists.newArrayList();
        for (ProtectEvidence evidence : evidenceItems) {
            Set<KnowledgebaseSource> protectSources = Sets.newHashSet();
            for (KnowledgebaseSource source : evidence.sources()) {
                if (source.name() != Knowledgebase.CKB_TRIAL && !evidence.onLabel()) {
                    protectSources.add(source);

                }
            }
            if (protectSources.size() >= 1) {
                nonTrials.add(ImmutableProtectEvidence.builder().from(evidence).sources(protectSources).build());
            }
        }
        return nonTrials;
    }
}