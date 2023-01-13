package com.hartwig.oncoact.orange.purple;

import java.util.List;
import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class PurpleRecord {

    @NotNull
    public abstract PurpleFit fit();

    @NotNull
    public abstract PurpleCharacteristics characteristics();

    @NotNull
    public abstract Set<PurpleDriver> somaticDrivers();

    @Nullable
    public abstract Set<PurpleDriver> germlineDrivers();

    @NotNull
    public abstract Set<PurpleVariant> allSomaticVariants();

    @NotNull
    public abstract Set<PurpleVariant> reportableSomaticVariants();

    @Nullable
    public abstract Set<PurpleVariant> allGermlineVariants();

    @Nullable
    public abstract Set<PurpleVariant> reportableGermlineVariants();

    @NotNull
    public abstract Set<PurpleCopyNumber> allSomaticCopyNumbers();

    @NotNull
    public abstract Set<PurpleGeneCopyNumber> allSomaticGeneCopyNumbers();

    @NotNull
    public abstract Set<PurpleGainLoss> allSomaticGainsLosses();

    @NotNull
    public abstract Set<PurpleGainLoss> reportableSomaticGainsLosses();

    @NotNull
    public abstract List<PurpleGeneCopyNumber> suspectGeneCopyNumbersWithLOH();
}
