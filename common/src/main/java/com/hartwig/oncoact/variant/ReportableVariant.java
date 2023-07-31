package com.hartwig.oncoact.variant;

import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleGenotypeStatus;
import com.hartwig.hmftools.datamodel.purple.Hotspot;
import com.hartwig.hmftools.datamodel.purple.PurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.Variant;
import com.hartwig.oncoact.util.Formats;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ReportableVariant implements Variant {

    @NotNull
    public abstract ReportableVariantSource source();

    @Nullable
    public abstract PurpleTranscriptImpact otherImpacts();

    @NotNull
    public abstract String transcript();

    public abstract boolean isCanonical();

    @NotNull
    @Value.Derived
    public String gDNA() {
        return chromosome() + ":" + position();
    }

    @NotNull
    public abstract String canonicalTranscript();

    @NotNull
    public abstract String canonicalEffect();

    @NotNull
    public abstract PurpleCodingEffect canonicalCodingEffect();

    @NotNull
    public abstract String canonicalHgvsCodingImpact();

    @NotNull
    public abstract String canonicalHgvsProteinImpact();

    @NotNull
    public abstract String otherReportedEffects();

    public abstract int totalReadCount();

    public abstract int alleleReadCount();

    public abstract double totalCopyNumber();

    public abstract double alleleCopyNumber();

    public abstract double minorAlleleCopyNumber();

    @NotNull
    @Value.Derived
    public String tVAF() {
        double vaf = alleleCopyNumber() / totalCopyNumber();
        return Formats.formatPercentage(100 * Math.max(0, Math.min(1, vaf)));
    }

    @NotNull
    public abstract Hotspot hotspot();

    public abstract double clonalLikelihood();

    public abstract double driverLikelihood();

    @NotNull
    @Value.Derived
    public DriverInterpretation driverLikelihoodInterpretation() {
        return DriverInterpretation.interpret(driverLikelihood());
    }

    @Nullable
    public abstract Boolean biallelic();

    @NotNull
    public abstract PurpleGenotypeStatus genotypeStatus();

    @Nullable
    public abstract Integer localPhaseSet();
}
