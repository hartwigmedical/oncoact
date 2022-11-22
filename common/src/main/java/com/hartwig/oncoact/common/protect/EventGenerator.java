package com.hartwig.oncoact.common.protect;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.oncoact.common.linx.LinxFusion;
import com.hartwig.oncoact.common.purple.loader.GainLoss;
import com.hartwig.oncoact.common.variant.CodingEffect;
import com.hartwig.oncoact.common.variant.ReportableVariant;
import com.hartwig.oncoact.common.variant.Variant;
import com.hartwig.oncoact.common.variant.impact.AltTranscriptReportableInfo;

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
        return toVariantEvent(variant.canonicalHgvsProteinImpact(),
                variant.canonicalHgvsCodingImpact(),
                variant.canonicalEffect(),
                variant.canonicalCodingEffect());
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
            @NotNull CodingEffect codingEffect) {
        if (!protein.isEmpty() && !protein.equals("p.?")) {
            return protein;
        }

        if (!coding.isEmpty()) {
            return codingEffect == CodingEffect.SPLICE ? coding + " splice" : coding;
        }

        return effect;
    }

    @NotNull
    public static String copyNumberEvent(@NotNull GainLoss gainLoss) {
        return gainLoss.interpretation().display();
    }

    @NotNull
    public static String fusionEvent(@NotNull LinxFusion fusion) {
        return fusion.geneStart() + " - " + fusion.geneEnd() + " fusion";
    }
}
