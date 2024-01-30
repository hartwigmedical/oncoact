package com.hartwig.oncoact.protect;

import java.util.Set;
import java.util.StringJoiner;

import com.google.common.base.Strings;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.PurpleVariant;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;
import com.hartwig.hmftools.datamodel.purple.Variant;
import com.hartwig.oncoact.copynumber.PurpleGainLossData;
import com.hartwig.oncoact.variant.ReportableVariant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EventGenerator {

    private EventGenerator() {
    }

    @NotNull
    public static String determineVariantAnnotation(@Nullable String hgvsCoding, @Nullable String hgvsProtein, @NotNull String effect,
            @NotNull PurpleCodingEffect codingEffect) {
        if (!Strings.isNullOrEmpty(hgvsProtein) && !hgvsProtein.equals("p.?")) {
            return hgvsProtein;
        }

        if (!Strings.isNullOrEmpty(hgvsCoding)) {
            return codingEffect == PurpleCodingEffect.SPLICE ? hgvsCoding + " splice" : hgvsCoding;
        }

        return effect;
    }

    @NotNull
    public static String variantEvent(@NotNull Variant variant) {
        if (variant instanceof ReportableVariant) {
            return reportableVariant((ReportableVariant) variant);
        } else if (variant instanceof PurpleVariant) {
            PurpleTranscriptImpact purpleTranscriptImpact = ((PurpleVariant) variant).canonicalImpact();
            return determineVariantAnnotationClinical(purpleTranscriptImpact);
        } else {
            throw new IllegalArgumentException(String.format("Unexpected variant type. Variant was: %s", variant));
        }
    }

    @NotNull
    private static String reportableVariant(final ReportableVariant variant) {
        String variantEvent = determineVariantAnnotation(variant.canonicalHgvsCodingImpact(),
                variant.canonicalHgvsProteinImpact(),
                variant.canonicalEffect(),
                variant.canonicalCodingEffect());
        PurpleTranscriptImpact otherImpactClinical = variant.otherImpactClinical();
        if (otherImpactClinical == null) {
            return variantEvent;
        }
        String clinical = determineVariantAnnotationClinical(otherImpactClinical);
        if (variantEvent.equals(clinical)) {
            return variantEvent;
        }
        return variantEvent + " (" + clinical + ")";
    }

    @NotNull
    private static String determineVariantAnnotationClinical(@NotNull PurpleTranscriptImpact purpleTranscriptImpact) {
        return determineVariantAnnotation(purpleTranscriptImpact.hgvsCodingImpact(),
                purpleTranscriptImpact.hgvsProteinImpact(),
                concat(purpleTranscriptImpact.effects()),
                purpleTranscriptImpact.codingEffect());
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
    public static String gainLossEvent(@NotNull PurpleGainLossData gainLoss) {
        return gainLoss.interpretation().toString().toLowerCase().replaceAll("_", " ");
    }

    @NotNull
    public static String fusionEvent(@NotNull LinxFusion fusion) {
        return fusion.geneStart() + " - " + fusion.geneEnd() + " fusion";
    }
}