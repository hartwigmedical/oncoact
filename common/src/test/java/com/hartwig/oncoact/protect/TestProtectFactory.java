package com.hartwig.oncoact.protect;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestProtectFactory {

    private TestProtectFactory() {
    }

    @NotNull
    public static ImmutableProtectEvidence.Builder builder() {
        return ImmutableProtectEvidence.builder()
                .event(Strings.EMPTY)
                .germline(false)
                .reported(true)
                .matchGender(true)
                .onLabel(false)
                .level(EvidenceLevel.A)
                .direction(EvidenceDirection.RESPONSIVE)
                .sources(Sets.newHashSet(createSource(Knowledgebase.VICC_CGI)));
    }

    @NotNull
    public static ImmutableKnowledgebaseSource.Builder sourceBuilder() {
        return ImmutableKnowledgebaseSource.builder()
                .name(Knowledgebase.UNKNOWN)
                .sourceEvent(Strings.EMPTY)
                .evidenceType(EvidenceType.ANY_MUTATION)
                .rangeRank(null);
    }

    @NotNull
    public static KnowledgebaseSource createSource(@NotNull Knowledgebase knowledgebase) {
        return sourceBuilder().name(knowledgebase).build();
    }
}