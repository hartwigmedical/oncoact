package com.hartwig.oncoact.common.variant.impact;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.common.variant.CodingEffect;

import htsjdk.variant.variantcontext.VariantContext;

// methods for reading from and writing to VCFs
public final class VariantImpactSerialiser
{
    public static final String VAR_IMPACT = "IMPACT";

    // in the VCF, the components of the variant impact are separated by ',' and effects are separated by '&'
    // other reportable effects are separated by '-' and their sub-details by '|'

    public static List<String> toVcfData(final VariantImpact impact)
    {
        return Lists.newArrayList(
                impact.CanonicalGeneName,
                impact.CanonicalTranscript,
                impact.CanonicalEffect,
                String.valueOf(impact.CanonicalCodingEffect),
                String.valueOf(impact.CanonicalSpliceRegion),
                impact.CanonicalHgvsCoding,
                impact.CanonicalHgvsProtein,
                impact.OtherReportableEffects,
                String.valueOf(impact.WorstCodingEffect),
                String.valueOf(impact.GenesAffected));
    }

    public static VariantImpact fromVariantContext(final VariantContext context)
    {
        return fromAttributeValues(context.getAttributeAsStringList(VAR_IMPACT, ""));
    }

    public static VariantImpact fromAttributeValues(final List<String> impactValues)
    {
        if(impactValues.size() != 10)
        {
            return new VariantImpact(
                    "", "", "", CodingEffect.UNDEFINED, "", "",
                    false, "", CodingEffect.UNDEFINED, 0);
        }

        int index = 0;
        String canonicalGeneName = impactValues.get(index++);
        String canonicalTranscript = impactValues.get(index++);
        String canonicalEffect = impactValues.get(index++);
        CodingEffect canonicalCodingEffect = CodingEffect.valueOf(impactValues.get(index++));

        boolean canonicalSpliceRegion = Boolean.parseBoolean(impactValues.get(index++));
        String canonicalHgvsCodingImpact = impactValues.get(index++);
        String canonicalHgvsProteinImpact = impactValues.get(index++);

        String otherReportableEffects = impactValues.get(index++);

        CodingEffect worstCodingEffect = CodingEffect.valueOf(impactValues.get(index++));
        int genesAffected = Integer.parseInt(impactValues.get(index++));

        return new VariantImpact(
                canonicalGeneName, canonicalTranscript, canonicalEffect, canonicalCodingEffect, canonicalHgvsCodingImpact,
                canonicalHgvsProteinImpact, canonicalSpliceRegion, otherReportableEffects, worstCodingEffect, genesAffected);
    }
}
