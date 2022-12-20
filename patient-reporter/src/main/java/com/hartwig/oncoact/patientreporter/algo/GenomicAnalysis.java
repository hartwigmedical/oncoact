package com.hartwig.oncoact.patientreporter.algo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hartwig.oncoact.common.chord.ChordStatus;
import com.hartwig.oncoact.common.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.common.linx.GeneDisruption;
import com.hartwig.oncoact.common.linx.HomozygousDisruption;
import com.hartwig.oncoact.common.linx.LinxFusion;
import com.hartwig.oncoact.common.purple.PurpleQCStatus;
import com.hartwig.oncoact.common.purple.TumorMutationalStatus;
import com.hartwig.oncoact.common.purple.loader.CnPerChromosomeArmData;
import com.hartwig.oncoact.common.purple.loader.GainLoss;
import com.hartwig.oncoact.common.variant.ReportableVariant;
import com.hartwig.oncoact.common.variant.msi.MicrosatelliteStatus;
import com.hartwig.oncoact.common.virus.AnnotatedVirus;
import com.hartwig.oncoact.protect.ProtectEvidence;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
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
    public abstract MicrosatelliteStatus microsatelliteStatus();

    public abstract int tumorMutationalLoad();

    @NotNull
    public abstract TumorMutationalStatus tumorMutationalLoadStatus();

    public abstract double tumorMutationalBurden();

    public abstract double hrdValue();

    @NotNull
    public abstract ChordStatus hrdStatus();

    @NotNull
    public abstract List<GainLoss> gainsAndLosses();

    @NotNull
    public abstract List<CnPerChromosomeArmData> cnPerChromosome();

    @NotNull
    public abstract List<LinxFusion> geneFusions();

    @NotNull
    public abstract List<GeneDisruption> geneDisruptions();

    @NotNull
    public abstract List<HomozygousDisruption> homozygousDisruptions();

    @NotNull
    public abstract List<AnnotatedVirus> reportableViruses();

    @NotNull
    public abstract HlaAllelesReportingData hlaAlleles();

    @NotNull
    public abstract List<LohGenesReporting> suspectGeneCopyNumbersHRDWithLOH();

    @NotNull
    public abstract List<LohGenesReporting> suspectGeneCopyNumbersMSIWithLOH();
}
