package com.hartwig.oncoact.common.variant;

import static com.hartwig.oncoact.common.variant.PurpleVcfTags.PURPLE_AF_INFO;
import static com.hartwig.oncoact.common.variant.PurpleVcfTags.PURPLE_BIALLELIC_FLAG;
import static com.hartwig.oncoact.common.variant.PurpleVcfTags.PURPLE_CN_INFO;
import static com.hartwig.oncoact.common.variant.PurpleVcfTags.PURPLE_MINOR_ALLELE_CN_INFO;
import static com.hartwig.oncoact.common.variant.PurpleVcfTags.PURPLE_VARIANT_CN_INFO;
import static com.hartwig.oncoact.common.variant.SageVcfTags.MICROHOMOLOGY_FLAG;
import static com.hartwig.oncoact.common.variant.SageVcfTags.TRINUCLEOTIDE_FLAG;

import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.hartwig.oncoact.common.drivercatalog.DriverImpact;
import com.hartwig.oncoact.common.genotype.GenotypeStatus;
import com.hartwig.oncoact.common.variant.impact.VariantImpact;
import com.hartwig.oncoact.common.variant.impact.VariantImpactSerialiser;
import com.hartwig.oncoact.genome.GenomePosition;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;

public class VariantContextDecorator implements GenomePosition
{
    private final VariantContext mContext;
    private final VariantType mType;
    private final String mFilter;
    private final String mRef;
    private final String mAlt;
    private final VariantTier mTier;

    @Nullable
    private VariantImpact mVariantImpact;
    @Nullable
    private DriverImpact mDriverImpact;

    public VariantContextDecorator(final VariantContext context)
    {
        mContext = context;
        mFilter = displayFilter(context);
        mType = VariantType.type(context);
        mRef = getRef(context);
        mAlt = getAlt(context);
        mTier = VariantTier.fromContext(context);
        mVariantImpact = null;
        mDriverImpact = null;
    }

    public static String getRef(final VariantContext context)
    {
        return context.getReference().getBaseString();
    }

    public static String getAlt(final VariantContext context)
    {
        return context.getAlternateAlleles().stream().map(Allele::toString).collect(Collectors.joining(","));
    }

    public boolean isPass()
    {
        return mFilter.equals(SomaticVariantFactory.PASS_FILTER);
    }

    public VariantContext context()
    {
        return mContext;
    }
    public String filter()
    {
        return mFilter;
    }
    public VariantType type() { return mType; }
    public String ref()
    {
        return mRef;
    }
    public String alt()
    {
        return mAlt;
    }

    @Override
    public String chromosome()
    {
        return mContext.getContig();
    }

    @Override
    public int position()
    {
        return mContext.getStart();
    }

    public VariantImpact variantImpact()
    {
        if(mVariantImpact == null)
        {
            mVariantImpact = VariantImpactSerialiser.fromVariantContext(mContext);
        }

        return mVariantImpact;
    }

    public String gene()
    {
        return variantImpact().CanonicalGeneName;
    }

    public DriverImpact impact()
    {
        if(mDriverImpact == null)
        {
            mDriverImpact = DriverImpact.select(mType, variantImpact().CanonicalCodingEffect);
        }

        return mDriverImpact;
    }

    public CodingEffect canonicalCodingEffect()
    {
        return variantImpact().CanonicalCodingEffect;
    }

    public double qual()
    {
        return mContext.getPhredScaledQual();
    }

    public double adjustedCopyNumber() { return mContext.getAttributeAsDouble(PURPLE_CN_INFO, 0); }

    public double adjustedVaf()
    {
        return mContext.getAttributeAsDouble(PURPLE_AF_INFO, 0);
    }

    public boolean biallelic() { return mContext.getAttributeAsBoolean(PURPLE_BIALLELIC_FLAG, false); }

    public double minorAlleleCopyNumber() { return mContext.getAttributeAsDouble(PURPLE_MINOR_ALLELE_CN_INFO, 0); }

    public double variantCopyNumber() { return mContext.getAttributeAsDouble(PURPLE_VARIANT_CN_INFO, 0); }

    @NotNull
    public GenotypeStatus genotypeStatus(@NotNull final String sample)
    {
        final Genotype genotype = mContext.getGenotype(sample);
        return genotype != null ? GenotypeStatus.fromGenotype(genotype) : GenotypeStatus.UNKNOWN;
    }

    public VariantTier tier()
    {
        return mTier;
    }

    public int repeatCount() { return mContext.getAttributeAsInt(SageVcfTags.REPEAT_COUNT_FLAG, 0); }

    public String repeatSequence()
    {
        return mContext.getAttributeAsString(SageVcfTags.REPEAT_SEQUENCE_FLAG, Strings.EMPTY);
    }

    public Hotspot hotspot()
    {
        return Hotspot.fromVariant(mContext);
    }
    public boolean isHotspot()
    {
        return hotspot() == Hotspot.HOTSPOT;
    }

    public String trinucleotideContext()
    {
        return mContext.getAttributeAsString(TRINUCLEOTIDE_FLAG, Strings.EMPTY);
    }

    public double mappability()
    {
        return mContext.getAttributeAsDouble(SomaticVariantFactory.MAPPABILITY_TAG, 0);
    }

    public boolean reported()
    {
        return mContext.getAttributeAsBoolean(CommonVcfTags.REPORTED_FLAG, false);
    }

    public String microhomology()
    {
        return mContext.getAttributeAsString(MICROHOMOLOGY_FLAG, Strings.EMPTY);
    }

    @NotNull
    private static String displayFilter(@NotNull final VariantContext context)
    {
        if(context.isFiltered())
        {
            StringJoiner joiner = new StringJoiner(";");
            context.getFilters().forEach(joiner::add);
            return joiner.toString();
        }
        else
        {
            return SomaticVariantFactory.PASS_FILTER;
        }
    }

    @Override
    public String toString()
    {
        return chromosome() + ":" + position() + " " + mRef + '>' + mAlt;
    }
}
