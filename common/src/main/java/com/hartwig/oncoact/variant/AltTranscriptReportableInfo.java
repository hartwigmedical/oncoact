package com.hartwig.oncoact.variant;

import java.util.List;
import java.util.StringJoiner;

import javax.annotation.Nullable;

import com.hartwig.oncoact.orange.purple.PurpleCodingEffect;

import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class AltTranscriptReportableInfo {
    public final String TransName;
    public final String HgvsCoding;
    public final String HgvsProtein;
    public final String Effects;
    public final PurpleCodingEffect Effect;

    public static final String VAR_IMPACT_OTHER_REPORT_ITEM_DELIM = "|";
    public static final String VAR_IMPACT_OTHER_REPORT_DELIM = "--"; // a single hyphen conflicts with the HGVS coding annotation
    public static final int VAR_IMPACT_OTHER_REPORT_ITEM_COUNT = 5;

    public AltTranscriptReportableInfo(final String transName, final String hgvsCoding, final String hgvsProtein, final String effects,
            final PurpleCodingEffect codingEffect) {
        TransName = transName;
        HgvsCoding = hgvsCoding;
        HgvsProtein = hgvsProtein;
        Effects = effects;
        Effect = codingEffect;
    }

    @NotNull
    public static List<AltTranscriptReportableInfo> parseAltTranscriptInfo(@NotNull String otherReportableEffects) {
        List<AltTranscriptReportableInfo> altTransInfos = Lists.newArrayList();

        String[] altTransInfoItems = otherReportableEffects.split(VAR_IMPACT_OTHER_REPORT_DELIM, -1);

        for (String altTransInfoStr : altTransInfoItems) {
            AltTranscriptReportableInfo altTransInfo = parse(altTransInfoStr);

            if (altTransInfo != null) {
                altTransInfos.add(altTransInfo);
            }
        }

        return altTransInfos;
    }

    @NotNull
    private static AltTranscriptReportableInfo parse(@NotNull String transInfo) {
        String[] transValues = transInfo.split("\\" + VAR_IMPACT_OTHER_REPORT_ITEM_DELIM, -1);
        if (transValues.length != VAR_IMPACT_OTHER_REPORT_ITEM_COUNT) {
            return null;
        }

        return new AltTranscriptReportableInfo(transValues[0],
                transValues[1],
                transValues[2],
                transValues[3],
                PurpleCodingEffect.valueOf(transValues[4]));
    }

    @NotNull
    public String serialise() {
        return serialise(TransName, HgvsCoding, HgvsProtein, Effects, Effect);
    }

    @NotNull
    public static String serialise(@NotNull String transName, @NotNull String hgvsCoding, @NotNull String hgvsProtein,
            @NotNull String effects, @NotNull PurpleCodingEffect codingEffect) {
        // eg ENST00000579755|c.209_210delCCinsTT|p.Pro70Leu|missense_variant|MISSENSE;
        StringJoiner sj = new StringJoiner(VAR_IMPACT_OTHER_REPORT_ITEM_DELIM);
        sj.add(transName);
        sj.add(hgvsCoding);
        sj.add(hgvsProtein);
        sj.add(effects);
        sj.add(codingEffect.toString());
        return sj.toString();
    }

    // Convenience methods for PROTECT

    @Nullable
    public static AltTranscriptReportableInfo getFirstAltTranscriptInfo(final String otherReportableEffects) {
        if (otherReportableEffects.isEmpty()) {
            return null;
        }

        return parseAltTranscriptInfo(otherReportableEffects).get(0);
    }

    @NotNull
    public static String firstOtherTranscript(@NotNull String otherReportedEffects) {
        if (otherReportedEffects.isEmpty()) {
            return Strings.EMPTY;
        }

        return getFirstAltTranscriptInfo(otherReportedEffects).TransName;
    }

    @NotNull
    public static String firstOtherEffects(@NotNull String otherReportedEffects) {
        if (otherReportedEffects.isEmpty()) {
            return Strings.EMPTY;
        }

        return getFirstAltTranscriptInfo(otherReportedEffects).Effects;
    }

    @NotNull
    public static String firstOtherHgvsCodingImpact(@NotNull String otherReportedEffects) {
        if (otherReportedEffects.isEmpty()) {
            return Strings.EMPTY;
        }

        return getFirstAltTranscriptInfo(otherReportedEffects).HgvsCoding;
    }

    @NotNull
    public static String firstOtherHgvsProteinImpact(@NotNull String otherReportedEffects) {
        if (otherReportedEffects.isEmpty()) {
            return Strings.EMPTY;
        }

        return getFirstAltTranscriptInfo(otherReportedEffects).HgvsProtein;
    }

    @NotNull
    public static PurpleCodingEffect firstOtherCodingEffect(@NotNull String otherReportedEffects) {
        if (otherReportedEffects.isEmpty()) {
            return PurpleCodingEffect.UNDEFINED;
        }

        return getFirstAltTranscriptInfo(otherReportedEffects).Effect;
    }
}
