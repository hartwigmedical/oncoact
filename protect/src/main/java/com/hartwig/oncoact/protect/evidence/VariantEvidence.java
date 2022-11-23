package com.hartwig.oncoact.protect.evidence;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.common.protect.EventGenerator;
import com.hartwig.oncoact.common.protect.ProtectEvidence;
import com.hartwig.oncoact.common.variant.CodingEffect;
import com.hartwig.oncoact.common.variant.DriverInterpretation;
import com.hartwig.oncoact.common.variant.ReportableVariant;
import com.hartwig.oncoact.common.variant.ReportableVariantFactory;
import com.hartwig.oncoact.common.variant.ReportableVariantSource;
import com.hartwig.oncoact.common.variant.SomaticVariant;
import com.hartwig.oncoact.common.variant.Variant;
import com.hartwig.oncoact.common.variant.VariantType;
import com.hartwig.oncoact.common.variant.impact.AltTranscriptReportableInfo;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.range.ActionableRange;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class VariantEvidence {

    private static final Logger LOGGER = LogManager.getLogger(VariantEvidence.class);

    @NotNull
    private final PersonalizedEvidenceFactory personalizedEvidenceFactory;
    @NotNull
    private final List<ActionableHotspot> hotspots;
    @NotNull
    private final List<ActionableRange> ranges;
    @NotNull
    private final List<ActionableGene> genes;

    public VariantEvidence(@NotNull final PersonalizedEvidenceFactory personalizedEvidenceFactory,
            @NotNull final List<ActionableHotspot> hotspots, @NotNull final List<ActionableRange> ranges,
            @NotNull final List<ActionableGene> genes) {
        this.personalizedEvidenceFactory = personalizedEvidenceFactory;
        this.hotspots = hotspots;
        this.ranges = ranges;
        this.genes = genes.stream()
                .filter(x -> x.event() == GeneEvent.ACTIVATION || x.event() == GeneEvent.INACTIVATION
                        || x.event() == GeneEvent.ANY_MUTATION)
                .collect(Collectors.toList());
    }

    @NotNull
    public List<ProtectEvidence> evidence(@NotNull List<ReportableVariant> germline, @NotNull List<ReportableVariant> somatic,
            @NotNull List<SomaticVariant> allSomaticVariants) {
        List<ProtectEvidence> evidences = Lists.newArrayList();
        for (ReportableVariant reportableVariant : ReportableVariantFactory.mergeVariantLists(germline, somatic)) {
            evidences.addAll(evidence(reportableVariant));
        }

        for (SomaticVariant allSomaticVariant : allSomaticVariants) {
            if (!allSomaticVariant.reported()) {
                evidences.addAll(evidence(allSomaticVariant));
            }
        }

        return evidences;
    }

    @NotNull
    private List<ProtectEvidence> evidence(@NotNull Variant variant) {
        boolean mayReport;
        DriverInterpretation driverInterpretation;

        if (variant instanceof ReportableVariant) {
            ReportableVariant reportable = (ReportableVariant) variant;
            mayReport = true;
            driverInterpretation = reportable.driverLikelihoodInterpretation();
        } else {
            mayReport = false;
            driverInterpretation = DriverInterpretation.LOW;
        }

        List<ProtectEvidence> evidences = Lists.newArrayList();
        for (ActionableHotspot hotspot : hotspots) {
            if (hotspotMatch(variant, hotspot)) {
                evidences.add(evidence(variant, hotspot, mayReport));
            }
        }

        for (ActionableRange range : ranges) {
            if (rangeMatch(variant, range)) {
                evidences.add(evidence(variant, range, mayReport && driverInterpretation == DriverInterpretation.HIGH));
            }
        }

        for (ActionableGene gene : genes) {
            if (geneMatch(variant, gene)) {
                evidences.add(evidence(variant, gene, mayReport && driverInterpretation == DriverInterpretation.HIGH));
            }
        }

        return evidences;
    }

    @NotNull
    private ProtectEvidence evidence(@NotNull Variant variant, @NotNull ActionableEvent actionable, boolean report) {
        boolean isGermline;
        DriverInterpretation driverInterpretation;
        String transcript;
        boolean isCanonical;

        if (variant instanceof ReportableVariant) {
            ReportableVariant reportable = (ReportableVariant) variant;

            isGermline = reportable.source() == ReportableVariantSource.GERMLINE;
            driverInterpretation = reportable.driverLikelihoodInterpretation();
            transcript = reportable.transcript();
            isCanonical = reportable.isCanonical();
        } else {
            isGermline = false;
            driverInterpretation = DriverInterpretation.LOW;
            transcript = variant.canonicalTranscript();
            isCanonical = true;
        }

        return personalizedEvidenceFactory.evidenceBuilder(actionable)
                .gene(variant.gene())
                .transcript(transcript)
                .isCanonical(isCanonical)
                .event(EventGenerator.variantEvent(variant))
                .germline(isGermline)
                .reported(report)
                .eventIsHighDriver(EvidenceDriverLikelihood.interpretVariant(driverInterpretation))
                .build();
    }

    private static boolean hotspotMatch(@NotNull Variant variant, @NotNull ActionableHotspot hotspot) {
        return variant.chromosome().equals(hotspot.chromosome()) && hotspot.position() == variant.position() && hotspot.ref()
                .equals(variant.ref()) && hotspot.alt().equals(variant.alt());
    }

    private static boolean rangeMatch(@NotNull Variant variant, @NotNull ActionableRange range) {
        return variant.chromosome().equals(range.chromosome()) && variant.gene().equals(range.gene()) && variant.position() >= range.start()
                && variant.position() <= range.end() && meetsMutationType(variant, range.applicableMutationType());
    }

    private static boolean geneMatch(@NotNull Variant variant, @NotNull ActionableGene gene) {
        assert gene.event() == GeneEvent.ACTIVATION || gene.event() == GeneEvent.INACTIVATION
                || gene.event() == GeneEvent.ANY_MUTATION;

        return gene.gene().equals(variant.gene()) && meetsMutationType(variant, MutationType.ANY);
    }

    private static boolean meetsMutationType(@NotNull Variant variant, @NotNull MutationType applicableMutationType) {
        CodingEffect effect;
        if (variant instanceof ReportableVariant) {
            ReportableVariant reportable = (ReportableVariant) variant;
            effect = reportable.isCanonical()
                    ? reportable.canonicalCodingEffect()
                    : AltTranscriptReportableInfo.firstOtherCodingEffect(reportable.otherReportedEffects());
        } else {
            effect = variant.canonicalCodingEffect();
        }

        switch (applicableMutationType) {
            case NONSENSE_OR_FRAMESHIFT:
                return effect == CodingEffect.NONSENSE_OR_FRAMESHIFT;
            case SPLICE:
                return effect == CodingEffect.SPLICE;
            case INFRAME:
                return effect == CodingEffect.MISSENSE && variant.type() == VariantType.INDEL;
            case INFRAME_DELETION:
                return effect == CodingEffect.MISSENSE && isDelete(variant);
            case INFRAME_INSERTION:
                return effect == CodingEffect.MISSENSE && isInsert(variant);
            case MISSENSE:
                return effect == CodingEffect.MISSENSE;
            case ANY:
                return effect == CodingEffect.MISSENSE || effect == CodingEffect.NONSENSE_OR_FRAMESHIFT || effect == CodingEffect.SPLICE;
            default: {
                LOGGER.warn("Unrecognized mutation type filter: '{}'", applicableMutationType);
                return false;
            }
        }
    }

    private static boolean isInsert(@NotNull Variant variant) {
        return variant.type() == VariantType.INDEL && variant.alt().length() > variant.ref().length();
    }

    private static boolean isDelete(@NotNull Variant variant) {
        return variant.type() == VariantType.INDEL && variant.alt().length() < variant.ref().length();
    }
}