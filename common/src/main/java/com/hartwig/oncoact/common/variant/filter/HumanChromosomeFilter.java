package com.hartwig.oncoact.common.variant.filter;

import com.hartwig.oncoact.genome.HumanChromosome;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.filter.VariantContextFilter;

public class HumanChromosomeFilter implements VariantContextFilter {

    @Override
    public boolean test(final VariantContext record) {
        return HumanChromosome.contains(record.getContig());
    }
}
