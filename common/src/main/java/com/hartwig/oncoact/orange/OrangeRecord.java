package com.hartwig.oncoact.orange;

import java.time.LocalDate;

import com.hartwig.oncoact.orange.chord.ChordRecord;
import com.hartwig.oncoact.orange.cuppa.CuppaRecord;
import com.hartwig.oncoact.orange.lilac.LilacRecord;
import com.hartwig.oncoact.orange.linx.LinxRecord;
import com.hartwig.oncoact.orange.peach.PeachRecord;
import com.hartwig.oncoact.orange.plots.OrangePlots;
import com.hartwig.oncoact.orange.purple.PurpleRecord;
import com.hartwig.oncoact.orange.virus.VirusInterpreterRecord;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class OrangeRecord {

    @NotNull
    public abstract String sampleId();

    @NotNull
    public abstract LocalDate experimentDate();

    @NotNull
    public abstract OrangeRefGenomeVersion refGenomeVersion();

    @NotNull
    public abstract PurpleRecord purple();

    @NotNull
    public abstract LinxRecord linx();

    @NotNull
    public abstract PeachRecord peach();

    @NotNull
    public abstract CuppaRecord cuppa();

    @NotNull
    public abstract VirusInterpreterRecord virusInterpreter();

    @NotNull
    public abstract LilacRecord lilac();

    @NotNull
    public abstract ChordRecord chord();

    @NotNull
    public abstract OrangePlots plots();

}
