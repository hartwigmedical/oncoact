package com.hartwig.oncoact.variant;

import java.util.List;
import java.util.StringJoiner;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.datamodel.purple.PurpleCodingEffect;
import com.hartwig.hmftools.datamodel.purple.PurpleTranscriptImpact;
import com.hartwig.hmftools.datamodel.purple.PurpleVariantEffect;

import org.jetbrains.annotations.NotNull;

public final class AltTranscriptReportableInfoFactory {

    private static final String VAR_IMPACT_OTHER_REPORT_ITEM_DELIM = "|";
    private static final String VAR_IMPACT_OTHER_REPORT_DELIM = "--"; // a single hyphen conflicts with the HGVS coding annotation
    private static final int VAR_IMPACT_OTHER_REPORT_ITEM_COUNT = 5;

    private AltTranscriptReportableInfoFactory() {
    }

    @NotNull
    public static String serialize(@NotNull Iterable<PurpleTranscriptImpact> impacts) {
        StringJoiner impactJoiner = new StringJoiner(VAR_IMPACT_OTHER_REPORT_DELIM);
        for (PurpleTranscriptImpact impact : impacts) {
            StringJoiner fieldJoiner = new StringJoiner(VAR_IMPACT_OTHER_REPORT_ITEM_DELIM);
            fieldJoiner.add(impact.transcript()).add(impact.hgvsCodingImpact()).add(impact.hgvsProteinImpact())
                    .add(concatEffects(impact.effects())).add(impact.codingEffect().toString());
            impactJoiner.add(fieldJoiner.toString());
        }
        return impactJoiner.toString();
    }

    @NotNull
    private static String concatEffects(@NotNull Iterable<PurpleVariantEffect> effects) {
        StringJoiner joiner = new StringJoiner("&");
        for (PurpleVariantEffect effect : effects) {
            joiner.add(effect.toString().toLowerCase());
        }
        return joiner.toString();
    }

    @NotNull
    public static List<AltTranscriptReportableInfo> create(@NotNull String otherReportableEffects) {
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

        return ImmutableAltTranscriptReportableInfo.builder()
                .transName(transValues[0])
                .hgvsCoding(transValues[1])
                .hgvsProtein(transValues[2])
                .effects(transValues[3])
                .codingEffect(PurpleCodingEffect.valueOf(transValues[4]))
                .build();
    }
}
