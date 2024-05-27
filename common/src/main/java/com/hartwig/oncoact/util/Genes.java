package com.hartwig.oncoact.util;

import com.google.common.collect.Sets;

import java.util.Set;

public final class Genes {

    public static final Set<String> HRD_GENES = Sets.newHashSet("BRCA1", "BRCA2", "PALB2", "RAD51C", "RAD51B");
    public static final Set<String> MSI_GENES = Sets.newHashSet("MSH6", "MSH2", "MLH1", "PMS2", "EPCAM");

    private Genes() {
    }
}
