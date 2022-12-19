package com.hartwig.oncoact.common.datamodel;

import com.hartwig.oncoact.common.interpretation.DataUtil;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleCodingEffect;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleHotspotType;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleVariantType;
import com.hartwig.oncoact.common.orange.datamodel.purple.Variant;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ReportableVariant implements Variant {

    @NotNull
    public abstract ReportableVariantSource source();

    @NotNull
    @Override
    public abstract PurpleVariantType type();

    @NotNull
    @Override
    public abstract String gene();

    @NotNull
    public abstract String transcript();

    public abstract boolean isCanonical();

    @NotNull
    @Override
    public abstract String chromosome();

    @Override
    public abstract int position();

    @NotNull
    @Value.Derived
    public String gDNA() {
        return chromosome() + ":" + position();
    }

    @NotNull
    @Override
    public abstract String ref();

    @NotNull
    @Override
    public abstract String alt();

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
        return DataUtil.formatPercentage(100 * Math.max(0, Math.min(1, vaf)));
    }

    @NotNull
    public abstract PurpleHotspotType hotspot();

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
    public abstract GenotypeStatus genotypeStatus();

    @Nullable
    public abstract Integer localPhaseSet();
}
