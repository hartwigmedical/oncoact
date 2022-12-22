package com.hartwig.oncoact.protect;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class KnowledgebaseSourceComparator implements Comparator<KnowledgebaseSource> {

    @Override
    public int compare(@NotNull KnowledgebaseSource source1, @NotNull KnowledgebaseSource source2) {
        int knowledgebaseNameCompare = source1.name().toString().compareTo(source2.name().toString());
        if (knowledgebaseNameCompare != 0) {
            return knowledgebaseNameCompare;
        }

        int sourceEventCompare = source1.sourceEvent().compareTo(source2.sourceEvent());
        if (sourceEventCompare != 0) {
            return sourceEventCompare;
        }

        return source1.evidenceType().display().compareTo(source2.evidenceType().display());
    }
}
