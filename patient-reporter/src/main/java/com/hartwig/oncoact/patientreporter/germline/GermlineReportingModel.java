package com.hartwig.oncoact.patientreporter.germline;

import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.hmftools.datamodel.purple.PurpleGenotypeStatus;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GermlineReportingModel {

    private static final Logger LOGGER = LogManager.getLogger(GermlineReportingModel.class);

    @NotNull
    private final List<GermlineReportingEntry> entries;

    GermlineReportingModel(@NotNull final List<GermlineReportingEntry> entries) {
        this.entries = entries;
    }

    public boolean notifyGermlineVariant(@NotNull ReportableVariant germlineVariant, boolean flagGermlineOnReport,
            @NotNull Set<String> germlineGenesWithIndependentHits) {
        assert (germlineVariant.source() == ReportableVariantSource.GERMLINE
                || germlineVariant.source() == ReportableVariantSource.GERMLINE_ONLY);

        if (!flagGermlineOnReport) {
            return false;
        }

        GermlineReportingEntry reportingEntry = entryForGene(germlineVariant.gene());
        if (reportingEntry == null) {
            LOGGER.warn("No reporting entry found for germline gene {}!", germlineVariant.gene());
            return false;
        }

        switch (reportingEntry.condition()) {
            case ALWAYS: {
                return true;
            }
            case NEVER: {
                return false;
            }
            case ONLY_GERMLINE_HOM: {
                return germlineVariant.genotypeStatus() == PurpleGenotypeStatus.HOM_ALT || germlineGenesWithIndependentHits.contains(
                        germlineVariant.gene());
            }
            case ONLY_SPECIFIC_VARIANT: {
                String condition = reportingEntry.conditionFilter();
                if (condition == null) {
                    LOGGER.warn("No condition specified for germline reporting entry on {}!", germlineVariant.gene());
                    return false;
                } else {
                    return condition.equals(germlineVariant.canonicalHgvsProteinImpact());
                }
            }
            default: {
                LOGGER.warn("Unrecognized germline reporting entry value: {}", reportingEntry.condition());
                return false;
            }
        }
    }

    @NotNull
    @VisibleForTesting
    List<GermlineReportingEntry> entries() {
        return entries;
    }

    @Nullable
    @VisibleForTesting
    GermlineReportingEntry entryForGene(@NotNull String gene) {
        for (GermlineReportingEntry entry : entries) {
            if (entry.gene().equals(gene)) {
                return entry;
            }
        }
        return null;
    }
}
