package com.hartwig.oncoact.protect;

import java.util.Set;
import java.util.StringJoiner;

import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleGainLoss;
import com.hartwig.hmftools.datamodel.purple.PurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.PurpleVariant;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;
import com.hartwig.hmftools.datamodel.purple.Variant;
import com.hartwig.oncoact.variant.ReportableVariant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EventGenerator {

    private EventGenerator() {
    }

    @NotNull
    public static StringBuilder determineVariantAnnotations(@NotNull String hgvsCoding, @NotNull String hgvsProtein,
            @Nullable PurpleTranscriptImpact purpleTranscriptImpact, @NotNull PurpleCodingEffect canonicalCodingEffect,
            @NotNull String canonicalEffect) {
        StringBuilder annotation = new StringBuilder();
        if (purpleTranscriptImpact != null) {
            if (!hgvsProtein.equals(purpleTranscriptImpact.hgvsProteinImpact())
                    && !hgvsCoding.equals(purpleTranscriptImpact.hgvsCodingImpact())) {
                annotation.append(determineVariantAnnotation(hgvsCoding, hgvsProtein, canonicalEffect, canonicalCodingEffect));
            } else {
                annotation.append(determineVariantAnnotation(hgvsCoding, hgvsProtein, canonicalEffect, canonicalCodingEffect));
            }
        } else {
            annotation.append(determineVariantAnnotation(hgvsCoding, hgvsProtein, canonicalEffect, canonicalCodingEffect));
        }
        return annotation;
    }

    @NotNull
    public static String determineVariantAnnotation(@Nullable String hgvsCoding, @Nullable String hgvsProtein, @NotNull String effect,
            @NotNull PurpleCodingEffect codingEffect) {
        if (hgvsProtein != null && !hgvsProtein.isEmpty() && !hgvsProtein.equals("p.?")) {
            return hgvsProtein;
        }

        if (hgvsCoding != null && !hgvsCoding.isEmpty()) {
            return codingEffect == PurpleCodingEffect.SPLICE ? hgvsCoding + " splice" : hgvsCoding;
        }

        return effect;
    }

    @Nullable
    public static String determineVariantAnnotationClinical(@Nullable PurpleTranscriptImpact purpleTranscriptImpact) {
        if (purpleTranscriptImpact != null) {
            String hgvsCoding = purpleTranscriptImpact.hgvsCodingImpact();
            String hgvsProtein = purpleTranscriptImpact.hgvsProteinImpact();
            return determineVariantAnnotation(hgvsCoding,
                    hgvsProtein,
                    concat(purpleTranscriptImpact.effects()),
                    purpleTranscriptImpact.codingEffect());
        }
        return null;
    }

    @NotNull
    public static String variantEvent(@NotNull Variant variant) {
        if (variant instanceof ReportableVariant) {
            ReportableVariant reportableVariant = (ReportableVariant) variant;
            String variantEvent = determineVariantAnnotations(reportableVariant.canonicalHgvsCodingImpact(),
                    reportableVariant.canonicalHgvsProteinImpact(),
                    reportableVariant.otherImpactClinical(),
                    reportableVariant.canonicalCodingEffect(),
                    reportableVariant.canonicalEffect()).toString();
            String clinical = determineVariantAnnotationClinical(reportableVariant.otherImpactClinical());
            if (clinical == null || variantEvent.equals(clinical)) {
                return variantEvent;
            }
            return variantEvent + " (" + clinical + ")";
            
        } else {
            return canonicalVariantEvent(variant);
        }
    }

    @NotNull
    private static String canonicalVariantEvent(@NotNull Variant variant) {
        if (variant instanceof ReportableVariant) {
            ReportableVariant reportable = (ReportableVariant) variant;
            return determineVariantAnnotation(reportable.canonicalHgvsCodingImpact(),
                    reportable.canonicalHgvsProteinImpact(),
                    reportable.canonicalEffect(),
                    reportable.canonicalCodingEffect());
        } else {
            PurpleVariant purple = (PurpleVariant) variant;
            return determineVariantAnnotation(purple.canonicalImpact().hgvsCodingImpact(),
                    purple.canonicalImpact().hgvsProteinImpact(),
                    concat(purple.canonicalImpact().effects()),
                    purple.canonicalImpact().codingEffect());
        }
    }

    @NotNull
    public static String concat(@NotNull Set<PurpleVariantEffect> effects) {
        StringJoiner joiner = new StringJoiner("&");
        for (PurpleVariantEffect effect : effects) {
            joiner.add(effect.toString().toLowerCase());
        }
        return joiner.toString();
    }

    @NotNull
    public static String gainLossEvent(@NotNull PurpleGainLoss gainLoss) {
        return gainLoss.interpretation().toString().toLowerCase().replaceAll("_", " ");
    }

    @NotNull
    public static String fusionEvent(@NotNull LinxFusion fusion) {
        return fusion.geneStart() + " - " + fusion.geneEnd() + " fusion";
    }
}