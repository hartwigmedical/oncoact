package com.hartwig.oncoact.protect;

import com.google.api.client.util.Lists;
import com.google.common.base.Strings;
import com.hartwig.hmftools.datamodel.linx.LinxFusion;
import com.hartwig.hmftools.datamodel.purple.*;
import com.hartwig.oncoact.variant.ReportableVariant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public final class EventGenerator {

    private EventGenerator() {
    }


    @NotNull
    public static String determineVariantAnnotationCanonicalReport(@NotNull String hgvsCoding, @NotNull String hgvsProtein) {
        if (!hgvsCoding.isEmpty() && !hgvsProtein.isEmpty()) {
            return hgvsCoding + " (" + hgvsProtein + ")";
        } else if (!hgvsCoding.isEmpty()) {
            return hgvsCoding;
        } else if (!hgvsProtein.isEmpty()) {
            return hgvsProtein;
        }
        return org.apache.logging.log4j.util.Strings.EMPTY;
    }

    @NotNull
    public static String determineVariantAnnotationClinicalReport(@Nullable PurpleTranscriptImpact purpleTranscriptImpact) {
        if (purpleTranscriptImpact != null) {
            String hgvsCoding = purpleTranscriptImpact.hgvsCodingImpact();
            String hgvsProtein = purpleTranscriptImpact.hgvsProteinImpact();
            return determineVariantAnnotationCanonicalReport(hgvsCoding, hgvsProtein);
        }
        return org.apache.logging.log4j.util.Strings.EMPTY;
    }

    @NotNull
    public static List<String> determineVariantAnnotationReport(@NotNull String hgvsCoding, @NotNull String hgvsProtein,
                                                                @Nullable PurpleTranscriptImpact purpleTranscriptImpact) {

        List<String> annotationList = Lists.newArrayList();
        annotationList.add(determineVariantAnnotationCanonicalReport(hgvsCoding, hgvsProtein));
        if (purpleTranscriptImpact != null && !hgvsCoding.equals(purpleTranscriptImpact.hgvsCodingImpact())) {
            annotationList.add(determineVariantAnnotationClinicalReport(purpleTranscriptImpact));
        }
        return annotationList;
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
    public static String gainLossEvent(@NotNull PurpleGainLoss gainLoss) {
        return gainLoss.interpretation().toString().toLowerCase().replaceAll("_", " ");
    }

    @NotNull
    public static String fusionEvent(@NotNull LinxFusion fusion) {
        return fusion.geneStart() + " - " + fusion.geneEnd() + " fusion";
    }
}