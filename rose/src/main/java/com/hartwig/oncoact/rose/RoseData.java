package com.hartwig.oncoact.rose;

import java.util.List;

import com.hartwig.oncoact.common.chord.ChordData;
import com.hartwig.oncoact.common.cuppa.interpretation.CuppaPrediction;
import com.hartwig.oncoact.common.drivercatalog.panel.DriverGene;
import com.hartwig.oncoact.common.linx.LinxData;
import com.hartwig.oncoact.common.purple.loader.PurpleData;
import com.hartwig.oncoact.common.virus.VirusInterpreterData;
import com.hartwig.oncoact.rose.actionability.ActionabilityEntry;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class RoseData {

    @NotNull
    public abstract String sampleId();

    @NotNull
    public abstract String patientId();

    @NotNull
    public abstract PurpleData purple();

    @NotNull
    public abstract LinxData linx();

    @NotNull
    public abstract VirusInterpreterData virusInterpreter();

    @NotNull
    public abstract ChordData chord();

    @NotNull
    public abstract CuppaPrediction cuppaPrediction();

    @NotNull
    public abstract List<ActionabilityEntry> actionabilityEntries();

    @NotNull
    public abstract List<DriverGene> driverGenes();
}