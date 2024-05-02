package com.hartwig.oncoact.patientreporter.algo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.linx.LinxHomozygousDisruption;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleTumorMutationalStatus;
import com.hartwig.hmftools.datamodel.virus.VirusInterpreterEntry;
import com.hartwig.oncoact.copynumber.CnPerChromosomeArmData;
import com.hartwig.oncoact.disruption.GeneDisruption;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.variant.ReportableVariant;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class GenomicAnalysis {

    public abstract Set<PurpleQCStatus> purpleQCStatus();

    public abstract double impliedPurity();

    public abstract boolean hasReliablePurity();

    public abstract boolean hasReliableQuality();

    public abstract double averageTumorPloidy();

    @NotNull
    public abstract List<ProtectEvidence> tumorSpecificEvidence();

    @NotNull
    public abstract List<ProtectEvidence> clinicalTrials();

    @NotNull
    public abstract List<ProtectEvidence> offLabelEvidence();

    @NotNull
    public abstract List<ReportableVariant> reportableVariants();

    @NotNull
    public abstract Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant();

    public abstract double microsatelliteIndelsPerMb();

    @NotNull
    public abstract PurpleMicrosatelliteStatus microsatelliteStatus();

    public abstract int tumorMutationalLoad();

    public abstract double tumorMutationalBurden();

    @NotNull
    public abstract PurpleTumorMutationalStatus tumorMutationalBurdenStatus();

    public abstract double hrdValue();

    @NotNull
    public abstract ChordStatus hrdStatus();

    @NotNull
    public abstract List<PurpleGainLoss> gainsAndLosses();

    @NotNull
    public abstract List<CnPerChromosomeArmData> cnPerChromosome();

    @NotNull
    public abstract List<LinxFusion> geneFusions();

    @NotNull
    public abstract List<GeneDisruption> geneDisruptions();

    @NotNull
    public abstract List<LinxHomozygousDisruption> homozygousDisruptions();

    @NotNull
    public abstract List<VirusInterpreterEntry> reportableViruses();

    @NotNull
    public abstract List<InterpretPurpleGeneCopyNumbers> suspectGeneCopyNumbersWithLOH();
}
