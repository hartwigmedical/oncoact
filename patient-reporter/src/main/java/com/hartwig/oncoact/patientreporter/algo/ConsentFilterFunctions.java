package com.hartwig.oncoact.patientreporter.algo;

import java.util.List;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.hmftools.datamodel.virus.AnnotatedVirus;
import com.hartwig.oncoact.protect.ImmutableProtectEvidence;
import com.hartwig.oncoact.protect.ProtectEvidence;
import com.hartwig.oncoact.variant.ImmutableReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariant;
import com.hartwig.oncoact.variant.ReportableVariantSource;

import org.immutables.value.internal.$guava$.annotations.$VisibleForTesting;
import org.jetbrains.annotations.NotNull;

public final class ConsentFilterFunctions {

    private ConsentFilterFunctions() {
    }

    @NotNull
    public static GenomicAnalysis filter(@NotNull GenomicAnalysis genomicAnalysis,
            @NotNull Boolean germlineReportingLevel) {
        List<ReportableVariantWithNotify> filteredVariantsWithNotify = filterVariants(genomicAnalysis.reportableVariants(),
                genomicAnalysis.notifyGermlineStatusPerVariant(),
                germlineReportingLevel);

        List<ReportableVariant> filteredVariants = Lists.newArrayList();
        Map<ReportableVariant, Boolean> notifyPerVariant = Maps.newHashMap();
        for (ReportableVariantWithNotify filtered : filteredVariantsWithNotify) {
            filteredVariants.add(filtered.variant());
            notifyPerVariant.put(filtered.variant(), filtered.notifyVariant());
        }

        List<ProtectEvidence> filteredTumorSpecificEvidence =
                filterEvidenceForGermlineConsent(genomicAnalysis.tumorSpecificEvidence(), germlineReportingLevel);

        List<ProtectEvidence> filteredClinicalTrials =
                filterEvidenceForGermlineConsent(genomicAnalysis.clinicalTrials(), germlineReportingLevel);

        List<ProtectEvidence> filteredOffLabelEvidence =
                filterEvidenceForGermlineConsent(genomicAnalysis.offLabelEvidence(), germlineReportingLevel);

        return ImmutableGenomicAnalysis.builder()
                .from(genomicAnalysis)
                .reportableVariants(filteredVariants)
                .notifyGermlineStatusPerVariant(notifyPerVariant)
                .tumorSpecificEvidence(filteredTumorSpecificEvidence)
                .clinicalTrials(filteredClinicalTrials)
                .offLabelEvidence(filteredOffLabelEvidence)
                .build();
    }

    @NotNull
    @$VisibleForTesting
    static List<ReportableVariantWithNotify> filterVariants(@NotNull List<ReportableVariant> variants,
            @NotNull Map<ReportableVariant, Boolean> notifyGermlineStatusPerVariant,
            @NotNull Boolean germlineReportingLevel) {
        List<ReportableVariantWithNotify> filteredVariants = Lists.newArrayList();
        for (ReportableVariant variant : variants) {
            if (!(variant.source() == ReportableVariantSource.GERMLINE
                    && !germlineReportingLevel)) {
                if (variant.source() == ReportableVariantSource.GERMLINE && !notifyGermlineStatusPerVariant.get(variant)) {
                    filteredVariants.add(ImmutableReportableVariantWithNotify.builder()
                            .variant(ImmutableReportableVariant.builder().from(variant).source(ReportableVariantSource.SOMATIC).build())
                            .notifyVariant(false)
                            .build());
                } else {
                    filteredVariants.add(ImmutableReportableVariantWithNotify.builder()
                            .variant(variant)
                            .notifyVariant(notifyGermlineStatusPerVariant.get(variant))
                            .build());
                }
            }
        }
        return filteredVariants;
    }

    @NotNull
    @VisibleForTesting
    static List<ProtectEvidence> filterEvidenceForGermlineConsent(@NotNull List<ProtectEvidence> evidences,
            @NotNull Boolean germlineReportingLevel) {
        List<ProtectEvidence> filtered = Lists.newArrayList();
        for (ProtectEvidence evidence : evidences) {
            if (evidence.germline() && !germlineReportingLevel) {
                // We always overwrite to somatic in evidence since we are not sure that we notify about the actual variant.
                filtered.add(ImmutableProtectEvidence.builder().from(evidence).germline(false).build());
            } else if (!evidence.germline()) {
                filtered.add(evidence);
            }
        }

        return filtered;
    }
}
