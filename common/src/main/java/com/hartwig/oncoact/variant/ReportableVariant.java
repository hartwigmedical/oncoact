package com.hartwig.oncoact.variant;

import com.hartwig.hmftools.datamodel.purple.Hotspot;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleGenotypeStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.Variant;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ReportableVariant implements Variant {

    @NotNull
    public abstract ReportableVariantSource source();

    @Nullable
    public abstract PurpleTranscriptImpact otherImpactClinical();

    @NotNull
    public abstract String transcript();

    public abstract boolean isCanonical();

    @NotNull
    @Value.Derived
    public String gDNA() {
        return chromosome() + ":" + position();
    }

    @Nullable
    public abstract Integer affectedCodon();

    @Nullable
    public abstract Integer affectedExon();

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

    public abstract int totalReadCount();

    public abstract int alleleReadCount();

    @Nullable
    public abstract Double totalCopyNumber();

    @Nullable
    public abstract Double alleleCopyNumber();

    @Nullable
    public abstract Double minorAlleleCopyNumber();

    @NotNull
    public abstract String tVAF();

    @Nullable
    public abstract Hotspot hotspot();

    @Nullable
    public abstract Double clonalLikelihood();

    @Nullable
    public abstract Double driverLikelihood();

    @Nullable
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
