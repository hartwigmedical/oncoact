package com.hartwig.oncoact.protect;

import java.util.Set;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.oncoact.orange.datamodel.linx.LinxFusion;
import com.hartwig.oncoact.orange.datamodel.purple.PurpleCodingEffect;
import com.hartwig.oncoact.orange.datamodel.purple.PurpleGainLoss;
import com.hartwig.oncoact.orange.datamodel.purple.PurpleVariant;
import com.hartwig.oncoact.orange.datamodel.purple.PurpleVariantEffect;
import com.hartwig.oncoact.orange.datamodel.purple.Variant;
import com.hartwig.oncoact.variant.AltTranscriptReportableInfo;
import com.hartwig.oncoact.variant.ReportableVariant;

import org.jetbrains.annotations.NotNull;

public final class EventGenerator {

    private EventGenerator() {
    }

    @NotNull
    public static String variantEvent(@NotNull Variant variant) {
        if (variant instanceof ReportableVariant) {
            return reportableVariantEvent((ReportableVariant) variant);
        } else {
            return canonicalVariantEvent(variant);
        }
    }

    @NotNull
    private static String reportableVariantEvent(@NotNull ReportableVariant reportableVariant) {
        return reportableVariant.isCanonical() ? canonicalVariantEvent(reportableVariant) : nonCanonicalVariantEvent(reportableVariant);
    }

    @NotNull
    private static String canonicalVariantEvent(@NotNull Variant variant) {
        if (variant instanceof ReportableVariant) {
            ReportableVariant reportable = (ReportableVariant) variant;
            return toVariantEvent(reportable.canonicalHgvsProteinImpact(),
                    reportable.canonicalHgvsCodingImpact(),
                    reportable.canonicalEffect(),
                    reportable.canonicalCodingEffect());
        } else {
            PurpleVariant purple = (PurpleVariant) variant;
            return toVariantEvent(purple.canonicalImpact().hgvsProteinImpact(),
                    purple.canonicalImpact().hgvsCodingImpact(),
                    concat(purple.canonicalImpact().effects()),
                    purple.canonicalImpact().codingEffect());
        }
    }

    @NotNull
    private static String concat(@NotNull Set<PurpleVariantEffect> effects) {
        StringJoiner joiner = new StringJoiner("&");
        for (PurpleVariantEffect effect : effects) {
            joiner.add(effect.toString().toLowerCase());
        }
        return joiner.toString();
    }

    @NotNull
    private static String nonCanonicalVariantEvent(@NotNull ReportableVariant variant) {
        return toVariantEvent(AltTranscriptReportableInfo.firstOtherHgvsProteinImpact(variant.otherReportedEffects()),
                AltTranscriptReportableInfo.firstOtherHgvsCodingImpact(variant.otherReportedEffects()),
                AltTranscriptReportableInfo.firstOtherEffects(variant.otherReportedEffects()),
                AltTranscriptReportableInfo.firstOtherCodingEffect(variant.otherReportedEffects()));
    }

    @NotNull
    @VisibleForTesting
    static String toVariantEvent(@NotNull String protein, @NotNull String coding, @NotNull String effect,
            @NotNull PurpleCodingEffect codingEffect) {
        if (!protein.isEmpty() && !protein.equals("p.?")) {
            return protein;
        }

        if (!coding.isEmpty()) {
            return codingEffect == PurpleCodingEffect.SPLICE ? coding + " splice" : coding;
        }

        return effect;
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
