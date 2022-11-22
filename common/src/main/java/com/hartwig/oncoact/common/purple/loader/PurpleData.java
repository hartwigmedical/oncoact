package com.hartwig.oncoact.common.purple.loader;

import java.util.List;

import com.hartwig.oncoact.common.purple.PurpleQC;
import com.hartwig.oncoact.common.purple.TumorMutationalStatus;
import com.hartwig.oncoact.common.purple.GeneCopyNumber;
import com.hartwig.oncoact.common.purple.GermlineDeletion;
import com.hartwig.oncoact.common.purple.FittedPurityMethod;
import com.hartwig.oncoact.common.variant.ReportableVariant;
import com.hartwig.oncoact.common.variant.SomaticVariant;
import com.hartwig.oncoact.common.variant.msi.MicrosatelliteStatus;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface PurpleData
{

    @NotNull
    PurpleQC qc();

    boolean hasReliableQuality();

    @NotNull
    FittedPurityMethod fittedPurityMethod();

    boolean hasReliablePurity();

    double purity();

    double minPurity();

    double maxPurity();

    double ploidy();

    double minPloidy();

    double maxPloidy();

    boolean wholeGenomeDuplication();

    double microsatelliteIndelsPerMb();

    @NotNull
    MicrosatelliteStatus microsatelliteStatus();

    double tumorMutationalBurdenPerMb();

    int tumorMutationalLoad();

    @NotNull
    TumorMutationalStatus tumorMutationalLoadStatus();

    int svTumorMutationalBurden();

    @NotNull
    List<SomaticVariant> allSomaticVariants();

    @NotNull
    List<ReportableVariant> reportableSomaticVariants();

    @NotNull
    List<SomaticVariant> allGermlineVariants();

    @NotNull
    List<ReportableVariant> reportableGermlineVariants();

    @NotNull
    List<GeneCopyNumber> allSomaticGeneCopyNumbers();

    @NotNull
    List<GainLoss> allSomaticGainsLosses();

    @NotNull
    List<GainLoss> reportableSomaticGainsLosses();

    @NotNull
    List<GermlineDeletion> allGermlineDeletions();

    @NotNull
    List<GermlineDeletion> reportableGermlineDeletions();

    @NotNull
    List<CnPerChromosomeArmData> copyNumberPerChromosome();
}
