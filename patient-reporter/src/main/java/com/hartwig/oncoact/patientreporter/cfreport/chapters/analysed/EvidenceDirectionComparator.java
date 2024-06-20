package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import java.util.EnumMap;

import com.hartwig.serve.datamodel.EvidenceDirection;

public class EvidenceDirectionComparator {
    private static final EnumMap<EvidenceDirection, Integer> evidenceDirectionOrder = new EnumMap<>(EvidenceDirection.class);

    private EvidenceDirectionComparator() {
    }

    public static EnumMap<EvidenceDirection, Integer> generateDirectionPriorityMap() {
        evidenceDirectionOrder.put(EvidenceDirection.RESPONSIVE, 0);
        evidenceDirectionOrder.put(EvidenceDirection.PREDICTED_RESPONSIVE, 1);
        evidenceDirectionOrder.put(EvidenceDirection.RESISTANT, 2);
        evidenceDirectionOrder.put(EvidenceDirection.PREDICTED_RESISTANT, 3);
        evidenceDirectionOrder.put(EvidenceDirection.NO_BENEFIT, 4);
        return evidenceDirectionOrder;
    }
}
