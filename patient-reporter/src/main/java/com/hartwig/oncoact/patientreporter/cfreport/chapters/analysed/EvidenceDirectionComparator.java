package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import java.util.Comparator;
import java.util.EnumMap;

import com.hartwig.serve.datamodel.EvidenceDirection;

public class EvidenceDirectionComparator implements Comparator<EvidenceDirection> {
    private static final EnumMap<EvidenceDirection, Integer> evidenceDirectionOrder = new EnumMap<>(EvidenceDirection.class);
    public static final EvidenceDirectionComparator INSTANCE = new EvidenceDirectionComparator();

    static {
        evidenceDirectionOrder.put(EvidenceDirection.RESPONSIVE, 0);
        evidenceDirectionOrder.put(EvidenceDirection.PREDICTED_RESPONSIVE, 1);
        evidenceDirectionOrder.put(EvidenceDirection.RESISTANT, 2);
        evidenceDirectionOrder.put(EvidenceDirection.PREDICTED_RESISTANT, 3);
        evidenceDirectionOrder.put(EvidenceDirection.NO_BENEFIT, 4);
    }

    private EvidenceDirectionComparator() {
    }

    @Override
    public int compare(EvidenceDirection evidenceDirection1, EvidenceDirection evidenceDirection2) {
        Integer evidenceDirectionOrder1 = evidenceDirectionOrder.get(evidenceDirection1);
        Integer evidenceDirectionOrder2 = evidenceDirectionOrder.get(evidenceDirection2);
        return evidenceDirectionOrder1.compareTo(evidenceDirectionOrder2);
    }
}
