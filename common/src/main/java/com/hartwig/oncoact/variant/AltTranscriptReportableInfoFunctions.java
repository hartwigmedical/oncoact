package com.hartwig.oncoact.variant;

import javax.annotation.Nullable;

import com.hartwig.oncoact.orange.purple.PurpleCodingEffect;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class AltTranscriptReportableInfoFunctions {

    private AltTranscriptReportableInfoFunctions() {
    }

    @NotNull
    public static String firstOtherTranscript(@NotNull String otherReportedEffects) {
        if (otherReportedEffects.isEmpty()) {
            return Strings.EMPTY;
        }

        return getFirstAltTranscriptInfo(otherReportedEffects).transName();
    }

    @NotNull
    public static String firstOtherEffects(@NotNull String otherReportedEffects) {
        if (otherReportedEffects.isEmpty()) {
            return Strings.EMPTY;
        }

        return getFirstAltTranscriptInfo(otherReportedEffects).effects();
    }

    @NotNull
    public static String firstOtherHgvsCodingImpact(@NotNull String otherReportedEffects) {
        if (otherReportedEffects.isEmpty()) {
            return Strings.EMPTY;
        }

        return getFirstAltTranscriptInfo(otherReportedEffects).hgvsCoding();
    }

    @NotNull
    public static String firstOtherHgvsProteinImpact(@NotNull String otherReportedEffects) {
        if (otherReportedEffects.isEmpty()) {
            return Strings.EMPTY;
        }

        return getFirstAltTranscriptInfo(otherReportedEffects).hgvsProtein();
    }

    @NotNull
    public static PurpleCodingEffect firstOtherCodingEffect(@NotNull String otherReportedEffects) {
        if (otherReportedEffects.isEmpty()) {
            return PurpleCodingEffect.UNDEFINED;
        }

        return getFirstAltTranscriptInfo(otherReportedEffects).codingEffect();
    }

    @Nullable
    private static AltTranscriptReportableInfo getFirstAltTranscriptInfo(@NotNull String otherReportableEffects) {
        if (otherReportableEffects.isEmpty()) {
            return null;
        }

        return AltTranscriptReportableInfoFactory.create(otherReportableEffects).get(0);
    }
}
