package com.hartwig.oncoact.orange.purple;

import com.hartwig.oncoact.genome.GenomeRegion;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class PurpleCopyNumber implements GenomeRegion {

    public abstract double averageTumorCopyNumber();

}
