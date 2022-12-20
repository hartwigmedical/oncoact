package com.hartwig.oncoact.orange.datamodel.virus;

import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class VirusInterpreterRecord {

    @NotNull
    public abstract Set<VirusInterpreterEntry> allViruses();

    @NotNull
    public abstract Set<VirusInterpreterEntry> reportableViruses();
}
