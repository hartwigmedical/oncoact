package com.hartwig.oncoact.rose;

import java.util.List;

import com.hartwig.oncoact.drivergene.DriverGene;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.oncoact.rose.actionability.ActionabilityEntry;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class RoseData {
    @NotNull
    public abstract OrangeRecord orange();

    @NotNull
    public abstract List<ActionabilityEntry> actionabilityEntries();

    @NotNull
    public abstract List<DriverGene> driverGenes();
}