package com.hartwig.oncoact.patientreporter.cfreport.data;

import com.hartwig.oncoact.common.purple.TumorMutationalStatus;

public final class MutationalLoad {

    public static final int RANGE_MIN = 1;
    public static final int RANGE_MAX = 1000;
    public static final int THRESHOLD = TumorMutationalStatus.TML_THRESHOLD;

    private MutationalLoad() {
    }
}
