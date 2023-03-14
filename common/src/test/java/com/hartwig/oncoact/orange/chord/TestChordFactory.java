package com.hartwig.oncoact.orange.chord;

import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.chord.ImmutableChordRecord;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestChordFactory {

    private TestChordFactory() {
    }

    @NotNull
    public static ImmutableChordRecord.Builder builder() {
        return ImmutableChordRecord.builder()
                .hrdValue(0D)
                .hrStatus(ChordStatus.UNKNOWN)
                .brca1Value(0)
                .brca2Value(0)
                .hrdType(Strings.EMPTY)
                ;
    }
}
