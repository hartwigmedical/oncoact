package com.hartwig.oncoact.common.pathogenic;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface PathogenicSummary {

    @NotNull
    String clinvarInfo();

    @NotNull
    Pathogenicity pathogenicity();
}
