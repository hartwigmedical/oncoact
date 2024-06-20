package com.hartwig.oncoact.patientreporter.cfreport.chapters.analysed;

import java.util.HashMap;

public class EvidenceTumorTypeSpecificComparator {

    private static final HashMap<Boolean, Integer> evidenceTumorTypeOrder = new HashMap<>();

    private EvidenceTumorTypeSpecificComparator() {
    }

    public static HashMap<Boolean, Integer> generateTumorTypePriorityMap() {
        evidenceTumorTypeOrder.put(true, 0);
        evidenceTumorTypeOrder.put(false, 1);
        return evidenceTumorTypeOrder;
    }
}
