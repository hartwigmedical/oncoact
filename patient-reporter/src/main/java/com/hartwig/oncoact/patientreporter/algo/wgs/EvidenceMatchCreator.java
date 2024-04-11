package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.api.client.util.Lists;
import com.hartwig.oncoact.patientreporter.model.EvidenceMatch;
import com.hartwig.oncoact.protect.EvidenceType;
import com.hartwig.oncoact.protect.KnowledgebaseSource;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Set;

class EvidenceMatchCreator {

    static List<EvidenceMatch> createEvidenceMatch(
            Set<KnowledgebaseSource> knowledgebases
    ) {
        List<EvidenceMatch> evidenceMatches = Lists.newArrayList();
        for (KnowledgebaseSource source : knowledgebases) {
            evidenceMatches.add(EvidenceMatch.builder()
                    .type(getEvidenceType(source.evidenceType()))
                    .rank(extractRange(source))
                    .url(source.sourceUrls())
                    .build());
        }
        return evidenceMatches;
    }

    private static String extractRange(KnowledgebaseSource source) {
        String evidenceRank = Strings.EMPTY;
        String evidenceSource = source.evidenceType().display();

        if (source.evidenceType().equals(EvidenceType.CODON_MUTATION) || source.evidenceType().equals(EvidenceType.EXON_MUTATION)) {
            evidenceRank = String.valueOf(source.rangeRank());
        }

        String evidenceMerged;
        if (!evidenceRank.isEmpty()) {
            evidenceMerged = evidenceSource + " " + evidenceRank;
        } else {
            evidenceMerged = evidenceSource;
        }
        return evidenceMerged;
    }

    private static com.hartwig.oncoact.patientreporter.model.EvidenceType getEvidenceType(EvidenceType type) {
        switch (type) {
            case VIRAL_PRESENCE:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.VIRAL_PRESENCE;
            case SIGNATURE:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.SIGNATURE;
            case ACTIVATION:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.ACTIVATION;
            case INACTIVATION:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.INACTIVATION;
            case AMPLIFICATION:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.AMPLIFICATION;
            case OVER_EXPRESSION:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.OVER_EXPRESSION;
            case DELETION:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.DELETION;
            case UNDER_EXPRESSION:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.UNDER_EXPRESSION;
            case PROMISCUOUS_FUSION:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.PROMISCUOUS_FUSION;
            case FUSION_PAIR:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.FUSION_PAIR;
            case HOTSPOT_MUTATION:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.HOTSPOT_MUTATION;
            case CODON_MUTATION:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.CODON_MUTATION;
            case EXON_MUTATION:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.EXON_MUTATION;
            case ANY_MUTATION:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.ANY_MUTATION;
            case WILD_TYPE:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.WILD_TYPE;
            case HLA:
                return com.hartwig.oncoact.patientreporter.model.EvidenceType.HLA;
            default:
                throw new IllegalStateException("Unknown evidence type known: " + type);
        }
    }
}