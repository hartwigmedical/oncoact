package com.hartwig.oncoact.common.drivercatalog.panel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hartwig.oncoact.common.drivercatalog.DriverCategory;
import com.hartwig.oncoact.common.drivercatalog.DriverImpact;
import com.hartwig.oncoact.common.variant.CodingEffect;
import com.hartwig.oncoact.common.variant.VariantType;
import com.hartwig.oncoact.common.variant.impact.AltTranscriptReportableInfo;
import com.hartwig.oncoact.common.variant.impact.VariantImpact;
import com.hartwig.oncoact.common.variant.impact.VariantEffect;

public class ReportablePredicate
{
    public static final int MAX_ONCO_REPEAT_COUNT = 7;

    private final int mMaxRepeatCount;
    private final Map<String, DriverGene> mDriverGeneMap;

    public ReportablePredicate(final DriverCategory type, final List<DriverGene> driverGenes)
    {
        mDriverGeneMap = driverGenes.stream()
                .filter(x -> x.likelihoodType().equals(type) && x.reportSomatic())
                .collect(Collectors.toMap(DriverGene::gene, x -> x));

        mMaxRepeatCount = type == DriverCategory.ONCO ? MAX_ONCO_REPEAT_COUNT : -1;
    }

    public boolean isReportable(final VariantImpact variantImpact, final VariantType type, int repeatCount, boolean isHotspot)
    {
        if(isReportable(
            variantImpact.CanonicalGeneName, type, repeatCount, isHotspot, variantImpact.CanonicalCodingEffect, variantImpact.CanonicalEffect))
        {
            return true;
        }

        if(variantImpact.OtherReportableEffects.isEmpty())
            return false;

        List<AltTranscriptReportableInfo> altTransEffects = AltTranscriptReportableInfo.parseAltTranscriptInfo(variantImpact.OtherReportableEffects);

        return altTransEffects.stream().anyMatch(x ->
                isReportable(variantImpact.CanonicalGeneName, type, repeatCount, isHotspot, x.Effect, x.Effects));
    }

    public boolean isReportable(
            final String gene, final VariantType type, int repeatCount, boolean isHotspot,
            final CodingEffect codingEffect, final String effects)
    {
        final DriverGene driverGene = mDriverGeneMap.get(gene);

        if(driverGene == null)
            return false;

        if(type.equals(VariantType.INDEL) && mMaxRepeatCount > 0 && repeatCount > mMaxRepeatCount)
            return false;

        if(isHotspot && driverGene.reportSomaticHotspot())
            return true;

        DriverImpact impact = DriverImpact.select(type, codingEffect);

        // splice ranks above missense so if a gene is reportable for missense but not splice, ensure this is handled
        boolean hasMissense = effects.contains(VariantEffect.MISSENSE.effect());

        if(hasMissense && driverGene.reportMissenseAndInframe())
            return true;

        switch(impact)
        {
            case NONSENSE:
            case FRAMESHIFT:
                return driverGene.reportNonsenseAndFrameshift();

            case SPLICE:
                return driverGene.reportSplice();

            case MISSENSE:
            case INFRAME:
                return driverGene.reportMissenseAndInframe();

            default:
                return false;
        }
    }
}
